package com.pc.kaizer.netbank;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FDRFragment extends Fragment {

    //private ViewPager viewPager;
    private FDRTabsAdapter mAdapter;
    private android.app.ActionBar actionBar;
    private String[] tabs = {"Create FDR", "View FDR"};

    public FDRFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("FDR");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_fdr, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.Toolbar);
        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Create FDR"));
        tabLayout.addTab(tabLayout.newTab().setText("View FDR"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) v.findViewById(R.id.pager);
        mAdapter = new FDRTabsAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return v;
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getActivity().getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Tabs Adapter in the same file. Do not change
    public class FDRTabsAdapter extends FragmentPagerAdapter {

        public FDRTabsAdapter(FragmentManager fm, int tabCount){
            super(fm);
        }

        @Override
        public Fragment getItem(int index){
            switch (index) {
                case 0:
                    CreateFDRFragment cFDR = new CreateFDRFragment();
                    return cFDR;

                case 1:
                    ViewFDRFragment vFDR = new ViewFDRFragment();
                    return vFDR;

            }
            return null;
        }

        @Override
        public int getCount(){
            return 2;
        }
    }

}
