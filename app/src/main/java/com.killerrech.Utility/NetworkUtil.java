package com.killerrech.Utility;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.killerrech.constant.ConstantsForSharedPrefrences;
import com.killerrech.sharedPrefrences.SharedPrefrence;

public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                boolean status=isOnlineWifi(context);
                if (status)
                return TYPE_WIFI;
            }

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                boolean status=isOnline(context);
                if (status)
                return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

private static boolean isOnlineWifi(Context context) {
    ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    if (mWifi.isConnected()) {
        // Do whatever
        return  true;
    }
    return false;
}

    private static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }


    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
    public static boolean getConnectivityStatusBoolean(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        boolean status = true;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = true;
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = true;
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {

            status = false;
        }
        SharedPrefrence.saveBooleanSharedPrefernces(context, ConstantsForSharedPrefrences.IS_NETWORK_AVAILABLE, status);
        return status;
    }

    public static void setToast(Context context,String st){
        Toast.makeText(context,st,Toast.LENGTH_SHORT).show();
    }

    public static void setLongToast(Context context,String st){
        Toast.makeText(context,st,Toast.LENGTH_LONG).show();
    }



    public static boolean IsGpsEnable(Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {}
        SharedPrefrence.saveBooleanSharedPrefernces(context, ConstantsForSharedPrefrences.IS_GPS_AVAILABLE, gps_enabled);

        return gps_enabled;
    }

}