package hackru2015s.ru_direct;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class AllBusesActivity extends ActionBarActivity {
    private final String[] allBuses = {"A", "B", "C", "EE", "F", "H", "LX", "Rex B", "Rex L", "Weekend 1", "Weekend 2"};
    private final String[] allBusTags = {"a", "b", "c", "ee", "f", "h", "lx", "rexb", "rexl", "wknd1", "wknd2"};
    ListView LV;
    String[] activeBusTags;
    String[] activeBusTitles;

    private class SetupBusPredictions extends AsyncTask<String, Void, String> {
        private String busName;

        protected String doInBackground(String... strings) {
            busName = strings[0];
            return MainActivity.getJSON("http://runextbus.herokuapp.com/route/" + busName);
        }

        protected void onPostExecute(String result) {
            ArrayList<String> busStopTitles = new ArrayList<String>();
            ArrayList<String> busStopTimes = new ArrayList<String>();
            try {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {
                    String allTimes = "";
                    JSONObject stopObject = jArray.getJSONObject(i);
                    busStopTitles.add(stopObject.getString("title"));
                    if (stopObject.getString("predictions") != "null") {
                        JSONArray predictions = stopObject.getJSONArray("predictions");
                        for (int j = 0; j < predictions.length(); j++) {
                            JSONObject times = predictions.getJSONObject(j);
                            String min = times.getString("minutes");
                            if (min.equals("0"))
                                min = "<1";
                            allTimes += min;
                            if (j != predictions.length() - 1) {
                                allTimes += ", ";
                            }
                        }
                        allTimes += " minutes";
                    }
                    else {
                        allTimes = "Offline";
                    }
                    busStopTimes.add(allTimes);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Start new activity to display bus times
            Intent intent = new Intent(AllBusesActivity.this, BusTimesActivity.class);
            intent.putExtra(MainActivity.BUS_NAME, busName);
            intent.putExtra(MainActivity.BUS_STOP_TITLES_MESSAGE, busStopTitles.toArray(new String[busStopTitles.size()]));
            intent.putExtra(MainActivity.BUS_STOP_TIMES_MESSAGE, busStopTimes.toArray(new String[busStopTimes.size()]));
            AllBusesActivity.this.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_buses);

        Intent intent = getIntent();
        activeBusTags = intent.getStringArrayExtra(MainActivity.ACTIVE_BUS_TAGS);
        activeBusTitles = intent.getStringArrayExtra(MainActivity.ACTIVE_BUSES_MESSAGE);

        for (int i = 0; i < activeBusTitles.length; i++) {
//            if (activeBusTitles[i].equals("a")) {
//
//            }

            // Setup list view
            LV = (ListView) findViewById(R.id.allBusesList);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.list_black_text, R.id.list_content, allBuses);
            LV.setAdapter(adapter);

            // Setup item click listener
            LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                    String bus = (String) (LV.getItemAtPosition(myItemInt));
                    String busTag = allBusTags[java.util.Arrays.asList(allBuses).indexOf(bus)];
                    new SetupBusPredictions().execute(busTag);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_buses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
