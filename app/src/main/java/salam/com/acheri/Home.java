package salam.com.acheri;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {



    private ViewPager viewPager;
    private TabLayout tabLayout;
    ImageView imgmovie,imgProfile;
    FloatingActionButton floatWrite;
    AdView adView;
    SharedPreferences sd;
    String user;
    SharedPreferences.Editor editor;
    private InterstitialAd mInterstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sd = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sd.edit();
        user = sd.getString("user","null");

        MobileAds.initialize(this, "ca-app-pub-1679206260526965~1117051146");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1679206260526965/9922691596");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        adView = findViewById(R.id.adView_home);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        Toolbar actionBar = (Toolbar)findViewById(R.id.toolbar_videos);
        setSupportActionBar(actionBar);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbarColor));
        }

        imgmovie = (ImageView)findViewById(R.id.img_movie);
        imgmovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Horror_Movies.class));
            }
        });

        imgProfile = (ImageView)findViewById(R.id.img_profile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.equals("null")){
                    Toast.makeText(Home.this,"Please Login To View Your Profile",Toast.LENGTH_LONG).show();
                }else {
                    startActivity(new Intent(Home.this,Profile_Module.class));
                    finish();
                }

            }
        });

        floatWrite = (FloatingActionButton)findViewById(R.id.float_write);

        floatWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.equals("null")){
                    Toast.makeText(Home.this,"Please Login To Write Your Story",Toast.LENGTH_LONG).show();
                }else {
                    startActivity(new Intent(Home.this,Editor.class));
                }
            }
        });

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mInterstitialAd.show();
        finish();
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragement(new Posts()," Stories");
        adapter.addFragement(new Haunted_Places()," Places");
        adapter.addFragement(new Alien_Stuff(),"Aliens ");
        viewPager.setAdapter(adapter);
    }


    private class ViewPagerAdapter  extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFagmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragement(Fragment fagment,String title){
            mFragmentList.add(fagment);
            mFagmentTitleList.add(title);
        }

        public CharSequence getPageTitle(int position){
            return mFagmentTitleList.get(position);
        }
    }


}
