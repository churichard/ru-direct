package org.rudirect.android.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.rudirect.android.R;
import org.rudirect.android.adapter.RoutePagerAdapter;
import org.rudirect.android.data.constants.AppData;
import org.rudirect.android.data.constants.RUDirectApplication;
import org.rudirect.android.data.model.BusRoute;

public class RouteActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 5001;
    private static boolean firstMapLoad = true;

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    private BusRoute route;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        // Get intent extras
        Intent intent = getIntent();
        String busTag = intent.getStringExtra(getString(R.string.bus_tag_message));
        route = RUDirectApplication.getBusData().getBusTagsToBusRoutes().get(busTag);
        String pageClickedFrom = intent.getStringExtra(getString(R.string.page_clicked_from_message));

        setTitle(route.getTitle());
        setupToolbar();
        setupViewPagerAndTabLayout();

        mGoogleApiClient = buildGoogleApiClient();

        // Log the screen
        RUDirectApplication.getTracker().setScreenName(getString(R.string.bus_stops_screen));
        RUDirectApplication.getTracker().send(new HitBuilders.ScreenViewBuilder()
                .setCustomDimension(AppData.ROUTE_NAME_DIMEN, route.getTitle())
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
        final ViewPager viewPager = (ViewPager) findViewById(R.id.route_viewpager);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Setup tabs
        for (int i = 0; i < RoutePagerAdapter.NUM_OF_ITEMS; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(RoutePagerAdapter.TITLES[i]);
            tabLayout.addTab(tab);
        }

        // Setup on tab selected listener
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                viewPager.setCurrentItem(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { /* Do nothing */ }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { /* Do nothing */ }
        });

        if (firstMapLoad) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    firstMapLoad = false;
                    viewPager.setAdapter(new RoutePagerAdapter(getFragmentManager()));
                }
            }, 100);
        } else {
            viewPager.setAdapter(new RoutePagerAdapter(getFragmentManager()));
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    public BusRoute getRoute() {
        return route;
    }

    // Build Google Api Client for displaying maps
    private synchronized GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) { /* Do nothing */ }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    // Connection to Google Play Services failed
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mResolvingError && result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        }
    }
}