package com.javgon.wakeme.Activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toolbar;

import com.javgon.wakeme.Fragments.MainFragment;
import com.javgon.wakeme.Fragments.UserListFragment;
import com.javgon.wakeme.R;

public class NavigationPage extends AppCompatActivity implements View.OnClickListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FloatingActionButton fabLeft,fabCenter,fabRight;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_page);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1); //begin in middle
        mViewPager.addOnPageChangeListener(new CustomOnPageChangeListener());
        //set up floating action bars
        fabLeft=(FloatingActionButton) findViewById(R.id.fab_left);
        fabRight=(FloatingActionButton)findViewById(R.id.fab_right);
        fabCenter= (FloatingActionButton)findViewById(R.id.fab_center);

        fabLeft.setOnClickListener(this);
        fabCenter.setOnClickListener(this);
        fabRight.setOnClickListener(this);


        /*
            background transformation by http://kubaspatny.github.io/2014/09/18/viewpager-background-transition/
         */
        // (3 - 1) = number of pages minus 1
        setUpColors();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_left:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.fab_center:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.fab_right:
                mViewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
    }
    private void setUpColors(){

        Integer color1 = ContextCompat.getColor(getApplicationContext(),R.color.colorOrange);
        Integer color2 = ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary);
        Integer color3 = ContextCompat.getColor(getApplicationContext(),R.color.colorTransparent);

        Integer[] colors_temp = {color1, color2, color3};
        colors = colors_temp;

    }
    private class CustomOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(position < (mSectionsPagerAdapter.getCount() -1) && position < (colors.length - 1)) {

                mViewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));

            } else {

                // the last page color
                mViewPager.setBackgroundColor(colors[colors.length - 1]);

            }        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return UserListFragment.newInstance();
                case 1:
                    return MainFragment.newInstance();
                case 2:
                    return UserListFragment.newInstance();
                default:
                    return UserListFragment.newInstance();
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }


    }
}
