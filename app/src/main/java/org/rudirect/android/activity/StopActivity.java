package org.rudirect.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;

import org.rudirect.android.R;
import org.rudirect.android.adapter.StopPagerAdapter;
import org.rudirect.android.data.constants.AppData;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusStop;

public class StopActivity extends AppCompatActivity {

    private BusStop stop;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

        // Get intent extras
        Intent intent = getIntent();
        String stopTag = intent.getStringExtra(getString(R.string.stop_tag_message));
        stop = RUDirectApplication.getBusData().getStopTagsToBusStops().get(stopTag);
        String pageClickedFrom = intent.getStringExtra(getString(R.string.page_clicked_from_message));

        setTitle(stop.getTitle());
        setupToolbar();
        setupViewPagerAndTabLayout();

        // Log the screen
        RUDirectApplication.getTracker().setScreenName(getString(R.string.stop_screen));
        RUDirectApplication.getTracker().send(new HitBuilders.ScreenViewBuilder()
                .setCustomDimension(AppData.ROUTE_OR_STOP_NAME_DIMEN, stop.getTitle())
                .setCustomDimension(AppData.PAGE_CLICKED_FROM_DIMEN, pageClickedFrom).build());
    }

    // Setup toolbar
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_action_toolbar_back, getTheme()));
        }
    }

    // Setup viewpager and tab layout
    private void setupViewPagerAndTabLayout() {
        // Set up viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.stop_viewpager);
        viewPager.setAdapter(new StopPagerAdapter(getFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        // Set up tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public BusStop getStop() {
        return stop;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.abc_shrink_fade_out_from_bottom);
    }
}