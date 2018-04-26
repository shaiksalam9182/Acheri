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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import static salam.com.acheri.Registration.CONNECTION_TIMEOUT;
import static salam.com.acheri.Registration.READ_TIMEOUT;

public class Profile_Module extends AppCompatActivity {

    Button btLogout,btChangeProfile;
    String user;
    SharedPreferences sd;
    TextView tvnot,tvcont,tvname,tvmob;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__module);

        tvnot = (TextView)findViewById(R.id.tv_not);
        tvcont = (TextView)findViewById(R.id.tv_cont);
        tvname = (TextView)findViewById(R.id.tv_name);
        tvmob = (TextView)findViewById(R.id.tv_mob);

        Toolbar actionBar = (Toolbar)findViewById(R.id.toolbar_profile);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sd = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sd.edit();
         user = sd.getString("user",null);
        new AsyncProfile().execute(user);

        btLogout = (Button)findViewById(R.id.bt_logout);

        btChangeProfile = (Button)findViewById(R.id.bt_change_profile);

        btChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Profile_Module.this,"Under Development",Toast.LENGTH_LONG).show();
            }
        });

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();

                startActivity(new Intent(Profile_Module.this, Login.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Profile_Module.this,Home.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            startActivity(new Intent(Profile_Module.this,Home.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class AsyncProfile extends AsyncTask<String,String,String> {
        HttpsURLConnection connection;
        URL url = null;
        @Override
        protected String doInBackground(String... strings) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://salamappz.tech/Acheri/user_info.php");

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
            Log.e("profile_res",s);
            if (s.equals("exceptions")||s.equals("unsuccessfull")){
                Toast.makeText(Profile_Module.this,"sorry unable to process",Toast.LENGTH_LONG).show();
            }else if (s.contains(",")){
                String[] data = s.split(",");
                String name = data[0];
                String not = data[1];
                String cont = data[2];
                setData(name,not,cont);
            }
        }
    }

    private void setData(String name, String not, String cont) {
        tvnot.setText(not);
        tvcont.setText(cont);
        tvmob.setText(user);
        tvname.setText(name);
    }
}
