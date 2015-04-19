package hackru2015s.ru_direct;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


public class AllBusesActivity extends ActionBarActivity {
    private final String[] allBuses = {"A", "B", "C", "EE", "F", "H", "LX", "Rex B", "Rex L", "Weekend 1", "Weekend 2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_buses);

        Intent intent = getIntent();
        String[] activeBusTags = intent.getStringArrayExtra(MainActivity.ACTIVE_BUS_TAGS);
        String[] activeBusTitles = intent.getStringArrayExtra(MainActivity.ACTIVE_BUSES_MESSAGE);

        for (int i = 0; i < activeBusTitles.length; i++) {
//            if (activeBusTitles[i].equals("a")) {
//
//            }

            // Setup list view
            ListView LV = (ListView) findViewById(R.id.allBusesList);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.list_black_text, R.id.list_content, allBuses);
            LV.setAdapter(adapter);

            // Setup item click listener
//            LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
//                    String bus = (String) (LV.getItemAtPosition(myItemInt));
//                    String busTag = activeBusTags.get(activeBusTitles.indexOf(bus));
//                    new SetupBusPredictions().execute(busTag);
//                }
//            });
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
