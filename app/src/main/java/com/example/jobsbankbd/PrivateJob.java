package com.example.jobsbankbd;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
public class PrivateJob extends Fragment {


    private DatabaseReference PrivateJobRef;

    private View PrivateJobView;
    private RecyclerView PrivateJobRecycler;
    public PrivateJob() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateJobView = inflater.inflate(R.layout.fragment_private_jobs, container, false);

        PrivateJobRecycler = PrivateJobView.findViewById(R.id.private_job_recycler);
        PrivateJobRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        PrivateJobRef = FirebaseDatabase.getInstance().getReference().child("Private");
        return PrivateJobView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query topVacancy = PrivateJobRef.orderByChild("vacancy");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<JobInfo>().setQuery(topVacancy, JobInfo.class).build();


        final FirebaseRecyclerAdapter<JobInfo, PrivateJobsViewHolder> adapter
                = new FirebaseRecyclerAdapter<JobInfo, PrivateJobsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PrivateJobsViewHolder holder, int position, @NonNull JobInfo model) {

                final String jobKeys = getRef(position).getKey();

                PrivateJobRef.child(jobKeys).addValueEventListener(new ValueEventListener() {
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

                            DaysConvert daysConvert=new DaysConvert();
                            holder.TvDaysRemain.setText(daysConvert.daysRemain(deadline));

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
            public PrivateJobsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.jobs_view_layout, viewGroup,false);
                PrivateJobsViewHolder govtJobviewHolder = new PrivateJobsViewHolder(view);
                return govtJobviewHolder;

            }
        };

        PrivateJobRecycler.setAdapter(adapter);
        adapter.startListening();
    }
    private void DownloadImage(String key) {
        Intent showImg = new Intent(getActivity(), DownloadImage.class);
        showImg.putExtra("key",key);
        showImg.putExtra("category","Private");
        startActivity(showImg);
    }
    public static class PrivateJobsViewHolder extends RecyclerView.ViewHolder{

        TextView TvTitle, TvOrg,TvDeadline,TvVacancy,TvDaysRemain;




        public PrivateJobsViewHolder(@NonNull View itemView) {
            super(itemView);
            TvDaysRemain = itemView.findViewById(R.id.tv_days_remaining);
            TvDeadline = itemView.findViewById(R.id.tv_deadline);
            TvOrg = itemView.findViewById(R.id.tv_org);
            TvTitle = itemView.findViewById(R.id.tv_title);
            TvVacancy = itemView.findViewById(R.id.tv_vacancy);



        }
    }
}
