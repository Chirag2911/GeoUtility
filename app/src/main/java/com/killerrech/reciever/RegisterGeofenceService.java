package com.killerrech.reciever;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.killerrech.Geofence.GeofenceErrorMessages;
import com.killerrech.Geofence.GeofenceTransitionsIntentService;
import com.killerrech.database.DBHelper;
import com.killerrech.database.TablesController;
import com.killerrech.model.Geofencemodel;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RegisterGeofenceService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    TablesController tbController;

    private PendingIntent mGeofencePendingIntent;
    protected static final String TAG = "RegistergeofenceService";

    protected GoogleApiClient mGoogleApiClient;


    public RegisterGeofenceService() {
        super("RegisterGeofenceService");
    }
    private ArrayList<Geofencemodel> mgeo_list=new ArrayList<>();
    boolean isHavingIntent=false;

    @Override
    protected void onHandleIntent(Intent intent) {
        buildGoogleApiClient();
        tbController = TablesController.getTablesController(this);
        tbController.open();
        String geoIdToRemove="";
        try{
           geoIdToRemove=intent.getStringExtra("geoId");
        }catch (Exception e){
            geoIdToRemove="";
        }

        System.out.println("====================inside service geoIdToRemove:::"+geoIdToRemove);


        if (geoIdToRemove!=null && !geoIdToRemove.equalsIgnoreCase("")){
            Geofencemodel gd = new Geofencemodel();

            gd.setId(geoIdToRemove);
            mgeo_list.add(gd);
            isHavingIntent=true;
        }else {


            Cursor cr = tbController.getAllGeoLocation();
            if (cr.getCount() < 1)
                return;
            cr.moveToFirst();

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








    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }


    @Override
    public void onConnected(Bundle connectionHint) {
        System.out.println("====================onConnected");

        if (isHavingIntent){
            List<String> removingGeofenceId=new ArrayList<>();
            for (int i=0;i<mgeo_list.size();i++){
                removingGeofenceId.add(mgeo_list.get(i).getId());
            }

            if (removingGeofenceId.size()>0) {
                removeGeofencesButtonHandler(removingGeofenceId);
                tbController.removeGeoLocation(removingGeofenceId.get(0));
                System.out.println("====================removing from db and server");

            }
        }else {

            List<String> removingGeofenceId = new ArrayList<>();
            List<Geofence> addGeofenc = new ArrayList<>();
            for (int i = 0; i < mgeo_list.size(); i++) {
                removingGeofenceId.add(mgeo_list.get(i).getId());
                addGeofenc.add(mgeo_list.get(i).builtInGeofence());
            }


            if (removingGeofenceId.size() > 0) {
                removeGeofencesButtonHandler(removingGeofenceId);
            }
            if (addGeofenc.size() > 0) {
                addGeofencesButtonHandler(addGeofenc);
            }
        }

        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        System.out.println("====================onConnectionFailed"+result.getErrorCode());

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        System.out.println("====================connection suspended"+mGoogleApiClient.isConnected());

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
    public void addGeofencesButtonHandler(List<Geofence> geofence) {
        System.out.println("====================inside addGeofence"+mGoogleApiClient.isConnected());

        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(,"Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            System.out.println("====================inside addGeofence");

            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    geofence,
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
    public void removeGeofencesButtonHandler(List<String> geofence) {
        System.out.println("====================inside remove");
        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this,"NOtConnected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            System.out.println("====================inside remove");

            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    geofence
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
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.

            System.out.println("========================on Result:::"+status.isSuccess()+"  "+status.getStatusMessage());


//            mGeofencesAdded = !mGeofencesAdded;
//            SharedPreferences.Editor editor = mSharedPreferences.edit();
//            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
//            editor.commit();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
//            setButtonsEnabledState();

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
            Log.e(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent insgetGeofencePendingIntentide this PendingIntent whenever a geofence transition occurs for the
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






}
