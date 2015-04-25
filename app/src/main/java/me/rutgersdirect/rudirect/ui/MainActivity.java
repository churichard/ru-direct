package me.rutgersdirect.rudirect.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import hackru2015s.ru_direct.R;
import me.rutgersdirect.rudirect.BusArrays;
import me.rutgersdirect.rudirect.BusConstants;
import me.rutgersdirect.rudirect.helper.SetupBusStopsAndTimes;
import me.rutgersdirect.rudirect.api.NextBusAPI;

public class MainActivity extends ActionBarActivity {
    private ListView listView;

    private class SetupListViewTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            return NextBusAPI.getJSON("http://runextbus.herokuapp.com/active");
        }

        protected void onPostExecute(String result) {
            String[][] tagsAndTitles = NextBusAPI.getActiveBusTagsAndTitles(result);
            BusArrays.ActiveBusTags = tagsAndTitles[0];
            BusArrays.ActiveBusTitles = tagsAndTitles[1];

            // Setup list view
            listView = (ListView) findViewById(R.id.busList);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                    R.layout.list_black_text, R.id.list_content, tagsAndTitles[1]);
            listView.setAdapter(adapter);

            // Setup item click listener
            listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                    String bus = (String) (listView.getItemAtPosition(myItemInt));
                    String busTag = BusArrays.ActiveBusTags[(java.util.Arrays.asList(BusArrays.ActiveBusTitles).indexOf(bus))];
                    new SetupBusStopsAndTimes().execute(busTag, MainActivity.this);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("RU Direct");

        // Setup the list view
        new SetupListViewTask().execute();

        // Setup refresh button
        final Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SetupListViewTask().execute();
            }
        });

        // Setup all buses button
        final Button allBuses = (Button) findViewById(R.id.allBuses);
        allBuses.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Start new activity to display all buses
                Intent intent = new Intent(MainActivity.this, AllBusesActivity.class);
                intent.putExtra(BusConstants.ACTIVE_BUSES_MESSAGE, BusArrays.ActiveBusTitles);
                intent.putExtra(BusConstants.ACTIVE_BUS_TAGS, BusArrays.ActiveBusTags);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            new SetupListViewTask().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
