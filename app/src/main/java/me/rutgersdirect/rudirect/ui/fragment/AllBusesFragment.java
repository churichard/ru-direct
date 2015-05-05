package me.rutgersdirect.rudirect.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.helper.ShowBusStopsHelper;
import me.rutgersdirect.rudirect.ui.activity.BusStopsActivity;
import me.rutgersdirect.rudirect.ui.activity.MainActivity;

public class AllBusesFragment extends Fragment {
    private MainActivity mainActivity;
    private ListView listView;

    // Sets up the all buses list view
    public void setupListView() {
        // Setup list view of all buses
        if (getView() != null) {
            listView = (ListView) getView().findViewById(R.id.allBusesList);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity.getApplicationContext(),
                R.layout.list_black_text, R.id.list_content, BusConstants.allBusNames);
        listView.setAdapter(adapter);

        // Setup item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                if (!BusStopsActivity.active) {
                    BusStopsActivity.active = true;
                    String bus = (String) (listView.getItemAtPosition(myItemInt));
                    SharedPreferences busesToTagsPref = mainActivity.getSharedPreferences(getString(R.string.buses_to_tags_key), Context.MODE_PRIVATE);
                    String busTag = busesToTagsPref.getString(bus, null);
                    new ShowBusStopsHelper().execute(busTag, mainActivity, mainActivity.getApplicationContext());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) super.getActivity();
        RelativeLayout rlLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_all_buses, container, false);

        setHasOptionsMenu(true);

        return rlLayout;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_all_buses, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
