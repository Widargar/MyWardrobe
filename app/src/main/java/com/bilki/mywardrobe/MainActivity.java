package com.bilki.mywardrobe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.Gravity;
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
import com.bilki.mywardrobe.NewsModels.NewsApiResponse;
import com.bilki.mywardrobe.NewsModels.NewsHeadlines;
import com.bumptech.glide.RequestManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, fashionNewsAdapter.SelectNewsListener {

    private long pressedTime;
    private TextView city, temp, cond, news;
    private ImageView icon, menuIcon;
    private LinearLayout weatherLayout, content;
    public LinearLayout weatherDataLayout;
    private RelativeLayout closetLayout;
    private noInternetConnection dialog;
    public ProgressBar progressBar;
    private LocationManager locationManager;
    private Location location;
    private RecyclerView featuredRecycler, fashNewsRecycler;
    private RecyclerView.Adapter adapter, _adapter;
    private int PERMISSION_CODE = 1;
    public String cityName;
    private JSONArray hourArray;
    private WeatherForecast WeatherDialog;
    public ArrayList<WeatherHelperClass> weatherArrayList;
    public WeatherAdapter weatherAdapter;
    private final static String TAG = "bilki: Main ";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static final float END_SCALE = 0.97f;
    private ArrayList<FeaturedHelperClass> featuredLocations;
    private ArrayList<fashionNewsHelperClass> fashionArrayList;
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
        fashionArrayList = new ArrayList<>();

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
        weatherDataLayout = (LinearLayout) findViewById(R.id.weather_data);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        featuredRecycler = (RecyclerView) findViewById(R.id.featured_recycler);
        news = (TextView) findViewById(R.id.news);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        menuIcon = (ImageView) findViewById(R.id.menu_icon);
        content = (LinearLayout) findViewById(R.id.content);

        closetLayout = (RelativeLayout) findViewById(R.id.closet_button);
        fashNewsRecycler = (RecyclerView) findViewById(R.id.news_recycler);


        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, PackageManager.PERMISSION_GRANTED);

        navigationDrawer();
        featuredRecycler();
        newsRecycler();


        //askCameraPermissions();
        //askLocationPermission();
        /*if (!hasPermissions(MainActivity.this, PERMISSIONS)){

            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);

        }*/


        //Weather --start--
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        langANDlong();
        //Weather --end--



        weatherDataLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getWeatherInfo(cityName);
                WeatherDialog = new WeatherForecast();
                WeatherDialog.show(getFragmentManager(), "dialog");

            }
        });

        onClosetCLick();

        //onLogOutCLick();


    }

    @Override
    protected void onStart() {
        super.onStart();

        getWeatherInfo(cityName);

        CheckInternet checkInternet = new CheckInternet();
        if(!checkInternet.isConnected(MainActivity.this)){

            progressBar.setVisibility(View.VISIBLE);
            weatherDataLayout.setVisibility(View.GONE);
            dialog = new noInternetConnection();
            dialog.show(getFragmentManager(), "internetConnectionDialog");
            dialog.setCancelable(false);

        }

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

    public void getWeatherInfo(String cityName){

//        String url = "https://api.weatherapi.com/v1/current.json?key=cd599d82deb5404f81a232210211311&q=Cracov&aqi=no";
//        String url = "https://api.weatherapi.com/v1/forecast.json?key=ec099509b8474853b85230806222901&q=" + cityName + "&days=1&aqi=no&alerts=no";
        String url = "https://api.weatherapi.com/v1/forecast.json?key=ec099509b8474853b85230806222901&q=Cracov&days=1&aqi=no&alerts=no";
        city.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                weatherLayout.setVisibility(View.VISIBLE);

                try {

                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temp.setText(temperature + "°C");

                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    cond.setText(condition);

                    String icon_weather = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(icon_weather)).centerCrop().resize(80, 80).into(icon);

                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONObject forecastday = forecast.getJSONArray("forecastday").getJSONObject(0);
                    hourArray = forecastday.getJSONArray("hour");

                    Log.d(TAG, "Temp: " + temp);



//                    for(int i = 0; i < hourArray.length(); i++){
//
//                        JSONObject hourObject = hourArray.getJSONObject(i);
//                        String time = hourObject.getString("time");
//                        String temperat = hourObject.getString("temp_c");
//                        String img = hourObject.getJSONObject("condition").getString("icon");
//                        String conditon = hourObject.getJSONObject("condition").getString("text");
//                        weatherArrayList.add(new WeatherHelperClass(time, temperat, img, conditon));
//                        Log.d(TAG, "Temp: " + temperat);
////                        weatherAdapter = new WeatherAdapter(MainActivity.this, weatherArrayList);
//
//                    }
//
////                    weatherAdapter.notifyDataSetChanged();



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

    public JSONArray getHourArray(){

        return hourArray;

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

    private boolean isLocationEnable(Context context){

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);

    }

    private void langANDlong(){

        if(!isLocationEnable(MainActivity.this)){

            Toast.makeText(this, "Enable your location!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            weatherDataLayout.setVisibility(View.GONE);

        } else {

            cityName = getCityName(location.getLongitude(), location.getLatitude());
            progressBar.setVisibility(View.GONE);
            weatherDataLayout.setVisibility(View.VISIBLE);

        }



    }

    private void featuredRecycler(){

        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

//        featuredLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Hat"));
//        featuredLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Jacket"));
//        featuredLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Trousers"));
//        featuredLocations.add(new FeaturedHelperClass(R.drawable.hanger, "Boots"));

        featuredLocations.add(new FeaturedHelperClass(R.drawable.jacket_winter));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.hoodie));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.jeans));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.boots));

        adapter = new FeaturedAdapter(featuredLocations);
        featuredRecycler.setAdapter(adapter);

    }




    private final OnFetchNewsDataListener<NewsApiResponse> listener = new OnFetchNewsDataListener<NewsApiResponse>() {
        @Override
        public void onFetchData(List<NewsHeadlines> list, String message) {

            showNews(list);

        }

        @Override
        public void onError(String message) {

        }
    };

    private void showNews(List<NewsHeadlines> list) {

        fashNewsRecycler.setHasFixedSize(true);
        fashNewsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        _adapter = new fashionNewsAdapter(MainActivity.this, list, MainActivity.this);
        fashNewsRecycler.setAdapter(_adapter);


    }

    private void newsRecycler(){

        NewsRequestManager manager = new NewsRequestManager(MainActivity.this);
        manager.getNewsHeadlines(listener, "men's fashion");




//        fashionArrayList.add(new fashionNewsHelperClass(R.drawable.article_2, "Finally, Sexy Clothes For Men", "When COVID first hit back in early 2020, I remember several friends texting me their cozy sweatsuit fits in the weeks that followed. I, however, went a more impractical dressing route. Feeling unsexier than ever—an impending sense of doom will do that to you—I ordered a fancy..."));
//        fashionArrayList.add(new fashionNewsHelperClass(R.drawable.article_1, "How J Balvin Stole the Show at Dior Men’s", "Anyone looking to make a splash during Paris Fashion Week could take a page from J Balvin’s playbook. The Colombian singer was a welcome presence in the front row of Kim Jones’ Dior Men’s show last Friday. Clad in a cream bomber jacket, khakis, and blue pin-striped shirt..."));
//        fashionArrayList.add(new fashionNewsHelperClass(R.drawable.article_3, "How Letterman Jackets Are Taking Over Paris Streetstyle", "It’s time to dust off your old letterman jackets, because the collegiate look is officially back—but with a cool new upgrade. At Louis Vuitton’s fall menswear show models and guests alike sported colorful varsity jackets, some in neon hues, bearing the house’s initials..."));
//
////        _adapter = new fashionNewsAdapter(fashionArrayList);
//        fashNewsRecycler.setAdapter(_adapter);

    }

    @Override
    public void OnNewsClick(NewsHeadlines headlines) {

        Intent i = new Intent(MainActivity.this, ChosenNews.class);
        i.putExtra("news", headlines);
        startActivity(i);
        finish();

    }



}