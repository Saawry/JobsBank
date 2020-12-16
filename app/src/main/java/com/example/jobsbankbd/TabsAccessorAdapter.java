package com.example.jobsbankbd;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    String [] text ={"Govt","Hot Jobs","Private"};
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){

            case 0:
                GovtJob fragment1 = new GovtJob();
                return fragment1;

            case 1:
                HotJobFragment fragment3 = new HotJobFragment();
                return fragment3;
            case 2:
                PrivateJob fragment2 = new PrivateJob();
                return fragment2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //return super.getPageTitle(position);
        switch (position){
            case 0:
                return text[position];
            case 1:
                return text[position];
            case 2:
                return text[position];
//            case 3:
//                return text[position];
            default:
                return null;
        }
    }
}
