package mosis.myplaces;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neca on 24.3.2016..
 */
public class MyPlacesHTTPHelper {
    private static final String GET_MY_PLACE = "1";
    private static final String GET_ALL_PLACES_NAMES = "2";
    private static final String SEND_MY_PLACE = "3";

    static String urlIPServera = "http://10.0.2.2.:8080";

    public static String sendMyPlace(MyPlace place)
    {
        String retStr = "";
        try {
            URL url = new URL(urlIPServera);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Pravimo JSON objekat myplace za slanje
            JSONObject holder = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("name", place.getName());
            data.put("desc", place.getDesc());
            data.put("lon", place.getLongitude());
            data.put("lat", place.getLatitude());
            holder.put("myplace", data);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("req", SEND_MY_PLACE)
                    .appendQueryParameter("name", place.getName())
                    .appendQueryParameter("payload", holder.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                retStr = inputStreamToString(connection.getInputStream());
            }
            else
            {
                retStr = String.valueOf("Error:" + responseCode);
            }
        } catch (Exception ec)
        {
            ec.printStackTrace();
            retStr = "Error during upload!";
        }
        return retStr;
    }

    private static String inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null){
                total.append(line);
            }
        } catch (Exception ec) {
            ec.printStackTrace();
        }

        return total.toString();
    }

    public static List<String> getAllPLacesNames()
    {
        List<String> names = new ArrayList<String>();
        try {
             URL url = new URL(urlIPServera);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("req", GET_ALL_PLACES_NAMES);
            String query = builder.build().getEncodedQuery();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                String str = inputStreamToString(connection.getInputStream());
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray("myplacesnames");
                for (int i=0; i < jsonArray.length(); i++){
                    String name = jsonArray.getString(i);
                    names.add(name);
                }
            }
            else
            {
                names.add("Connection error:" + String.valueOf(responseCode));
            }
        } catch (Exception ec)
        {
            ec.printStackTrace();
            names.add("Problem downloading places names list");
        }
        return names;
    }

    public static MyPlace getMyPlace(String itemName)
    {
        MyPlace place = null;
        try {
            URL url = new URL(urlIPServera);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("req", GET_MY_PLACE)
                    .appendQueryParameter("name", itemName);
            String query = builder.build().getEncodedQuery();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(connection.getInputStream());
                JSONObject jsonObject = new JSONObject(str);
                JSONObject jsonPlace = jsonObject.getJSONObject("myplace");
                String name = jsonPlace.getString("name");
                String desc = jsonPlace.getString("desc");
                String lon = jsonPlace.getString("lon");
                String lat = jsonPlace.getString("lat");
                place = new MyPlace(name, desc);
                place.setLongitude(lon);
                place.setLatitude(lat);
            }

        } catch (Exception ec)
        {
            ec.printStackTrace();
        }
        return place;
    }

}

