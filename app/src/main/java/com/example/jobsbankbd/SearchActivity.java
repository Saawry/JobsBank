package com.example.jobsbankbd;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class SearchActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView toolBarTitle;

    private AlertDialog dialog;
    private FirebaseStorage imgStorageRef;

    private EditText etSearch;
    private Button btnSearch;
    private DatabaseReference jobRef;

    private String keyWord, category, searchType;
    private RecyclerView searchRecycler;
    private RadioGroup radioGroup;
    private RadioGroup radioGroup2;
    private RadioButton radioButton;
    private RadioButton radioButton2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        mToolbar = findViewById(R.id.search_job_toolbar);
        toolBarTitle = findViewById(R.id.toolbar_title);
        toolBarTitle.setText("Search Job");

        imgStorageRef = FirebaseStorage.getInstance();

        searchRecycler = findViewById(R.id.search_job_recycler);
        searchRecycler.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.search_btn);
        radioGroup = findViewById(R.id.radio_group);
        radioGroup2 = findViewById(R.id.radio_group2);


        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId2 = radioGroup2.getCheckedRadioButtonId();
                radioButton2 = findViewById(selectedId2);
                if (selectedId2 == R.id.radio_title) {
                    if (TextUtils.isEmpty(category)) {
                        etSearch.setHint(R.string.str_title);
                    }
                    searchType = "title";
                } else if (selectedId2 == R.id.radio_org) {
                    if (TextUtils.isEmpty(category)) {
                        etSearch.setHint(R.string.str_org);
                    }
                    searchType = "org";
                }
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectedId);
                if (selectedId == R.id.radio_govt) {
                    category = "Govt";
                } else if (selectedId == R.id.radio_private) {
                    category = "Private";
                }


                keyWord = etSearch.getText().toString();
                if (TextUtils.isEmpty(keyWord)) {
                    Toast.makeText(SearchActivity.this, "Enter Keyword", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(category)) {
                    Toast.makeText(SearchActivity.this, "Select Job Category", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(searchType)) {
                    Toast.makeText(SearchActivity.this, "Select Job Title/Org", Toast.LENGTH_SHORT).show();
                } else {
                    LoadData();
                }
            }
        });
    }

    private void LoadData() {

        jobRef = FirebaseDatabase.getInstance().getReference().child(category);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<JobInfo>().setQuery(jobRef, JobInfo.class).build();


        final FirebaseRecyclerAdapter<JobInfo, SearchJobsViewholder> adapter
                = new FirebaseRecyclerAdapter<JobInfo, SearchJobsViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SearchJobsViewholder holder, int position, @NonNull JobInfo model) {


                //-------------------------

                final String list_user_id = getRef(position).getKey();

                DatabaseReference getRequestTypeRef;
                if (searchType.equals("org")) {
                    getRequestTypeRef=getRef(position).child("organization").getRef();
                } else {
                    getRequestTypeRef=getRef(position).child("title").getRef();
                }

                getRequestTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String type = dataSnapshot.getValue().toString();
                            if (type.contains(keyWord)) {

                                jobRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                        String title = dataSnapshot.child("title").getValue().toString();
                                        String org = dataSnapshot.child("organization").getValue().toString();
                                        String deadline = dataSnapshot.child("deadline").getValue().toString();
                                        String vacancy = dataSnapshot.child("vacancy").getValue().toString();
                                        final String imgUrl = dataSnapshot.child("imgUrl").getValue().toString();


                                        holder.Tv1.setVisibility(View.VISIBLE);
                                        holder.deleteBtn.setVisibility(View.VISIBLE);
                                        holder.Tv2.setVisibility(View.VISIBLE);
                                        holder.Tv3.setVisibility(View.VISIBLE);
                                        holder.Tv4.setVisibility(View.VISIBLE);
                                        holder.Tv5.setVisibility(View.VISIBLE);

                                        int vcn = Integer.parseInt(vacancy);
                                        vcn = 500 - vcn;
                                        vacancy = String.valueOf(vcn);

                                        DaysConvert daysConvert = new DaysConvert();
                                        holder.TvDaysRemain.setText(daysConvert.daysRemain(deadline));

                                        holder.TvTitle.setText(title);
                                        holder.TvOrg.setText(org);
                                        holder.TvVacancy.setText(vacancy);
                                        holder.TvDeadline.setText(deadline);

                                        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DeleteItem(list_user_id,imgUrl);
                                            }
                                        });

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DownloadImage(list_user_id, category);
                                            }
                                        });

                                    }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public SearchJobsViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_jobs_view_layout, viewGroup, false);

                SearchJobsViewholder searchjobviewHolder = new SearchJobsViewholder(view);
                return searchjobviewHolder;

            }
        };

        searchRecycler.setAdapter(adapter);
        adapter.startListening();

    }

    private void DeleteItem(final String key,String imgUrl) {

        showLoadingBar();
        StorageReference photoRef = imgStorageRef.getReferenceFromUrl(imgUrl);

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                DatabaseReference deleteJobRef= FirebaseDatabase.getInstance().getReference().child(category);
                deleteJobRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissLoadingBar();
                        Toast.makeText(SearchActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissLoadingBar();
                        Toast.makeText(SearchActivity.this, "Error.!"+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                dismissLoadingBar();
                Toast.makeText(SearchActivity.this, "Error.! "+exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showLoadingBar() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("On Progress ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();

        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    private void dismissLoadingBar() {
        dialog.cancel();
    }
    private void DownloadImage(String key, String category) {
        Intent showImg = new Intent(SearchActivity.this, DownloadImage.class);
        showImg.putExtra("key", key);
        showImg.putExtra("category", category);
        startActivity(showImg);
    }

    public static class SearchJobsViewholder extends RecyclerView.ViewHolder {

        TextView TvTitle, TvOrg, TvDeadline, TvVacancy, TvDaysRemain, Tv1, Tv2, Tv3, Tv4, Tv5;

        Button deleteBtn;

        public SearchJobsViewholder(@NonNull View itemView) {
            super(itemView);

            deleteBtn = itemView.findViewById(R.id.sjvl_delete_btn);

            TvDaysRemain = itemView.findViewById(R.id.tv_days_remaining);
            TvDeadline = itemView.findViewById(R.id.tv_deadline);
            TvOrg = itemView.findViewById(R.id.tv_org);
            TvTitle = itemView.findViewById(R.id.tv_title);
            TvVacancy = itemView.findViewById(R.id.tv_vacancy);
            Tv1 = itemView.findViewById(R.id.tv1);
            Tv2 = itemView.findViewById(R.id.tv2);
            Tv3 = itemView.findViewById(R.id.tv3);
            Tv4 = itemView.findViewById(R.id.tv4);
            Tv5 = itemView.findViewById(R.id.tv5);

        }
    }
}
