package salam.com.acheri;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class Registration extends AppCompatActivity {


    EditText etName,etPhone,etPassword,etConfPassword;
    Spinner spCountry;
    Button btRegister;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    String name,phone,password,cpassword,country;
    ArrayAdapter<String> countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        etName = (EditText)findViewById(R.id.et_name);
        etPhone = (EditText)findViewById(R.id.et_phone);
        etPassword = (EditText)findViewById(R.id.et_password);
        etConfPassword = (EditText)findViewById(R.id.et_conf_password);


        spCountry = (Spinner)findViewById(R.id.sp_country);

        btRegister = (Button)findViewById(R.id.bt_register);

        countries = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.Countries));
        spCountry.setAdapter(countries);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                phone = etPhone.getText().toString();
                password = etPassword.getText().toString();
                cpassword = etConfPassword.getText().toString();
                country = spCountry.getSelectedItem().toString();

                if (name.length()>20){
                    Toast.makeText(Registration.this,"Name Must Be Less Than 20 Characters",Toast.LENGTH_LONG).show();
                }else if (phone.length()>10 || phone.length()<10){
                    Toast.makeText(Registration.this,"Invalid Phone No",Toast.LENGTH_LONG).show();
                }else if (password.length()<8 ||password.length()>20){
                    Toast.makeText(Registration.this,"Password must be greater than 8 characters and lessthan 20 characters",Toast.LENGTH_LONG).show();
                }else if (country.equals("Select Country")){
                    Toast.makeText(Registration.this,"Please select country",Toast.LENGTH_LONG).show();
                }else if (!password.equals(cpassword)) {
                    Toast.makeText(Registration.this,"Passwords are not matching",Toast.LENGTH_LONG).show();
                }else {
                    completeRegistration(name,phone,password,cpassword,country);
                }
            }
        });
    }

    private void completeRegistration(String name, String phone, String password, String cpassword, String country) {
        new AsyncRegistration().execute(name,phone,password,country);
    }



    private class AsyncRegistration extends AsyncTask<String,String,String> {

        ProgressDialog pdLoading = new ProgressDialog(Registration.this);

        HttpsURLConnection connection;
        URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setCancelable(false);
            pdLoading.setMessage("Registering\nPlease Wait...");
            pdLoading.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://salamappz.tech/Acheri/reg.php");

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
                        .appendQueryParameter("name", strings[0])
                        .appendQueryParameter("phone",strings[1])
                        .appendQueryParameter("password",strings[2])
                        .appendQueryParameter("country",strings[3]);

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
                Toast.makeText(Registration.this,"success",Toast.LENGTH_LONG).show();
            }else if (s.equals("false")){
                Toast.makeText(Registration.this,"Something went wrong..",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(Registration.this,s,Toast.LENGTH_LONG).show();
            }

        }
    }
}
