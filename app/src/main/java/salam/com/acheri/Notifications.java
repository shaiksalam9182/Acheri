package salam.com.acheri;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class Notifications extends AppCompatActivity {

    ListView lvNot;
    SharedPreferences sd;
    ArrayList<HashMap> dataList;
    HashMap<String,String> map;
    SharedPreferences.Editor editor;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Toolbar actionBar = (Toolbar)findViewById(R.id.toolbar_notifications);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvNot= (ListView)findViewById(R.id.lv_not);

        lvNot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Notifications.this,Notification_Complete_View.class);
                intent.putExtra("title",dataList.get(position).get("not_title").toString());
                intent.putExtra("desc",dataList.get(position).get("not_desc").toString());
                startActivity(intent);
            }
        });

        dataList = new ArrayList<>();

        sd = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sd.edit();

        user = sd.getString("user","null");

        new AsyncGetNotifications().execute(user);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private class NotAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return dataList.size();
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
            View v = inflater.inflate(R.layout.custom_notifications,null);

            TextView tv_title = (TextView)v.findViewById(R.id.tv_not_title);
            TextView tv_desc = (TextView)v.findViewById(R.id.tv_not_desc_real);
            TextView tv_date = (TextView)v.findViewById(R.id.tv_not_date);

            tv_title.setText(dataList.get(position).get("not_title").toString());
            tv_desc.setText(dataList.get(position).get("not_desc").toString());
            tv_date.setText(dataList.get(position).get("not_date").toString());


            return v;
        }
    }

    private class AsyncGetNotifications extends AsyncTask<String,String,String>{
        ProgressBar pbLoad;
        HttpsURLConnection connection;
        URL url = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoad = (ProgressBar)findViewById(R.id.pb_not_load);
            pbLoad.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://salamlabs.com/Acheri/read_not.php");

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
                        .appendQueryParameter("user", strings[0])

                        ;

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
            pbLoad.setVisibility(View.GONE);
            Log.e("not_res",s);
            praseData(s);


        }
    }

    private void praseData(String s) {
        try {
            JSONArray array = new JSONArray(s);
            JSONObject object;
            for (int i=0;i<array.length();i++){
                object = array.getJSONObject(i);
                map = new HashMap<String, String>();
                map.put("not_id",object.getString("Not_id"));
                map.put("not_title",object.getString("Not_title"));
                map.put("not_desc",object.getString("Not_desc"));
                map.put("not_date",object.getString("not_date"));
                dataList.add(map);
            }
            lvNot.setAdapter(new NotAdapter());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
