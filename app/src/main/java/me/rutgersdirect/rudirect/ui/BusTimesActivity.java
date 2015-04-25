package me.rutgersdirect.rudirect.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import me.rutgersdirect.rudirect.R;
import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.api.NextBusAPI;
import me.rutgersdirect.rudirect.helper.ShowBusStopsAndTimesHelper;

public class BusTimesActivity extends ListActivity {
    private String busTag;

//    private class SetupBusTitlesAndTimes extends AsyncTask<String, Void, String[][]> {
//        protected String[][] doInBackground(String... strings) {
//            String tag = strings[0];
//            String[][] busStopTitlesAndTimes = {NextBusAPI.getBusStopTitles(tag), NextBusAPI.getBusStopTimes(tag)};
//            return busStopTitlesAndTimes;
//        }
//
//        protected void onPostExecute(String[][] titlesAndTimes) {
//            setListView(titlesAndTimes[0], titlesAndTimes[1]);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus_times);

        Intent intent = getIntent();
        busTag = intent.getStringExtra(BusConstants.BUS_TAG_MESSAGE);
        String[] busStopTitles = intent.getStringArrayExtra(BusConstants.BUS_STOP_TITLES_MESSAGE);
        String[] busStopTimes = intent.getStringArrayExtra(BusConstants.BUS_STOP_TIMES_MESSAGE);

        setListView(busStopTitles, busStopTimes);

        // Setup refresh button
        Button refresh = (Button) findViewById(R.id.refreshTimes);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new ShowBusStopsAndTimesHelper().execute(busTag, BusTimesActivity.this);
            }
        });
    }

    public void setListView(String[] titles, String[] times) {
        String[] buses = new String[titles.length];

        for (int i = 0; i < buses.length; i++) {
            if (times == null) {
                buses[i] = "No predictions available";
            }
            else {
                buses[i] = titles[i] + "\n" + times[i];
            }
        }

        ListView busTimesList = (ListView) findViewById(android.R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                 R.layout.list_black_text, R.id.list_content, buses);
        busTimesList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bus_times, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.refresh) {
            new ShowBusStopsAndTimesHelper().execute(busTag, BusTimesActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
