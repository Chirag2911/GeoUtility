package geofence.killerrech.com.GeoAlert;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.killerrech.Geofence.Constants;
import com.killerrech.Geofence.GpsTrackingService;
import com.killerrech.Utility.NearByParser;
import com.killerrech.Utility.NetworkUtil;
import com.killerrech.constant.ConstantsForSharedPrefrences;
import com.killerrech.model.NearByModel;
import com.killerrech.sharedPrefrences.SharedPrefrence;

import java.util.ArrayList;

/**
 * Created by Chiggy on 15/11/15.
 */
public class NearBy extends BaseActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {
    private MapFragment googleMap;
    private String mspinerContent[] = {"food", "hospital", "atm", "police", "gas_station", "pharmacy", "shopping_mall", "night_club"};
    private Spinner mspinner;
    MarkerOptions markerOptions;
    Bitmap mImage = null;
    LocationManager locationManager;
    int pos;
    LatLng latLng1;
    String nearBy = "";

    int radius;
    LatLng latLng;
    ProgressDialog pDialog;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    private void initProgressDialog() {
        pDialog = new ProgressDialog(this, R.style.TransparentProgressDialog);
        pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.custom_progress_background));
        pDialog.setCancelable(false);

    }

    public void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCallingPermission();
        mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Snackbar.make(view, parent.getItemAtPosition(position).toString(), Snackbar.LENGTH_SHORT).show();

                Location mloc = getLocation();
                if (mloc == null)
                    return;
                String Url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + mloc.getLatitude() + "," + mloc.getLongitude() + "&rankby=distance&types=" + parent.getItemAtPosition(position).toString() + "&key=AIzaSyCJCgviS3bWZUegq4FaW1EdCWfTzq7zRjk";
                latLng1 = new LatLng(mloc.getLatitude(), mloc.getLongitude());

                final PlaceTask mplacetask = new PlaceTask();
                mplacetask.execute(Url);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mplacetask.getStatus() == AsyncTask.Status.RUNNING) {
                            mplacetask.cancel(true);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hidepDialog();

                                    Snackbar.make(mspinner, getResources().getString(R.string.Network_slow), Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }, 30000);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (!SharedPrefrence.getBooleanSharedPrefernces(this, ConstantsForSharedPrefrences.IS_GPS_AVAILABLE)) {
            GpsTrackingService.showDialog(this);
        }
        NetworkUtil.getConnectivityStatusBoolean(this);

        if (!SharedPrefrence.getBooleanSharedPrefernces(this, ConstantsForSharedPrefrences.IS_NETWORK_AVAILABLE)) {
            GpsTrackingService.showNetworkDialog(this);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby);
        mspinner = (Spinner) findViewById(R.id.nearbyspinner);
//        mBaaner = (Banner)      findViewById(R.id.startAppBanner);

        initProgressDialog();
//        mBaaner.showBanner();
        try {
            nearBy = getIntent().getStringExtra("nearBy");
            refreshSpinnerList();


        } catch (Exception e) {

        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mspinerContent);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));

        googleMap.getMapAsync(this);

        mspinner.setAdapter(dataAdapter);


//        mAdView = (AdView) findViewById(R.id.ad_view);
        mInterstitialAd = new InterstitialAd(NearBy.this);

        AdRequest adRequest = new AdRequest.Builder()

                .build();

        mInterstitialAd.setAdUnitId(getResources().getString(R.string.full_ad_unit_id));

//        mAdView.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

            }

        });

//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//
//            }
//
//            @Override
//            public void onAdLoaded() {
//                // TODO Auto-generated method stub
//                super.onAdLoaded();
//                mAdView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                // TODO Auto-generated method stub
//                super.onAdFailedToLoad(errorCode);
//                mAdView.setVisibility(View.GONE);
//
//            }
//        });
        requestNewInterstitial();

    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()

                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    private void refreshSpinnerList() {
        String[] active = nearBy.split("-");
        mspinerContent = null;
        mspinerContent = new String[active.length];
        for (int i = 0; i < active.length; i++) {
            mspinerContent[i] = active[i];
        }
    }

    public Location getLocation() {
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (BaseActivity.baseContext != null)
//                (BaseActivity.baseContext).getCallingPermission();
            return null;
        }
        return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);


    }


    @Override
    public void onMapLoaded() {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomGesturesEnabled(true);
    }

    GoogleMap map;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);


    }


    class PlaceTask extends AsyncTask {
        NearByParser obj;
        ArrayList<NearByModel> obj1;

        @Override
        protected Object doInBackground(Object[] params) {
            obj = new NearByParser();


            obj1 = obj.getNearBYListFromJson(obj.getJSONFromUrl(params[0].toString()));

            return "";

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showpDialog();
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            map.clear();
            map.setMyLocationEnabled(true);
            map.setTrafficEnabled(true);
            for (int i = 0; i < obj1.size(); i++) {
                latLng = new LatLng(obj1.get(i).getLatitude(), obj1.get(i).getLongitude());
                markerOptions = new MarkerOptions();

                markerOptions.position(latLng);
                markerOptions.title(obj1.get(i).getName() + "," + obj1.get(i).getAddressName());
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(obj1.get(i).getBitmap()));


                map.addCircle(new CircleOptions().center(latLng).radius(50)
                        .strokeColor(Color.BLACK).fillColor(getResources().getColor(R.color.trans_green)).strokeWidth(2));

                map.addMarker(markerOptions);

            }

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15));


            hidepDialog();


        }


    }

    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    public void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){

            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION_LOCATION_REQUEST_CODE);

        } else {

            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},Constants.PERMISSION_LOCATION_REQUEST_CODE);
        }
    }


    public void getCallingPermission() {
        if (!checkPermission()) {

            requestPermission();
        }}
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("=======inside on onRequest Permission Result");

        switch (requestCode) {
            case Constants.PERMISSION_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this,GpsTrackingService.class));
                } else {

                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



}



