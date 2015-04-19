package hackru2015s.ru_direct;

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
import android.widget.AdapterView.OnItemClickListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    ArrayList<String> activeBusTitles;
    ArrayList<String> activeBusTags;
    ListView LV;

    private class SetupListViewTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            return getJSON("http://runextbus.herokuapp.com/active");
        }

        protected void onPostExecute(String result) {
            activeBusTitles = new ArrayList<String>();
            activeBusTags = new ArrayList<String>();

            // Get active bus tags and titles
            try {
                JSONObject jObject = new JSONObject(result);
                String busArray = jObject.getString("routes");
                JSONArray jArray = new JSONArray(busArray);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject busObject = jArray.getJSONObject(i);
                    activeBusTitles.add(busObject.getString("title"));
                    activeBusTags.add(busObject.getString("tag"));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            // Setup list view
            LV = (ListView) findViewById(R.id.busList);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.list_black_text, R.id.list_content, activeBusTitles.toArray(new String[activeBusTitles.size()]));
            LV.setAdapter(adapter);

            // Setup item click listener
            LV.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                    String bus = (String) (LV.getItemAtPosition(myItemInt));
                    String busTag = activeBusTags.get(activeBusTitles.indexOf(bus));
                    new SetupBusPredictions().execute(busTag);
                }
            });
        }
    }

    private class SetupBusPredictions extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            return getJSON("http://runextbus.herokuapp.com/route/" + strings[0]);
        }

        protected void onPostExecute(String result) {
            ArrayList<String> busStopTitles = new ArrayList<String>();
            ArrayList<String> busStopTimes = new ArrayList<String>();
            String allTimes = "";
            try {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject stopObject = jArray.getJSONObject(i);
                    busStopTitles.add(stopObject.getString("title"));
                    JSONArray predictions = stopObject.getJSONArray("predictions");
                    for (int j = 0; j < predictions.length(); j++) {
                        JSONObject times = predictions.getJSONObject(j);
                        allTimes += times.getString("minutes");
                        if (j != predictions.length() - 1) {
                            allTimes += ", ";
                        }
                    }
                    busStopTimes.add(allTimes);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            Log.d("JSON Test", busStopTitles.toString());
            Log.d("JSON Test", busStopTimes.toString());
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Gets JSON from an address
    public String getJSON(String address){
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(address);
        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
            } else {
                Log.e(MainActivity.class.toString(), "Failed to get JSON object");
            }
        }catch(ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return builder.toString();
    }
}
