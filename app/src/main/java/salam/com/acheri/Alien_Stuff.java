package salam.com.acheri;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import static salam.com.acheri.Registration.CONNECTION_TIMEOUT;
import static salam.com.acheri.Registration.READ_TIMEOUT;


/**
 * A simple {@link Fragment} subclass.
 */
public class Alien_Stuff extends Fragment {

    RecyclerView rvAliens;
    ArrayList<HashMap> list;
    HashMap<String,String> map;


    @Override
    public void onStart() {
        super.onStart();

        rvAliens = (RecyclerView)getActivity().findViewById(R.id.rv_alien);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        list = new ArrayList<>();
        rvAliens.setLayoutManager(layoutManager);

        new AsyncgetAliens().execute("something");

        //rvAliens.setAdapter(new PostsAdapter(getContext()));
    }

    public Alien_Stuff() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alien__stuff, container, false);
    }

    private class AsyncgetAliens extends AsyncTask<String,String,String>{

        ProgressBar pbloading = (ProgressBar)getActivity().findViewById(R.id.pb_aliens);
        HttpsURLConnection connection;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbloading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                // Enter URL address where your php file resides
                url = new URL("https://salamappz.tech/Acheri/alien_read.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                connection = (HttpsURLConnection)url.openConnection();
                connection.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
                connection.setRequestMethod("POST");
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("name", strings[0]);


                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                connection.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {
                int response_code = connection.getResponseCode();

                Log.e("response_code",String.valueOf(response_code));
                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    // Pass data to onPostExecute method
                    return(result.toString());
                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                connection.disconnect();
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pbloading.setVisibility(View.GONE);
            Log.e("alien_data",s);
            parseResponse(s);
        }
    }

    private void parseResponse(String s) {
        try {
            JSONArray array = new JSONArray(s);
            JSONObject object;
            for (int i =0;i<array.length();i++){
                object = array.getJSONObject(i);
                map = new HashMap<String, String>();
                map.put("alien_id",object.getString("alien_id"));
                map.put("alien_title",object.getString("alien_title"));
                map.put("alien_desc",object.getString("alien_desc_en"));
                list.add(map);
            }
            rvAliens.setAdapter(new PostsAdapter(getContext(),list,"aliens"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
