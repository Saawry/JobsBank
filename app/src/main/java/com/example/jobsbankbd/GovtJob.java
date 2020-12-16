package com.example.jobsbankbd;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class GovtJob extends Fragment {

    private DatabaseReference GovtJobsRef;


    private View GovTJobView;
    private RecyclerView GovtJobRecycler;
    private FloatingActionButton fab;
    public GovtJob() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        GovTJobView = inflater.inflate(R.layout.fragment_govt_job, container, false);

        GovtJobRecycler = GovTJobView.findViewById(R.id.govt_job_recycler);
        GovtJobRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //GovtJobRecycler.setLayoutManager(new GridLayoutManager(getContext(),2));

        GovtJobsRef= FirebaseDatabase.getInstance().getReference().child("Govt");


        fab = GovTJobView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                CharSequence options[] = new CharSequence[]{
                        "Add Job",
                        "Delete Job",
                        "Search Job"
                };





                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
                builder.setTitle("Chose Option");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            Intent addJobIntent = new Intent(getActivity(), AddNewJob.class);
                            startActivity(addJobIntent);
                        }
                        if (which==1){
                            Intent dltIntent = new Intent(getActivity(), DeleteJob.class);
                            startActivity(dltIntent);
                        }
                        if (which==2){
                            Intent srchIntent = new Intent(getActivity(), SearchActivity.class);
                            startActivity(srchIntent);
                        }
                    }
                });


                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                builder.show();


            }
        });
        return GovTJobView;
    }


    @Override
    public void onStart() {
        super.onStart();

        Query topVacancy = GovtJobsRef.orderByChild("vacancy");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<JobInfo>().setQuery(topVacancy, JobInfo.class).build();


        final FirebaseRecyclerAdapter<JobInfo, GovtJobsViewholder> adapter
                = new FirebaseRecyclerAdapter<JobInfo, GovtJobsViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final GovtJobsViewholder holder, int position, @NonNull JobInfo model) {


                final String jobKeys = getRef(position).getKey();

                GovtJobsRef.child(jobKeys).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            String title = dataSnapshot.child("title").getValue().toString();
                            String org = dataSnapshot.child("organization").getValue().toString();
                            String deadline = dataSnapshot.child("deadline").getValue().toString();
                            String vacancy = dataSnapshot.child("vacancy").getValue().toString();

                            int vcn=Integer.parseInt(vacancy);
                            vcn=500-vcn;
                            vacancy=String.valueOf(vcn);
                            //-----------
                            DaysConvert daysConvert=new DaysConvert();
                            holder.TvDaysRemain.setText(daysConvert.daysRemain(deadline));
                            //-----------
                            holder.TvTitle.setText(title);
                            holder.TvOrg.setText(org);
                            holder.TvVacancy.setText(vacancy);
                            holder.TvDeadline.setText(deadline);
                        }

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DownloadImage(jobKeys);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public GovtJobsViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.jobs_view_layout, viewGroup,false);

                GovtJobsViewholder govtjobviewHolder = new GovtJobsViewholder(view);
                return govtjobviewHolder;

            }
        };

        GovtJobRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void DownloadImage( String key) {
        Intent showImg = new Intent(getActivity(), DownloadImage.class);
        showImg.putExtra("key",key);
        showImg.putExtra("category","Govt");
        startActivity(showImg);
    }

    public static class GovtJobsViewholder extends RecyclerView.ViewHolder{

        TextView TvTitle, TvOrg,TvDeadline,TvVacancy,TvDaysRemain;

        public GovtJobsViewholder(@NonNull View itemView) {
            super(itemView);

            TvDaysRemain = itemView.findViewById(R.id.tv_days_remaining);
            TvDeadline = itemView.findViewById(R.id.tv_deadline);
            TvOrg = itemView.findViewById(R.id.tv_org);
            TvTitle = itemView.findViewById(R.id.tv_title);
            TvVacancy = itemView.findViewById(R.id.tv_vacancy);



        }
    }

}
