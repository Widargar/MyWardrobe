package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private long pressedTime;
    private TextView city, temp, cond;
    private ImageView icon, menuIcon;
    private LinearLayout weatherLayout, content;
    private RelativeLayout closetLayout, camera, logOutLayout;
    private ProgressBar progressBar;
    private LocationManager locationManager;
    private RecyclerView featuredRecycler;
    private RecyclerView.Adapter adapter;
    private int PERMISSION_CODE = 1;
    private String cityName;
    private final static String TAG = "bilki: Main ";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static final float END_SCALE = 0.97f;
    private ArrayList<FeaturedHelperClass> featuredLocations;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private String[] PERMISSIONS;
    private PermissionUtility permissionUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance();
        featuredLocations = new ArrayList<>();

        //Permissions --start--
        PERMISSIONS = new String[]{

                Manifest.permission.READ_SMS,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE

        };

        permissionUtility = new PermissionUtility(this, PERMISSIONS);
        if(permissionUtility.arePermissionsEnabled()){
            Log.d(TAG, "Permission granted 1");
        } else {
            permissionUtility.requestMultiplePermissions();
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);

        }
        //Permissions --end--



        //Hooks
        city = (TextView) findViewById(R.id.city_weather);
        temp = (TextView) findViewById(R.id.temperature);
        cond = (TextView) findViewById(R.id.condition);
        icon = (ImageView) findViewById(R.id.weather_icon);
        weatherLayout = (LinearLayout) findViewById(R.id.weather);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        featuredRecycler = (RecyclerView) findViewById(R.id.featured_recycler);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        menuIcon = (ImageView) findViewById(R.id.menu_icon);
        content = (LinearLayout) findViewById(R.id.content);

        closetLayout = (RelativeLayout) findViewById(R.id.closet_button);
        logOutLayout = (RelativeLayout) findViewById(R.id.log_out_button);

        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, PackageManager.PERMISSION_GRANTED);

        navigationDrawer();

        featuredRecycler();

        //askCameraPermissions();
        //askLocationPermission();
        /*if (!hasPermissions(MainActivity.this, PERMISSIONS)){

            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);

        }*/


        //Weather --start--
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(), location.getLatitude());

        //Weather --end--




        onClosetCLick();

        //onLogOutCLick();


    }

    @Override
    protected void onStart() {
        super.onStart();
        getWeatherInfo(cityName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissionUtility.onRequestPermissionsResult(requestCode, permissions, grantResults)) {

            Log.d(TAG, "Permission granted 2");
        }
    }

    /*private boolean hasPermissions(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null) {

            for (String permission: PERMISSIONS){

                if (ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED) {



                    return false;

                }
            }
        }

        return true;
    }*/

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted!");
            }else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_LONG).show();
                finish();
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted!");

            }else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_LONG).show();
                finish();
            }

            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted!");
            }else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_LONG).show();
                finish();
            }

            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted!");
            }else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_LONG).show();
                finish();
            }

            if (grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted!");
            }else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_LONG).show();
                finish();
            }

            if (grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted!");
            }else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_LONG).show();
                finish();
            }

            if (grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted!");
            }else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_LONG).show();
                finish();
            }

            if (grantResults[7] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted!");
            }else {
                Log.d(TAG, "Permission not granted");
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_LONG).show();
                finish();
            }

        }
    }*/

    private void askCameraPermissions(){

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);

        }

    }

    private void askLocationPermission(){



    }

    //Navigation drawer --start--
    private void navigationDrawer() {

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){

                    drawerLayout.closeDrawer(GravityCompat.START);

                }
                else{

                    drawerLayout.openDrawer(GravityCompat.START);

                }
            }
        });

        navigationDrawerAnimation();

    }

    private void navigationDrawerAnimation() {

        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        //drawerLayout.setScrimColor(getResources().getColor(R.color.white));
        drawerLayout.setScrimColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                content.setScaleX(offsetScale);
                content.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = content.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                content.setTranslationX(xTranslation);
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_home:
                break;
            case R.id.nav_closet:
                Intent intent1 = new Intent(MainActivity.this, Closet.class);
                startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                break;
            case R.id.nav_profile:
                Intent intent2 = new Intent(MainActivity.this, Profile.class);
                startActivity(intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
                break;
            case R.id.nav_log_out:
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();

        }

        return true;
    }

    public void onClosetCLick(){

        closetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Closet.class);
                //overridePendingTransition(0, 0);
                overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
                startActivity(intent);
                finish();
                //getWindow().setWindowAnimations(0);


            }
        });


    }

    /*public void onLogOutCLick(){

        logOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();

            }
        });


    }*/

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerVisible(GravityCompat.START)){

            drawerLayout.closeDrawer(GravityCompat.START);

        }else if( pressedTime + 2000 > System.currentTimeMillis()) {

                super.onBackPressed();
                finish();


        } else {

            Toast.makeText(getBaseContext(), "Please, press back again to exit", Toast.LENGTH_SHORT).show();

        }


        pressedTime = System.currentTimeMillis();

    }
    //Navigation drawer --end--


    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_CODE){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Log.d(TAG, "Permission granted!");

            }else{

                Log.d(TAG, "Permission not granted");
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_LONG).show();
                finish();
            }

        }
    }*/

    private void getWeatherInfo(String cityName){

        String url = "https://api.weatherapi.com/v1/current.json?key=cd599d82deb5404f81a232210211311&q=Cracov&aqi=no";

        city.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                weatherLayout.setVisibility(View.VISIBLE);

                try {

                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temp.setText(temperature + "°C");

                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    cond.setText(condition);

                    String icon_weather = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(icon_weather)).into(icon);



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "Smth wrong with city name: " + error);

            }
        });

        requestQueue.add(jsonObjectRequest);


    }

    private String getCityName(double longtit, double latit){

        String city_name = "Not found";
        Geocoder geocoder = new Geocoder(getBaseContext(), /*Locale.getDefault()*/new Locale("en_001"));

        try{

            List<Address> addresses = geocoder.getFromLocation(latit, longtit, 1);

            for(Address address : addresses){

                if(address != null){

                    String city = address.getLocality();

                    if(city != null && !city.equals("")){

                        Log.d(TAG, "City name: " + city);
                        city_name = city;

                    }else{

                        Log.d(TAG, "Cannot found a city!");
                        Toast.makeText(this, "Cannot found your city!", Toast.LENGTH_LONG).show();

                    }


                }

            }

        }catch (IOException e){

            e.printStackTrace();

        }

        return city_name;

    }

    private void featuredRecycler(){

        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        featuredLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Hat"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Jacket"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Trousers"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Boots"));

        adapter = new FeaturedAdapter(featuredLocations);
        featuredRecycler.setAdapter(adapter);

    }



}