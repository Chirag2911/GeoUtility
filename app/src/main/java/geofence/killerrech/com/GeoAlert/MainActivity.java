
package geofence.killerrech.com.GeoAlert;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.killerrech.Geofence.Constants;
import com.killerrech.Geofence.GpsTrackingService;
import com.killerrech.Utility.NetworkUtil;
import com.killerrech.adapter.ListAdapter;
import com.killerrech.constant.ConstantsForSharedPrefrences;
import com.killerrech.database.DBHelper;
import com.killerrech.database.TablesController;
import com.killerrech.model.Geofencemodel;
import com.killerrech.reciever.RegisterGeofenceService;
import com.killerrech.sharedPrefrences.SharedPrefrence;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback, ListAdapter.MyClickListener ,ActivityCompat.OnRequestPermissionsResultCallback{
    private MapFragment googleMap;
    // private GeofenceAdapter madapter;
    private ListAdapter madapter;
    private ArrayList<Geofencemodel> mgeo_list;
    RecyclerView mainListView;
    TablesController tbController;
    private FloatingActionButton fab;
    MarkerOptions markerOptions;
    int radius;
    LatLng latLng;
    Animation animfade;
    RelativeLayout appbarLayout;
    CoordinatorLayout rootLay;
    FrameLayout mFrameLay;
    private AdView mAdView;
    private TextView memptyview;
    private InterstitialAd mInterstitialAd;
    private boolean isFirstTimeCheckSdk23PermissionCheck=false;

    private static String[] PERMISSIONS_CONTACT = {
            Manifest.permission.ACCESS_FINE_LOCATION,

    };


    public void deleteFromDb(int position) {
        new deleteElement().execute(position + "");
    }

    public void editGeofence(int position) {
        Intent mintent = new Intent(this, Settings.class);
        mintent.putExtra("Key_GeoId", mgeo_list.get(position).getId());
        mintent.putExtra("Key_GeoName", mgeo_list.get(position).getGeoName());
        mintent.putExtra("editflagkey", true);
        startActivity(mintent);

    }


    private class deleteElement extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showpDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent intent = new Intent(MainActivity.this, RegisterGeofenceService.class);
            intent.putExtra("geoId", s);
            startService(intent);

            if( madapter.getItemCount()>0){

                memptyview.setVisibility(View.GONE);
            }else{
                memptyview.setVisibility(View.VISIBLE);
            }
            madapter.notifyDataSetChanged();
            inItMap();
            hidepDialog();

        }

        @Override
        protected String doInBackground(String... params) {
            int position = Integer.parseInt(params[0]);
            String response = mgeo_list.get(position).getId();
            mgeo_list.remove(position);
            return response;
        }
    }

    @Override
    public void onItemClick(int position, View v) {
        showFullAd();
        setGoogleMap(mgeo_list.get(position).getLatitude(), mgeo_list.get(position).getLongitude(), Integer.parseInt(mgeo_list.get(position).getRadius()));

    }

    interface AddItemtGeofence {
        public void onAdd();
    }

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.durga.action.close")){
                finish();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_geofence);
        NetworkUtil.setLongToast(this, getResources().getString(R.string.toast_add_location));

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.durga.action.close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
        memptyview=(TextView)findViewById(R.id.emptyview);
        googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map));
        initProgressDialog();

        googleMap.getMapAsync(this);
        mgeo_list = new ArrayList<>();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tbController = TablesController.getTablesController(this);
        tbController.open();

        mainListView = (RecyclerView) findViewById(R.id.mlistgeofenceplaces);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mainListView.setLayoutManager(llm);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mintent = new Intent(MainActivity.this, AddGeoFence.class
                );
                startActivityForResult(mintent, Constants.ADD_GEOFENCE_REQUEST);
            }
        });
        new initializeScreen().execute();


        mAdView = (AdView) findViewById(R.id.ad_view);
        mInterstitialAd = new InterstitialAd(this);

        AdRequest adRequest = new AdRequest.Builder()

                .build();

        mInterstitialAd.setAdUnitId(getResources().getString(R.string.full_ad_unit_id));

        mAdView.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

            }

        });

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdLoaded() {
                // TODO Auto-generated method stub
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // TODO Auto-generated method stub
                super.onAdFailedToLoad(errorCode);
                mAdView.setVisibility(View.GONE);

            }
        });
        requestNewInterstitial();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()

                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    private int i;

    private void showFullAd() {
        if (i == 3) {
            if (mInterstitialAd.isLoaded()) {
                i = 0;
                mInterstitialAd.show();
            } else {
                i = 0;
            }

        }
        i++;
    }


    ProgressDialog pDialog;


    private void initProgressDialog() {
        pDialog = new ProgressDialog(this, R.style.TransparentProgressDialog);
        pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_background));
        pDialog.setCancelable(false);

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();


        NetworkUtil.IsGpsEnable(this);
            if (!SharedPrefrence.getBooleanSharedPrefernces(this, ConstantsForSharedPrefrences.IS_GPS_AVAILABLE)) {
                GpsTrackingService.showDialog(this);
            }
            NetworkUtil.getConnectivityStatusBoolean(this);
            if (!SharedPrefrence.getBooleanSharedPrefernces(this, ConstantsForSharedPrefrences.IS_NETWORK_AVAILABLE)) {
                GpsTrackingService.showNetworkDialog(this);
            }
    }


    private class initializeScreen extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showpDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  madapter = new GeofenceAdapter(MainActivity.this, mgeo_list);
            madapter = new ListAdapter(mgeo_list, MainActivity.this);

            ((TextView) findViewById(R.id.imageNearBy)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, NearBy.class));
                }
            });
            madapter.setOnItemClickListener(MainActivity.this);

            if(mgeo_list.size()>0){
                memptyview.setVisibility(View.GONE);
            }else{
                memptyview.setVisibility(View.VISIBLE);
            }
            mainListView.setAdapter(madapter);

            hidepDialog();
        }

        @Override
        protected String doInBackground(String... params) {


            inItList();
            return null;
        }
    }

    public void inItList() {
        Cursor cr = tbController.getAllGeoLocation();

        if (cr.getCount() > 0) {
            cr.moveToFirst();
            Log.d("TAGCount", cr.getCount() + "");


            while (!cr.isAfterLast()) {

                Geofencemodel gd = new Geofencemodel();
                gd.setLatitude(Double.parseDouble(cr.getString(cr.getColumnIndex(DBHelper.GEO_LATITUDE))));
                gd.setLongitude(Double.parseDouble(cr.getString(cr.getColumnIndex(DBHelper.GEO_LONGITUDE))));
                gd.setRadius(cr.getString(cr.getColumnIndex(DBHelper.GEO_RADIUS)));
                gd.setAddress(cr.getString(cr.getColumnIndex(DBHelper.GEO_Address)));
                gd.setGeoName(cr.getString(cr.getColumnIndex(DBHelper.GEO_NAME)));
                gd.setId(cr.getString(cr.getColumnIndex(DBHelper.GEO_ID)));
                mgeo_list.add(gd);

                cr.moveToNext();
            }
        }
    }


    GoogleMap map;

    @Override
    public void onMapLoaded() {


        new initializeMap().execute();
//        LatLngBounds bounds = builder.build();
//        int padding = 0; // offset from edges of the map in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//        map.animateCamera(cu);


    }


    private class initializeMap extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showpDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            inItMap();
            hidepDialog();
        }

        @Override
        protected String doInBackground(String... params) {


            return null;
        }
    }


    public void inItMap() {

        map.clear();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
        map.setBuildingsEnabled(true);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < mgeo_list.size(); i++) {

            latLng = new LatLng(mgeo_list.get(i).getLatitude(), mgeo_list.get(i).getLongitude());

            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            markerOptions.title(mgeo_list.get(i).getGeoName() + "," + "radius =" + mgeo_list.get(i).getRadius() + " km");

            map.addMarker(markerOptions);


            map.addCircle(new CircleOptions().center(latLng).radius(Integer.parseInt(mgeo_list.get(i).getRadius()) * 1000)
                    .strokeColor(Color.BLACK).fillColor(0x30ff0000).strokeWidth(2));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));


//            builder.include(markerOptions.getPosition());


        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ADD_GEOFENCE_REQUEST && resultCode == RESULT_OK) {
            if (madapter != null) {

                if (ManualSearchFragment.GeofenceTOAdd != null) {
                    mgeo_list.add(ManualSearchFragment.GeofenceTOAdd);
                    latLng = new LatLng(ManualSearchFragment.GeofenceTOAdd.getLatitude(), ManualSearchFragment.GeofenceTOAdd.getLongitude());

                    markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(ManualSearchFragment.GeofenceTOAdd.getGeoName() + ",radius = " + ManualSearchFragment.GeofenceTOAdd.getRadius()+" km");
                    map.addCircle(new CircleOptions().center(latLng).radius(Integer.parseInt(ManualSearchFragment.GeofenceTOAdd.getRadius()) * 1000)
                            .strokeColor(Color.BLACK).fillColor(0x30ff0000).strokeWidth(2));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    map.addMarker(markerOptions);
                   if( madapter.getItemCount()>0){

                           memptyview.setVisibility(View.GONE);
                       }else{
                           memptyview.setVisibility(View.VISIBLE);
                       }

                    madapter.notifyDataSetChanged();
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setGoogleMap(double mlat, double mlong, int r) {
        if (map != null) {
            latLng = new LatLng(mlat, mlong);


            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }

    }

}
