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

public class BusTimesActivity extends ListActivity {
    private String busName;

    private class SetupBusTitlesAndTimes extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            return NextBusAPI.getJSON("http://runextbus.herokuapp.com/route/" + strings[0]);
        }

        protected void onPostExecute(String result) {
            String[][] titlesAndTimes = NextBusAPI.getBusStopTitlesAndTimes(result);
            setListView(titlesAndTimes[0], titlesAndTimes[1]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus_times);

        Intent intent = getIntent();
        busName = intent.getStringExtra(BusConstants.BUS_NAME_MESSAGE);
        String[] busStopTitles = intent.getStringArrayExtra(BusConstants.BUS_STOP_TITLES_MESSAGE);
        String[] busStopTimes = intent.getStringArrayExtra(BusConstants.BUS_STOP_TIMES_MESSAGE);

        setListView(busStopTitles, busStopTimes);

        // Setup refresh button
        Button refresh = (Button) findViewById(R.id.refreshTimes);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SetupBusTitlesAndTimes().execute(busName);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            new SetupBusTitlesAndTimes().execute(busName);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
