package salam.com.acheri;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
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

public class MainActivity extends AppCompatActivity {


    SharedPreferences sd;
    SharedPreferences.Editor editor;
    String status,version,received_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        PackageManager manager = MainActivity.this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(MainActivity.this.getPackageName(), 0);
            version = info.versionName;
            Log.e("app_version", version);

            new AsyncGetVersion().execute("notin");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        sd = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sd.edit();

        status = sd.getString("login","null");




        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbarColor));
        }

    }

    private class AsyncGetVersion extends AsyncTask<String,String,String> {
        HttpsURLConnection connection;
        URL url = null;

        @Override
        protected String doInBackground(String... strings) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://salamlabs.com/Acheri/version.php");

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
            if (TextUtils.isEmpty(s)){
                Toast.makeText(MainActivity.this,"Nothing From server",Toast.LENGTH_LONG).show();
                finish();
            }else if (s.equals("exception")||s.equals("unsuccessful")){
                Toast.makeText(MainActivity.this,"Unable to Connect To Server",Toast.LENGTH_LONG).show();
            }else {
                received_version = s;
                compareVersion(version,received_version);
            }

        }

        private void compareVersion(String version, String received_version) {
            float versio = Float.parseFloat(version);
            float received_versio = Float.parseFloat(received_version);
            if (versio<received_versio){
                raiseDailog();
            }else {

                Thread logotimer = new Thread() {
                    public void run() {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            {
                                if (status.equals("null")){
                                    startActivity(new Intent(MainActivity.this, Login.class));
                                    finish();
                                }else {
                                    startActivity(new Intent( MainActivity.this,Home.class));
                                    finish();
                                }
                            }
                        }
                    }
                };
                logotimer.start();
            }
        }
    }

    private void raiseDailog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle("Update");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("New Version Of Acheri Is Available Please Update App");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "https://play.google.com/store/apps/details?id=salam.com.acheri";
                        Intent viewintent = new Intent("android.intent.action.VIEW",Uri.parse(url));
                        startActivity(viewintent);
                        dialog.cancel();
                        finish();

                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialog.show();
    }
}
