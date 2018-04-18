package salam.com.acheri;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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


public class Horror_Movies extends AppCompatActivity {

    ListView lvmovies;
    ArrayList<HashMap> list;
    HashMap<String,String> map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_horror__movies);

        Toolbar actionBar = (Toolbar)findViewById(R.id.toolbar_movies);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();

        lvmovies = (ListView)findViewById(R.id.lv_movies);


        new AsyncgetMovies().execute("something");

        //lvmovies.setAdapter(new MoviesAdapter());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class MoviesAdapter extends BaseAdapter {
        ArrayList<HashMap> data;
        public MoviesAdapter(ArrayList<HashMap> list) {
            data = list;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.custom_movie_layout,null);

            TextView tvname = (TextView)v.findViewById(R.id.tv_name);
            TextView tvrating = (TextView)v.findViewById(R.id.tv_rating);
            TextView tvdesc = (TextView)v.findViewById(R.id.tv_desc);



            tvname.setText(data.get(position).get("movie_name").toString());
            tvrating.setText(data.get(position).get("movie_rating").toString());
            tvdesc.setText(data.get(position).get("movie_desc").toString());

            return v;
        }
    }

    private class AsyncgetMovies extends AsyncTask<String,String,String> {

        ProgressBar pbloading = (ProgressBar)findViewById(R.id.pb_movies);
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
                url = new URL("https://salamappz.tech/Acheri/mv_read.php");

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
            Log.e("movies_data",s);
            parseResponse(s);
        }
    }

    private void parseResponse(String s) {
        try {
            JSONArray array = new JSONArray(s);
            JSONObject object;
            for(int i = 0;i<array.length();i++){
                object = array.getJSONObject(i);
                map =  new HashMap<String, String>();
                map.put("movie_id",object.getString("movie_id"));
                map.put("movie_name",object.getString("movie_name"));
                map.put("movie_rating",object.getString("imdb_rating"));
                map.put("movie_desc",object.getString("movie_desc_en"));
                list.add(map);
            }

            lvmovies.setAdapter(new MoviesAdapter(list));
            Log.e("movie_array_data",list.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
