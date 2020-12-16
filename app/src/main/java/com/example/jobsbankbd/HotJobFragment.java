package com.example.jobsbankbd;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class HotJobFragment extends Fragment {

    private DatabaseReference HotJobRef;

    private View HotJobView;
    private RecyclerView HotJobRecycler;

    public HotJobFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        HotJobView = inflater.inflate(R.layout.fragment_hot_job, container, false);

        HotJobRecycler = HotJobView.findViewById(R.id.hot_job_recycler);
        HotJobRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        HotJobRef = FirebaseDatabase.getInstance().getReference().child("Hot");
        return HotJobView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Query topVacancy = HotJobRef.orderByChild("vacancy");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<JobInfo>().setQuery(topVacancy, JobInfo.class).build();


        final FirebaseRecyclerAdapter<JobInfo, HotJobsViewHolder> adapter
                = new FirebaseRecyclerAdapter<JobInfo, HotJobsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HotJobsViewHolder holder, int position, @NonNull JobInfo model) {

                final String jobKeys = getRef(position).getKey();

                HotJobRef.child(jobKeys).addValueEventListener(new ValueEventListener() {
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
            public HotJobsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.jobs_view_layout, viewGroup,false);
                HotJobsViewHolder govtJobviewHolder = new HotJobsViewHolder(view);
                return govtJobviewHolder;

            }
        };

        HotJobRecycler.setAdapter(adapter);
        adapter.startListening();
    }
    private void DownloadImage(String key) {
        Intent showImg = new Intent(getActivity(), DownloadImage.class);
        showImg.putExtra("key",key);
        showImg.putExtra("category","Hot");
        startActivity(showImg);
    }
    public static class HotJobsViewHolder extends RecyclerView.ViewHolder{

        TextView TvTitle, TvOrg,TvDeadline,TvVacancy,TvDaysRemain;




        public HotJobsViewHolder(@NonNull View itemView) {
            super(itemView);
            TvDaysRemain = itemView.findViewById(R.id.tv_days_remaining);
            TvDeadline = itemView.findViewById(R.id.tv_deadline);
            TvOrg = itemView.findViewById(R.id.tv_org);
            TvTitle = itemView.findViewById(R.id.tv_title);
            TvVacancy = itemView.findViewById(R.id.tv_vacancy);



        }
    }


}
