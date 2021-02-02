package com.example.jobsbankbd;

import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DeleteJob extends AppCompatActivity {

    private TextView toolBarTitle;
    private Toolbar mToolbar;

    private Button findBtn;
    private DatabaseReference jobRef;

    private Spinner ctgSpinner;
    private String category;
    private RecyclerView deleteRecycler;

    private AlertDialog dialog;
    private FirebaseStorage imgStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_job);


        mToolbar=findViewById(R.id.delete_job_toolbar);
        toolBarTitle=findViewById(R.id.toolbar_title);
        toolBarTitle.setText("Delete Job");

        imgStorageRef = FirebaseStorage.getInstance();

        deleteRecycler = findViewById(R.id.delete_job_recycler);
        deleteRecycler.setLayoutManager(new LinearLayoutManager(DeleteJob.this));


        findBtn = findViewById(R.id.find_btn);
        ctgSpinner = findViewById(R.id.delete_spinner_category);
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category=ctgSpinner.getSelectedItem().toString();
                if (category.equals("Category")) {
                    Toast.makeText(DeleteJob.this, "Select Job Category", Toast.LENGTH_SHORT).show();
                } else {
                    LoadData();
                }
            }
        });

    }

    private void LoadData() {

        jobRef = FirebaseDatabase.getInstance().getReference().child(category);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<JobInfo>().setQuery(jobRef, JobInfo.class).build();


        final FirebaseRecyclerAdapter<JobInfo, DeleteJobsViewholder> adapter
                = new FirebaseRecyclerAdapter<JobInfo, DeleteJobsViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final DeleteJobsViewholder holder, int position, @NonNull JobInfo model) {


                //-------------------------

                final String list_user_id = getRef(position).getKey();

                DatabaseReference getRequestTypeRef=getRef(position).child("deadline").getRef();


                getRequestTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String date = dataSnapshot.getValue().toString();

                            DaysConvert daysConvert = new DaysConvert();
                            String res = daysConvert.daysRemain(date);

                            if (res.equals("-_-")) {

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

                                            holder.TvDaysRemain.setText("-_-");

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
            public DeleteJobsViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_jobs_view_layout, viewGroup, false);

                DeleteJobsViewholder searchjobviewHolder = new DeleteJobsViewholder(view);
                return searchjobviewHolder;

            }
        };

        deleteRecycler.setAdapter(adapter);
        adapter.startListening();

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
                        Toast.makeText(DeleteJob.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissLoadingBar();
                        Toast.makeText(DeleteJob.this, "Error.!"+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                dismissLoadingBar();
                Toast.makeText(DeleteJob.this, "Error.! "+exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void DownloadImage(String key, String category) {
        Intent showImg = new Intent(DeleteJob.this, DownloadImage.class);
        showImg.putExtra("key", key);
        showImg.putExtra("category", category);
        startActivity(showImg);
    }

    public static class DeleteJobsViewholder extends RecyclerView.ViewHolder {

        TextView TvTitle, TvOrg, TvDeadline, TvVacancy, TvDaysRemain, Tv1, Tv2, Tv3, Tv4, Tv5;

        Button deleteBtn;

        public DeleteJobsViewholder(@NonNull View itemView) {
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
