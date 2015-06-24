package me.rutgersdirect.rudirect.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.activity.MainActivity;
import me.rutgersdirect.rudirect.adapter.BusRouteAdapter;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.ui.view.DividerItemDecoration;

public class ActiveRoutesFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private MainActivity mainActivity;
    private TextView errorView;
    private RecyclerView activeBusesRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppBarLayout appBarLayout;

    private class UpdateRecyclerViewTask extends AsyncTask<Void, Void, String[]> {
        protected String[] doInBackground(Void... voids) {
            return NextBusAPI.getActiveBusTags();
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }

        protected void onPostExecute(String[] activeBusTags) {
            // Fill active bus array with active bus names
            String[] activeBuses = new String[activeBusTags.length];
            SharedPreferences tagsToBusesPref = mainActivity.getSharedPreferences(getString(R.string.tags_to_buses_key), Context.MODE_PRIVATE);
            for (int i = 0; i < activeBusTags.length; i++) {
                activeBuses[i] = tagsToBusesPref.getString(activeBusTags[i], "Offline");
            }
            if (activeBusTags.length == 1 && activeBuses[0].equals("Offline")) {
                // Setup error message
                errorView.setVisibility(View.VISIBLE);
                activeBusesRecyclerView.setAdapter(new BusRouteAdapter());
                if (isNetworkAvailable()) {
                    errorView.setText("No active buses.");
                } else {
                    errorView.setText("Unable to get active buses - check your Internet connection and try again.");
                }
            } else {
                // Show active buses
                errorView.setVisibility(View.GONE);
                activeBusesRecyclerView.setAdapter(new BusRouteAdapter(activeBuses, mainActivity, ActiveRoutesFragment.this));
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    // Sets up the list view
    public void updateRecyclerView() {
        new UpdateRecyclerViewTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_active_routes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSwipeRefreshLayout();
        errorView = (TextView) mainActivity.findViewById(R.id.active_buses_error);
        appBarLayout = (AppBarLayout) mainActivity.findViewById(R.id.appbar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateRecyclerView();
    }

    // Set up RecyclerView
    private void setupRecyclerView() {
        // Initialize recycler view
        activeBusesRecyclerView = (RecyclerView) mainActivity.findViewById(R.id.active_buses_recyclerview);
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        activeBusesRecyclerView.setLayoutManager(layoutManager);
        // Setup layout
        activeBusesRecyclerView.addItemDecoration(new DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL));
        // Set adapter
        activeBusesRecyclerView.setAdapter(new BusRouteAdapter());
    }

    // Set up SwipeRefreshLayout
    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mainActivity.findViewById(R.id.active_buses_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecyclerView();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_active_routes, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            mSwipeRefreshLayout.setRefreshing(true);
            updateRecyclerView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }
}