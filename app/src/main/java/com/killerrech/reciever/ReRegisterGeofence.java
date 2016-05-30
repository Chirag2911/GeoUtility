package com.killerrech.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by cratuz on 15/11/15.
 */
public class ReRegisterGeofence extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent serviceIntent = new Intent(context, RegisterGeofenceService.class);
            context.startService(serviceIntent);
        }

    }
}
