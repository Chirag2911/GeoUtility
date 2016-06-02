package com.killerrech.Geofence;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

import com.killerrech.constant.ConstantsForSharedPrefrences;
import com.killerrech.database.TablesController;
import com.killerrech.reciever.RegisterGeofenceService;
import com.killerrech.sharedPrefrences.SharedPrefrence;

import java.util.List;

import geofence.killerrech.com.GeoAlert.BaseActivity;
import geofence.killerrech.com.GeoAlert.R;

/**
 * Created by cratuz on 13/11/15.
 */
public class GpsTrackingService extends Service {

    private View mView;

    LocationListener locationListener;


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationListener = new MyLocationListener();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (BaseActivity.baseContext!=null)
//                (BaseActivity.baseContext).getCallingPermission();
            return -1;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this.locationListener);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((WindowManager)getSystemService(WINDOW_SERVICE)).removeView(mView);
        mView = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location locFromGps) {
            // called when the listener is notified with a location update from the GPS
            System.out.println("========================onLocationChanged");


        }

        @Override
        public void onProviderDisabled(String provider) {
            // called when the GPS provider is turned off (user turning off the GPS on the phone)

            System.out.println("========================Disabledd");
            SharedPrefrence.saveBooleanSharedPrefernces(GpsTrackingService.this, ConstantsForSharedPrefrences.IS_GPS_AVAILABLE, false);
            TablesController tbController = TablesController.getTablesController(GpsTrackingService.this);
            tbController.open();
            Cursor cr=tbController.getAllGeoLocation();
            if(cr.getCount()>0) {
                if (isAppIsInBackground(GpsTrackingService.this))
                    sendNotification("");
            }





        }

        @Override
        public void onProviderEnabled(String provider) {
            // called when the GPS provider is turned on (user turning on the GPS on the phone)
            System.out.println("========================Enabledd");
            SharedPrefrence.saveBooleanSharedPrefernces(GpsTrackingService.this, ConstantsForSharedPrefrences.IS_GPS_AVAILABLE, true);

            startService(new Intent(GpsTrackingService.this, RegisterGeofenceService.class));
            clearNotification();


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // called when the status of the GPS provider changes
            System.out.println("========================status changed" + status + "  " + provider);

        }
    }







    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
    private void sendNotification(String notificationDetails) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
// use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(this)
                .setContentTitle("GeoAlert")
                .setContentText("Activate your location service, for getting location based working of applicaation")
                .setSmallIcon(R.drawable.warning_amber_96x96)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(5, n);

    }

    public void clearNotification() {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(5);
    }

    public static void showDialog(final Context contxt) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                contxt);

        // set title
        alertDialogBuilder.setTitle("Info");

        // set dialog message
        alertDialogBuilder
                .setMessage(contxt.getResources().getString(R.string.gps_network_not_enabled))
                .setCancelable(false)
                .setPositiveButton(contxt.getResources().getString(R.string.open_location_settings),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        contxt.startActivity(myIntent);
                        dialog.cancel();

                    }
                })
                .setNegativeButton(contxt.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        if (SharedPrefrence.getBooleanSharedPrefernces(contxt, ConstantsForSharedPrefrences.IS_GPS_AVAILABLE)) {

                            dialog.cancel();


                        }else {

                            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                                    .getInstance(contxt);
                            localBroadcastManager.sendBroadcast(new Intent(
                                    "com.durga.action.close"));

//                            showDialog(contxt);
                        }
                        }
                        }

                    );

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }



    public static void showNetworkDialog(final Context contxt) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                contxt);

        // set title
        alertDialogBuilder.setTitle("Info");

        // set dialog message
        alertDialogBuilder
                .setMessage(contxt.getResources().getString(R.string.network_not_enabled))
                .setCancelable(false)
                .setPositiveButton(contxt.getResources().getString(R.string.open_settings),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent myIntent = new Intent( Settings.ACTION_SETTINGS);
                        contxt.startActivity(myIntent);
                        dialog.cancel();

                    }
                })
                .setNegativeButton(contxt.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        System.out.println("***********************************************888" + SharedPrefrence.getBooleanSharedPrefernces(contxt, ConstantsForSharedPrefrences.IS_NETWORK_AVAILABLE));


                            if (SharedPrefrence.getBooleanSharedPrefernces(contxt, ConstantsForSharedPrefrences.IS_NETWORK_AVAILABLE)) {

                                dialog.cancel();
                            } else {

                                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                                        .getInstance(contxt);
                                localBroadcastManager.sendBroadcast(new Intent(
                                        "com.durga.action.close"));


//                                showNetworkDialog(contxt);
                            }
                        }
                    }

                    );

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
    public static void showPermissionNotAllow(final Activity contxt,DialogInterface.OnClickListener positiveButton,DialogInterface.OnClickListener onNegativeAction) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                contxt);

        // set title
        alertDialogBuilder.setTitle("Info");

        // set dialog message
        alertDialogBuilder
                .setMessage("Location permission is allow, please provide it from geoalert settings")
                .setCancelable(false)
                .setPositiveButton(contxt.getResources().getString(R.string.open_settings),positiveButton)
                .setNegativeButton(contxt.getResources().getString(R.string.Cancel), onNegativeAction);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }


    }
