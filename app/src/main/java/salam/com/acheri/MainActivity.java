package salam.com.acheri;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView img;
    SharedPreferences sd;
    SharedPreferences.Editor editor;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        sd = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sd.edit();

        status = sd.getString("login","null");


        img = (ImageView) findViewById(R.id.img);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbarColor));
        }


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
