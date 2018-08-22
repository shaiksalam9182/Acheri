package salam.com.acheri;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

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

public class Login extends AppCompatActivity {


    TextView tvRegister;

    EditText etphone,etpass;

    Button btlogin;

    String phone,password;

    SharedPreferences sd;
    SharedPreferences.Editor editor;
    AdView adView;
    TextView tvSkip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MobileAds.initialize(this, "ca-app-pub-1679206260526965~1117051146");

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        sd = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sd.edit();


        etphone =(EditText)findViewById(R.id.et_phone);
        etpass = (EditText)findViewById(R.id.et_pass);

        btlogin = (Button)findViewById(R.id.bt_login);

        tvSkip = (TextView)findViewById(R.id.tv_skip);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(Login.this,Home.class);
                startActivity(login);
                finish();
            }
        });



        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             phone = etphone.getText().toString();
             password = etpass.getText().toString();
             if (TextUtils.isEmpty(phone)||TextUtils.isEmpty(password)){
                 Toast.makeText(Login.this,"Fill All Fields",Toast.LENGTH_LONG).show();
             }else {
                 validateUser(phone,password);
             }

            }
        });


        tvRegister = (TextView)findViewById(R.id.tv_register);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Registration.class));
            }
        });
    }

    private void validateUser(String phone, String password) {
        new AsyncLogin().execute(phone,password);
    }

    private class AsyncLogin extends AsyncTask<String,String,String> {

        ProgressDialog pdLoading = new ProgressDialog(Login.this);

        HttpsURLConnection connection;
        URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setCancelable(false);
            pdLoading.setMessage("Authenticating\nPlease Wait...");
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://salamlabs.com/Acheri/login.php");

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
                        .appendQueryParameter("phone", strings[0])
                        .appendQueryParameter("password",strings[1])
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
                Toast.makeText(Login.this,"Successfully Done",Toast.LENGTH_LONG).show();
                Intent login = new Intent(Login.this,Home.class);
                startActivity(login);
                editor.putString("login","true");
                editor.putString("user",phone);
                editor.commit();
                finish();
            }else if (s.equals("false")){
                Toast.makeText(Login.this,"Invalid Credentials",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(Login.this,"Something went wrong..",Toast.LENGTH_LONG).show();
            }
        }
    }
}
