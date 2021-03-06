package com.sayor.org.cutmypie.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sayor.org.cutmypie.R;
import com.sayor.org.cutmypie.models.FoodData;

import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{

    private ConnectivityManager cm;
    private NetworkInfo ni;
    private FoodData foodData;
    byte[] bytes;
    Location currentLocation;
    Marker marker;
    ProgressDialog progressDialog;
    TextView textView1, textView2;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching map data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        ParseAnalytics.trackAppOpened(getIntent());

        onBackPressed();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView1.setText(ParseUser.getCurrentUser().getUsername());
                textView2.setText(ParseUser.getCurrentUser().getEmail());
            }
        });
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Proceed to post leftovers", Snackbar.LENGTH_LONG)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(MapsActivity.this, PostActivity.class);
                                startActivity(i);
                            }
                        }).show();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);

        //SystemClock.sleep(5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_post) {
            Intent i = new Intent(this, PostActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodData");

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        currentLocation = locationManager.getLastKnownLocation(bestProvider);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 13));

        ni = cm.getActiveNetworkInfo();

        if(ni!=null && ni.isConnectedOrConnecting()==true) {
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        for (ParseObject parseObject : list) {
                            marker = googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(parseObject.getDouble("lat"), parseObject.getDouble("lon")))
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker))
                                    .title(parseObject.getString("fooddesc"))
                                    .snippet(parseObject.getString("ownername")));
                            //googleMap.setInfoWindowAdapter(new CustomInfoAdapter(getLayoutInflater(), parseObject.getBytes("photo")));
                            dropPinEffect(marker);
                            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    Intent i = new Intent(MapsActivity.this, DetailsActivity.class);
                                    i.putExtra("marker", marker.getTitle());
                                    startActivity(i);
                                }
                            });
                            ParseFile parseFile = parseObject.getParseFile("photo");
                            try {
                                bytes = parseFile.getData();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            FoodData foodData = new Select().from(FoodData.class).where("fooddesc=?",parseObject.getString("fooddesc")).executeSingle();

                            if(foodData==null) {
                                foodData = new FoodData(parseObject.getString("fooddesc"), parseObject.getString("feedcap"), parseObject.getString("timeexp"), parseObject.getDouble("lat"), parseObject.getDouble("lon"), bytes, parseObject.getString("ownerid"), parseObject.getString("ownername"));
                                foodData.save();
                            }
                        }
                        progressDialog.dismiss();
                    } else {
                        Log.d("item", "Error:" + e.getMessage());
                    }
                }
            });
        }
        else{
            List<FoodData> foodDataList = new Select().from(FoodData.class).execute();
                for (FoodData foodData : foodDataList) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(foodData.getLat(), foodData.getLon()))
                            .title(foodData.getFooddesc()));
                }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_conv) {
            Intent i = new Intent(this, ConversationActivity.class);
            startActivity(i);
        }
        if (id == R.id.nav_logout) {
            Intent i = new Intent(this, LoginActivity.class);
            ParseUser.logOut();
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
