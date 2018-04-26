package salam.com.acheri;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Editor extends AppCompatActivity {


    EditText etTitle,etDesc;
    Button btPost;
    String title,description;
    SharedPreferences sd;
    SharedPreferences.Editor editor;
    String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar actionBar = (Toolbar)findViewById(R.id.toolbar_posts);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sd = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sd.edit();

        username = sd.getString("user","null");


        etTitle = (EditText)findViewById(R.id.et_title);
        etDesc = (EditText)findViewById(R.id.et_desc);

        btPost = (Button)findViewById(R.id.bt_post);


        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = etTitle.getText().toString();
                description = etDesc.getText().toString();
                if (TextUtils.isEmpty(title)||TextUtils.isEmpty(description)){
                    Toast.makeText(Editor.this,"Please Fill All Fields",Toast.LENGTH_LONG).show();
                }else if (title.length()>50){
                    Toast.makeText(Editor.this,"Title Must Be Less Than 50 Character",Toast.LENGTH_LONG).show();
                }else {
                    new AsyncPost().execute(title,description,username);
                }
            }
        });

    }

    private class AsyncPost extends AsyncTask<String,String,String>{

        ProgressDialog pdLoading= new ProgressDialog(Editor.this);
        HttpsURLConnection connection;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Posting..\nPlease Wait..");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://salamappz.tech/Acheri/insert_post.php");

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
                        .appendQueryParameter("title", strings[0])
                        .appendQueryParameter("desc",strings[1])
                        .appendQueryParameter("sender",strings[2])
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
            pdLoading.dismiss();
            if (s.equals("true")){
                Toast.makeText(Editor.this,"Your Post Will Come Into Live Once We Review It..Thank you..",Toast.LENGTH_LONG).show();
            }else if (s.equals("false")){
                Toast.makeText(Editor.this,"Sorry..We are unable to post it..",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(Editor.this,"Some Error Occurred",Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
