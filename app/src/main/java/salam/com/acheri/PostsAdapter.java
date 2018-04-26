package salam.com.acheri;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import static salam.com.acheri.Registration.CONNECTION_TIMEOUT;
import static salam.com.acheri.Registration.READ_TIMEOUT;

/**
 * Created by raj on 01-Feb-18.
 */

class PostsAdapter extends RecyclerView.Adapter <PostsAdapter.MyViewAdapter>{

    Context adapter_context;
    ArrayList<HashMap> data;
    String whichclass,user,count;
    SharedPreferences sd;
    SharedPreferences.Editor editor;


    public class MyViewAdapter extends RecyclerView.ViewHolder {
        TextView tvheading,tvdesc,tvScareCount,tvShareCount;
        ImageView imgScared,imgComment,imgShare;
        public MyViewAdapter(View view) {
            super(view);

            tvheading = (TextView)view.findViewById(R.id.tv_heading);
            tvdesc = (TextView)view.findViewById(R.id.tv_story);

            tvScareCount = (TextView)view.findViewById(R.id.tv_scare_count);
            tvShareCount = (TextView)view.findViewById(R.id.tv_share_count);

            imgScared = (ImageView)view.findViewById(R.id.img_scared);
            imgComment = (ImageView)view.findViewById(R.id.img_comment);
            imgShare= (ImageView)view.findViewById(R.id.img_share);


        }
    }

    public PostsAdapter(Context context, ArrayList list,String comingfrom) {
        adapter_context = context;
        data = list;
        whichclass = comingfrom;

        sd = adapter_context.getSharedPreferences("login",Context.MODE_PRIVATE);
        editor = sd.edit();
        user = sd.getString("user","null");
    }

    @Override
    public MyViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(adapter_context)
                .inflate(R.layout.card_layout,parent,false);
        return new MyViewAdapter(itemView);
    }

    @Override
    public void onBindViewHolder(final PostsAdapter.MyViewAdapter holder, final int position) {
        if (whichclass.equals("posts")){

            holder.imgScared.setOnClickListener(new View.OnClickListener() {
                class AsyncAddLikePost extends AsyncTask<String,String,String>{
                    HttpsURLConnection connection;
                    URL url = null;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(String... strings) {
                        try {

                            // Enter URL address where your php file resides
                            url = new URL("https://salamappz.tech/Acheri/add_like.php");

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
                                    .appendQueryParameter("postId", strings[0])
                                    .appendQueryParameter("sender",strings[1])

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
                        Log.e("like_status",s);
                        if (count!="already"){
                            count=s;
                            holder.tvScareCount.setText(count);

                        }

                    }
                }
                @Override
                public void onClick(View v) {

                    new AsyncAddLikePost().execute(data.get(position).get("post_id").toString(),user);
                    //Toast.makeText(adapter_context,"Under Development",Toast.LENGTH_LONG).show();
                }
            });

            holder.imgComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(adapter_context,"Under Development",Toast.LENGTH_LONG).show();
                }
            });

            holder.imgShare.setOnClickListener(new View.OnClickListener() {
                class AsyncAddShare extends AsyncTask<String,String,String> {
                    HttpsURLConnection connection;
                    URL url = null;
                    @Override
                    protected String doInBackground(String... strings) {
                        try {

                            // Enter URL address where your php file resides
                            url = new URL("https://salamappz.tech/Acheri/add_share.php");

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
                                    .appendQueryParameter("post_id", strings[0])
                                    .appendQueryParameter("type",strings[1])

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
                        holder.tvShareCount.setText(s);
                    }
                }

                @Override
                public void onClick(View v) {
                   String sharebody = data.get(position).get("post_title").toString();
                   Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                   sharingIntent.setType("text/plain");
                   sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Acheri");
                   sharingIntent.putExtra(Intent.EXTRA_TEXT,sharebody);
                   adapter_context.startActivity(Intent.createChooser(sharingIntent,"Share Using.."));
                   new AsyncAddShare().execute(data.get(position).get("post_id").toString(),"post");
                }
            });


            holder.tvScareCount.setText(data.get(position).get("likes").toString());
            holder.tvShareCount.setText(data.get(position).get("shares").toString());


            holder.tvheading.setText(data.get(position).get("post_title").toString());
            holder.tvdesc.setText(data.get(position).get("post_desc").toString());
            holder.tvheading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("post_title").toString());
                    head.putExtra("desc",data.get(position).get("post_desc").toString());
                    adapter_context.startActivity(head);
                }
            });


            holder.tvdesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("post_title").toString());
                    head.putExtra("desc",data.get(position).get("post_desc").toString());
                    adapter_context.startActivity(head);
                    //Toast.makeText(adapter_context,"description",Toast.LENGTH_LONG).show();
                }
            });

        }else if (whichclass.equals("places")){
            holder.imgScared.setOnClickListener(new View.OnClickListener() {
                class AsyncaddLIkePlaces extends AsyncTask<String,String,String>{
                    HttpsURLConnection connection;
                    URL url = null;
                    @Override
                    protected String doInBackground(String... strings) {
                        try {

                            // Enter URL address where your php file resides
                            url = new URL("https://salamappz.tech/Acheri/add_like_places.php");

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
                                    .appendQueryParameter("postId", strings[0])
                                    .appendQueryParameter("sender",strings[1])

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
                        Log.e("places_likes",s);
                        if (!s.equals("already")){

                            holder.tvScareCount.setText(s);
                        }
                    }
                }

                @Override
                public void onClick(View v) {
                    Log.e("places_like","clicked");
                    new AsyncaddLIkePlaces().execute(data.get(position).get("place_id").toString(),user);
                }
            });



            holder.imgComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(adapter_context,"Under Development",Toast.LENGTH_LONG).show();
                }
            });

            holder.imgShare.setOnClickListener(new View.OnClickListener() {
                class AsyncAddShare extends AsyncTask<String,String,String>{
                    HttpsURLConnection connection;
                    URL url = null;
                    @Override
                    protected String doInBackground(String... strings) {
                        try {

                            // Enter URL address where your php file resides
                            url = new URL("https://salamappz.tech/Acheri/add_share.php");

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
                                    .appendQueryParameter("post_id", strings[0])
                                    .appendQueryParameter("type",strings[1])

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
                        holder.tvShareCount.setText(s);
                    }
                }

                @Override
                public void onClick(View v) {
                    String sharebody = data.get(position).get("place_title").toString();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Acheri");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT,sharebody);
                    adapter_context.startActivity(Intent.createChooser(sharingIntent,"Share Using.."));
                    new AsyncAddShare().execute(data.get(position).get("place_id").toString(),"place");
                }
            });

            holder.tvScareCount.setText(data.get(position).get("likes").toString());
            holder.tvShareCount.setText(data.get(position).get("shares").toString());

            holder.tvheading.setText(data.get(position).get("place_title").toString());
            holder.tvdesc.setText(data.get(position).get("place_desc").toString());
            holder.tvheading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("place_title").toString());
                    head.putExtra("desc",data.get(position).get("place_desc").toString());
                    adapter_context.startActivity(head);
                }
            });

            holder.tvdesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("place_title").toString());
                    head.putExtra("desc",data.get(position).get("place_desc").toString());
                    adapter_context.startActivity(head);
                    //Toast.makeText(adapter_context,"description",Toast.LENGTH_LONG).show();
                }
            });
        }else if (whichclass.equals("aliens")){


            holder.imgScared.setOnClickListener(new View.OnClickListener() {
                class AsyncAddLikealiens extends AsyncTask<String,String,String>{
                    HttpsURLConnection connection;
                    URL url = null;
                    @Override
                    protected String doInBackground(String... strings) {
                        try {

                            // Enter URL address where your php file resides
                            url = new URL("https://salamappz.tech/Acheri/alien_like_aliens.php");

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
                                    .appendQueryParameter("postId", strings[0])
                                    .appendQueryParameter("sender",strings[1])

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
                        Log.e("aliens_likes",s);
                        if (!s.equals("already")){
                            holder.tvScareCount.setText(s);

                        }
                    }
                }

                @Override
                public void onClick(View v) {
                   new  AsyncAddLikealiens().execute(data.get(position).get("alien_id").toString(),user);
                }
            });




            holder.imgComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(adapter_context,"Under Development",Toast.LENGTH_LONG).show();
                }
            });

            holder.imgShare.setOnClickListener(new View.OnClickListener() {
                class AsyncAddShare extends AsyncTask<String,String,String> {
                    HttpsURLConnection connection;
                    URL url = null;

                    @Override
                    protected String doInBackground(String... strings) {
                        try {

                            // Enter URL address where your php file resides
                            url = new URL("https://salamappz.tech/Acheri/add_share.php");

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
                                    .appendQueryParameter("post_id", strings[0])
                                    .appendQueryParameter("type",strings[1])

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
                        holder.tvShareCount.setText(s);
                    }
                }

                @Override
                public void onClick(View v) {
                    String sharebody = data.get(position).get("alien_title").toString();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Acheri");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT,sharebody);
                    adapter_context.startActivity(Intent.createChooser(sharingIntent,"Share Using.."));
                    new AsyncAddShare().execute(data.get(position).get("alien_id").toString(),"aliens");
                }
            });
            holder.tvScareCount.setText(data.get(position).get("likes").toString());
            holder.tvShareCount.setText(data.get(position).get("shares").toString());

            holder.tvheading.setText(data.get(position).get("alien_title").toString());
            holder.tvdesc.setText(data.get(position).get("alien_desc").toString());
            holder.tvheading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("alien_title").toString());
                    head.putExtra("desc",data.get(position).get("alien_desc").toString());
                    adapter_context.startActivity(head);
                }
            });

            holder.tvdesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent head = new Intent(adapter_context,Post_display.class);
                    head.putExtra("head",data.get(position).get("alien_title").toString());
                    head.putExtra("desc",data.get(position).get("alien_desc").toString());
                    adapter_context.startActivity(head);
                    //Toast.makeText(adapter_context,"description",Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class AsynAddLikePost extends AsyncTask<String,String,String> {
        HttpsURLConnection connection;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://salamappz.tech/Acheri/add_like.php");

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
                        .appendQueryParameter("postId", strings[0])
                        .appendQueryParameter("sender",strings[1])

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
            Log.e("like_status",s);
            count=s;
        }
    }
}
