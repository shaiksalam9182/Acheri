package salam.com.acheri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class Notification_Complete_View extends AppCompatActivity {


    String title,desc;
    TextView tvTitle,tvDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification__complete__view);

        Toolbar actionBar = (Toolbar)findViewById(R.id.toolbar_notifications);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");

        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvDesc = (TextView)findViewById(R.id.tv_comp_desc);

        tvTitle.setText(title);
        tvDesc.setText(desc);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
