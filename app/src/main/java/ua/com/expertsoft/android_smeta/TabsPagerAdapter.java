package ua.com.expertsoft.android_smeta;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.fragments.DetailFragment;
import ua.com.expertsoft.android_smeta.fragments.ResourcesFragment;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;

/*
 * Created by mityai on 04.01.2016.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    FragmentManager manager;
    Context context;
    ArrayList<Fragment> fragmentList;

    public TabsPagerAdapter(FragmentManager fm, Context ctx, Works work){
        super(fm);
        manager = fm;
        context = ctx;
        fragmentList = new ArrayList<>();
        Fragment frg;
        Bundle b = new Bundle();
        b.putSerializable("adapterWork",work);
        frg = new DetailFragment();
        frg.setArguments(b);
        fragmentList.add(frg);
        if (!SelectedWork.work.getWRec().equals("machine") & !SelectedWork.work.getWRec().equals("resource")) {
            frg = new ResourcesFragment();
            frg.setArguments(b);
            fragmentList.add(frg);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return context.getResources().getString(R.string.tab_detail);
            case 1:
                return context.getResources().getString(R.string.tab_consists);
        }
        return null;
    }
}
