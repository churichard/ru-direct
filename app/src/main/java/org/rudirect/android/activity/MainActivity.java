package org.rudirect.android.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.analytics.HitBuilders;

import org.rudirect.android.R;
import org.rudirect.android.adapter.MainPagerAdapter;
import org.rudirect.android.data.constants.RUDirectApplication;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        if (viewPager != null) {
            viewPager.setAdapter(new MainPagerAdapter(getFragmentManager()));
            viewPager.setOffscreenPageLimit(2);
        }

        // Set up tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        if (tabLayout != null) tabLayout.setupWithViewPager(viewPager);

        // Log the screen
        RUDirectApplication.getTracker().setScreenName(getString(R.string.main_screen));
        RUDirectApplication.getTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }
}