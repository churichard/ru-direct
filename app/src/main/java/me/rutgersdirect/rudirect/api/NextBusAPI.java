package me.rutgersdirect.rudirect.api;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import me.rutgersdirect.rudirect.ui.MainActivity;

public class NextBusAPI {

    // Gets XML from an address
    public static String getXML(String url) {
        String xml = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return XML
        return xml;
    }

    // Gets JSON from an address
    public static String getJSON(String address) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(address);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(MainActivity.class.toString(), "Failed to get JSON object");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    // Returns bus stop titles and times
    public static String[][] getBusStopTitlesAndTimes(String json) {
        ArrayList<String> busStopTitles = new ArrayList<>();
        ArrayList<String> busStopTimes = new ArrayList<>();
        try {
            JSONArray jArray = new JSONArray(json);
            for (int i = 0; i < jArray.length(); i++) {
                StringBuilder allTimes = new StringBuilder();
                JSONObject stopObject = jArray.getJSONObject(i);
                busStopTitles.add(stopObject.getString("title"));
                if (!stopObject.getString("predictions").equals("null")) {
                    JSONArray predictions = stopObject.getJSONArray("predictions");
                    for (int j = 0; j < predictions.length(); j++) {
                        JSONObject times = predictions.getJSONObject(j);
                        String min = times.getString("minutes");
                        if (min.equals("0")) {
                            min = "<1";
                        }
                        allTimes.append(min);
                        if (j != predictions.length() - 1) {
                            allTimes.append(", ");
                        }
                    }
                    allTimes.append(" minutes");
                }
                else {
                    allTimes.append("Offline");
                }
                busStopTimes.add(allTimes.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[][] titlesAndTimes = {busStopTitles.toArray(new String[busStopTitles.size()]),
                busStopTimes.toArray(new String[busStopTimes.size()])};
        return titlesAndTimes;
    }

    // Get active bus tags and titles
    public static HashMap<String, String> getActiveBusTagsAndTitles(String json) {
        HashMap<String, String> activeBusTitlesAndTags = new HashMap<>();

        try {
            JSONObject jObject = new JSONObject(json);
            String busArray = jObject.getString("routes");
            JSONArray jArray = new JSONArray(busArray);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject busObject = jArray.getJSONObject(i);
                activeBusTitlesAndTags.put(busObject.getString("title"), busObject.getString("tag"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activeBusTitlesAndTags;
    }
}