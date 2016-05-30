package com.killerrech.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.killerrech.Utility.NetworkUtil;
import com.killerrech.constant.ConstantsForSharedPrefrences;
import com.killerrech.sharedPrefrences.SharedPrefrence;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        NetworkUtil.getConnectivityStatusBoolean(context);
        System.out.println("***********************************************0000" + SharedPrefrence.getBooleanSharedPrefernces(context, ConstantsForSharedPrefrences.IS_NETWORK_AVAILABLE));

       // Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }
}