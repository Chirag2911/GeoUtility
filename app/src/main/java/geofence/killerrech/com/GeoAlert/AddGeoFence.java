package geofence.killerrech.com.GeoAlert;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.killerrech.Geofence.Constants;
import com.killerrech.Geofence.GeofenceErrorMessages;
import com.killerrech.Geofence.GeofenceTransitionsIntentService;
import com.killerrech.Geofence.GpsTrackingService;
import com.killerrech.Utility.NetworkUtil;
import com.killerrech.constant.ConstantsForSharedPrefrences;
import com.killerrech.database.TablesController;
import com.killerrech.model.Geofencemodel;
import com.killerrech.sharedPrefrences.SharedPrefrence;

import java.util.ArrayList;
import java.util.List;


public class AddGeoFence extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    // Declaring Your View and Variables
    Toolbar toolbar;
    Toast mToast;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = new CharSequence[3];
    int Numboftabs = 3;


    protected static final String TAG = "MainActivity";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    public boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;
    ProgressDialog pDialog;


    @Override
    protected void onResume() {
        super.onResume();

        if (!SharedPrefrence.getBooleanSharedPrefernces(this, ConstantsForSharedPrefrences.IS_GPS_AVAILABLE)) {
            GpsTrackingService.showDialog(this);
        }
        NetworkUtil.getConnectivityStatusBoolean(this);

        if (!SharedPrefrence.getBooleanSharedPrefernces(this, ConstantsForSharedPrefrences.IS_NETWORK_AVAILABLE)) {
            GpsTrackingService.showNetworkDialog(this);
        }

    }

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

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
//    StartAppAd.showSplash(this, savedInstanceState);

//    StartAppSDK.init(this, AppConstant.account_Id, AppConstant.app_Id,
//            false);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geo_fence);
        getCallingPermission();



        // Creating The Toolbar and setting it as the Toolbar for the activity
        mToast = Toast.makeText(AddGeoFence.this, "This is a toast.", Toast.LENGTH_SHORT);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Titles[0] = getResources().getString(R.string.auto_search);
        Titles[1] = getResources().getString(R.string.place_marker);
        Titles[2] = getResources().getString(R.string.current_location);

        initProgressDialog();
        NetworkUtil.setToast(this, getResources().getString(R.string.toast_choose_tab));


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pager.getWindowToken(), 0);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mToast.cancel();
                        mToast.makeText(AddGeoFence.this, getResources().getString(R.string.toast_auto_search), Toast.LENGTH_SHORT).show();
//                        NetworkUtil.setLongToast(AddGeoFence.this, getResources().getString(R.string.toast_auto_search));
                        break;
                    case 1:
                        mToast.cancel();
                        mToast.makeText(AddGeoFence.this, getResources().getString(R.string.toast_place_marker), Toast.LENGTH_SHORT).show();
//                        NetworkUtil.setLongToast(AddGeoFence.this, getResources().getString(R.string.toast_place_marker));
                        break;
                    case 2:
                        mToast.cancel();
                        mToast.makeText(AddGeoFence.this, getResources().getString(R.string.toast_current_location), Toast.LENGTH_SHORT).show();
//                        NetworkUtil.setLongToast(AddGeoFence.this, getResources().getString(R.string.toast_current_location));
                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildGoogleApiClient();


//        mInterstitialAd.setAdUnitId(getResources().getString(R.string.full_ad_unit_id));

//        mAdView.loadAd(adRequest);
//
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                requestNewInterstitial();
//
//            }
//
//        });

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
//        requestNewInterstitial();

    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()

                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofence(geofence);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */


    protected synchronized void buildGoogleApiClient     () {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {

        super.onStart();

        getCallingPermission();


        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void addGeofencesButtonHandler(Geofence geofence) {

        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(,"Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(geofence),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(Geofence geofence) {
        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this,"NOtConnected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            List<String> removingGeofenceId = new ArrayList<String>();
            removingGeofenceId.add(geofence.getRequestId());
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient
                    , removingGeofenceId
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * <p>
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     * removeGeofences() get called.
     * TablesController tbController;
     */

    TablesController tbController;

    public void onResult(Status status) {
        if (status.isSuccess()) {

            if (mGeofencesAdded) {
                tbController = TablesController.getTablesController(this);
                tbController.open();
                Geofencemodel geofence;
                geofence = ManualSearchFragment.GeofenceTOAdd;

                long insertedid = tbController.
                        addGeoLocation(geofence.getId(), geofence.getLatitude() + "", geofence.getLongitude() + "", geofence.getRadius(), geofence.getAddress(), geofence.getGeoName());


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();

                        Toast.makeText(AddGeoFence.this, getResources().getString(R.string.Geofence_create), Toast.LENGTH_SHORT).show();


                    }
                });


                Intent mintent = new Intent(this, Settings.class
                );

                mintent.putExtra("Key_GeoId", geofence.getId());
                mintent.putExtra("Key_GeoName", geofence.getAddress());
                startActivityForResult(mintent, Constants.ADD_GEOFENCE_REQUEST);
                //  this.finish();
            }
//            Toast.makeText(
//                    this,
//                    getString(mGeofencesAdded ? R.string.geofences_added :
//                            R.string.geofences_removed),
//                    Toast.LENGTH_SHORT
//            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hidepDialog();
                    Toast.makeText(AddGeoFence.this, getResources().getString(R.string.Geofence_notcreate), Toast.LENGTH_SHORT).show();

                }
            });

            Log.e(TAG, errorMessage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ADD_GEOFENCE_REQUEST && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onBackPressed() {


//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
        super.onBackPressed();
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

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){

            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION_LOCATION_REQUEST_CODE);

        } else {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION_LOCATION_REQUEST_CODE);
        }
    }


    public void getCallingPermission() {
        if (!checkPermission()) {
            if(!SharedPrefrence.getBooleanSharedPrefernces(this,"bool")) {
                SharedPrefrence.saveBooleanSharedPrefernces(this, "bool", true);
                requestPermission();
            }
        }}
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("=======inside on onRequest Permission Result");
        SharedPrefrence.saveBooleanSharedPrefernces(this, "bool", false);

        switch (requestCode) {
            case Constants.PERMISSION_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this,GpsTrackingService.class));

                } else {
                    getCallingPermission();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}