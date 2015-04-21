package hackru2015s.ru_direct;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class BusTimesActivity extends ListActivity {

    String busName;

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
            String[] titles = busStopTitles.toArray(new String[busStopTitles.size()]);
            String[] times = busStopTimes.toArray(new String[busStopTimes.size()]);
            setListView(titles, times);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus_times);

        Intent intent = getIntent();
        busName = intent.getStringExtra(MainActivity.BUS_NAME);
        String[] busStopTitles = intent.getStringArrayExtra(MainActivity.BUS_STOP_TITLES_MESSAGE);
        String[] busStopTimes = intent.getStringArrayExtra(MainActivity.BUS_STOP_TIMES_MESSAGE);

        setListView(busStopTitles, busStopTimes);

        // Setup refresh button
        final Button refresh = (Button) findViewById(R.id.refreshTimes);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SetupBusPredictions().execute(busName);
            }
        });
    }

    public void setListView(String[] titles, String[] times) {
        String[] buses = new String[titles.length];

        for (int i = 0; i < buses.length; i++) {
            if (titles == null || times == null) {
                buses[i] = "No predictions available";
            }
            else {
                buses[i] = titles[i] + "\n" + times[i];
            }
        }

        ListView busTimesList = (ListView) findViewById(android.R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
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
            new SetupBusPredictions().execute(busName);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
