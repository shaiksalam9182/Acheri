package salam.com.acheri;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Profile_Module extends AppCompatActivity {

    Button btLogout,btChangeProfile;
    SharedPreferences sd;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__module);

        Toolbar actionBar = (Toolbar)findViewById(R.id.toolbar_profile);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sd = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sd.edit();

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
}
