package salam.com.acheri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Post_display extends AppCompatActivity {


    TextView tvhead,tvdesc;
    String head,desc;
    ImageView imgCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);

        head = getIntent().getStringExtra("head");
        desc = getIntent().getStringExtra("desc");

        tvhead = (TextView)findViewById(R.id.tv_post_head);
        tvdesc = (TextView)findViewById(R.id.tv_post_desc);


        imgCancel = (ImageView)findViewById(R.id.img_cancel);

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        tvhead.setText(head);
        tvdesc.setText(desc);

    }
}
