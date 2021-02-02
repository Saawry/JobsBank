package com.example.jobsbankbd;

import com.example.jobsbankbd.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private Toolbar mToolbar;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    private DatabaseReference rootRef, jobsRef;
    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //chipNavigationBar=findViewById(R.id.bottomChipBar);

        rootRef = FirebaseDatabase.getInstance().getReference();
        jobsRef = rootRef.child("All Jobs").getRef();
        final FragmentManager[] fragmentManager = {getSupportFragmentManager()};
        mToolbar = findViewById(R.id.main_app_bar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Jobs Bank BD");

        if (savedInstanceState == null) {
            FragmentManager frgm = getSupportFragmentManager();
            HotJobFragment hotJobFragment = new HotJobFragment();
            frgm.beginTransaction().replace(R.id.fragment_container, hotJobFragment).commit();
            binding.bottomChipBar.setItemSelected(R.id.new_job_menu, true);
        }
        binding.bottomChipBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.govt_job_menu:
                        fragment = new GovtJob();
                        break;
                    case R.id.new_job_menu:
                        fragment = new HotJobFragment();
                        break;
                    case R.id.private_job_menu:
                        fragment = new PrivateJob();
                        break;
                }
                if (fragment != null) {
                    fragmentManager[0] = getSupportFragmentManager();
                    fragmentManager[0].beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }
            }
        });


    }
}
