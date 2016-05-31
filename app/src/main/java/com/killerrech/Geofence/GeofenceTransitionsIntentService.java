/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.killerrech.Geofence;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.killerrech.alert.AlarmAlertBroadcastReciever;
import com.killerrech.database.DBHelper;
import com.killerrech.database.TablesController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import geofence.killerrech.com.GeoAlert.MainActivity;
import geofence.killerrech.com.GeoAlert.R;

/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService
      //  implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener
{
   // private TextToSpeech mTts;
    private String spokenText;
    protected static final String TAG = "GeofenceTransitionsIS";
    int geofenceTransition;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
       // mTts=new TextToSpeech(this,this);
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("inside GeoTransition intent service");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            System.out.println("inside GeoTransition intent service error");

            return;
        }

        // Get the transition type.
         geofenceTransition = geofencingEvent.getGeofenceTransition();
//        sendNotification("inside srvice");
//        sendBigPictureStyleNotification(this,"atm-hospital-food","Dummy location");


        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

//            // Get the transition details as a String.
//            String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                    this,
//                    geofenceTransition,
//                    triggeringGeofences
//            );
//
//            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails);

            doRequiredTaskAtTheGeofence(this, geofenceTransition, triggeringGeofences);

          //  Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    public void doRequiredTaskAtTheGeofence(Context context, int geofenceTransition, List<Geofence> triggeringGeofences) {

        for (Geofence geofence : triggeringGeofences){
            checkForSettings(context, geofenceTransition, geofence);
        }

    }
    TablesController tbController;

    private void checkForSettings(Context context, int geofenceTransition, Geofence geofence) {
        System.out.println("inside check for settings:::");

        String Geo_Id=geofence.getRequestId();
        tbController = TablesController.getTablesController(this);
        tbController.open();
       Cursor cr = tbController.getGeoSettings(Geo_Id);

        if (cr.getCount() > 0) {
            System.out.println("inside check for settings:::"+cr.getCount());

            cr.moveToFirst();
            int profile_in=Integer.parseInt(cr.getString(cr.getColumnIndex(DBHelper.PROFILE_MODE_IN)));
            int profile_out=Integer.parseInt(cr.getString(cr.getColumnIndex(DBHelper.PROFILE_MODE_OUT)));
            boolean enter_notification=Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.NOTIFICATION_ENTER)));
            boolean exit_notification=Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.NOTIFICATION_EXIT)));
            boolean enter_alarm=Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.ALARM_ENTER)));
            boolean exit_alarm=Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.ALARM_EXIT)));
            boolean is_notification=Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.IS_NOTIFICATION)));
            boolean is_alarm=Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.IS_ALARM)));

            String nearBy=(cr.getString(cr.getColumnIndex(DBHelper.GEO_NEARBY_PLACES)));
            Cursor placeInfo=tbController.getGeoLocation(Geo_Id);
            if (placeInfo.getCount()<1)
                return;
            placeInfo.moveToFirst();

            String placeName=(placeInfo.getString(placeInfo.getColumnIndex(DBHelper.GEO_NAME)));
            System.out.println("inside check for settings:::"+placeName);


            if (geofenceTransition== Geofence.GEOFENCE_TRANSITION_ENTER){
                System.out.println("inside check for settings if:::");

                changeDeviceProfile(context,profile_in);
                if (is_notification && enter_notification){
                    sendNotification(placeName);                }
                if (is_alarm && enter_alarm){
                    raiseAlarm(placeName, nearBy, placeName);
                }else
                if (!nearBy.equalsIgnoreCase("") && nearBy!=null){
                    raiseAlarm("",nearBy,placeName);

                }
            }else {
                System.out.println("inside check for settings else:::");

                changeDeviceProfile(context,profile_out);
                if (is_notification && exit_notification){
                    sendNotification(placeName);                }
                if (is_alarm && exit_alarm){
                    raiseAlarm(placeName,"","");
                }

            }




        }
    }

    private void raiseAlarm(String placeName,String nearby, String locName) {
        System.out.println("inside raise alarm:::" + placeName);
        String toSpeech="";
        if (!placeName.equalsIgnoreCase("")) {
         toSpeech = "You have " + getTransitionString(geofenceTransition) + " " + placeName + "\nPress cancel to stop alarm.";
    }
        speakOut(this,toSpeech,nearby,locName);

//        if (isInit){
//            String toSpeech="You have "+getTransitionString(geofenceTransition)+" "+placeName;
//            System.out.println("inside raise alarm text:::"+toSpeech);
//
//            speak(toSpeech);
//        }

    }

    private void changeDeviceProfile(Context context, int profile_in) {
        System.out.println("inside change profile mode:::"+profile_in);
        AudioManager am;
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        switch (profile_in){
            case 0:
                //For Silent mode
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case 1:
                //For Vibrate mode
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case 2:
                //For Normal mode
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            default:
                break;
        }


    }



    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }


    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    boolean isInit;
//    @Override
//    public void onInit(int status) {
//        if (status == TextToSpeech.SUCCESS) {
//            int result = mTts.setLanguage(Locale.US);
//            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
//                isInit = true;
//            }
//        }
//    }
//
//
//
//private void speak(String toSpeech) {
//        if (mTts != null) {
//            mTts.speak(toSpeech, TextToSpeech.QUEUE_FLUSH, null);
//        }
//        }

    @Override
    public void onDestroy() {
//        if (mTts != null) {
//            mTts.stop();
//            mTts.shutdown();
//        }
        super.onDestroy();
    }

//
//    @Override
//    public void onUtteranceCompleted(String utteranceId) {
//       // stopSelf();
//
//    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
//        Intent notificationIntent = new Intent(GeofenceTransitionsIntentService.this, MainActivity.class);
//
//        // Construct a task stack.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Add the main Activity to the task stack as the parent.
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Push the content Intent onto the stack.
//        stackBuilder.addNextIntent(notificationIntent);
//
//        // Get a PendingIntent containing the entire back stack.
//        PendingIntent notificationPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Get a notification builder that's compatible with platform versions >= 4
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//
//        // Define the notification settings.
//        builder.setSmallIcon(R.drawable.ic_cast_dark)
//                // In a real app, you may want to use a library like Volley
//                // to decode the Bitmap.
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
//                        R.drawable.ic_cast_dark))
//                .setColor(Color.RED)
//                .setContentTitle(notificationDetails)
//                .setContentText(getTransitionString(geofenceTransition))
//                .setContentIntent(notificationPendingIntent);
//
//        // Dismiss notification once the user touches it.
//        builder.setAutoCancel(true);
//
//        // Get an instance of the Notification manager
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
////            r.play();
//            builder.setSound(notification);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // Issue the notification
//        mNotificationManager.notify(0, builder.build());
//
//
//


        Intent intent = new Intent(this, MainActivity.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        String alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();

        Notification n  = new Notification.Builder(this)
                .setContentTitle(notificationDetails)
                .setContentText(getTransitionString(geofenceTransition))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSound(Uri.parse(alarmTonePath))
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .build();





        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), n);




    }




    static TextToSpeech  mTts;
    public static void speakOut(Context ctx, final String speakText,final String nearBy,final String locName){
        String nearByMessage="Hey! you have been entered in "+locName+" geofence.\n you can see nearBy places by clicking on location icon below.";

        Intent i = new Intent(ctx, AlarmAlertBroadcastReciever.class);
        i.putExtra("content",speakText);
        i.putExtra("nearBy", nearBy);
        i.putExtra("nearBYMessage",nearByMessage);

        PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, i,
                PendingIntent.FLAG_ONE_SHOT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);






//          mTts=new TextToSpeech(ctx, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                System.out.println("---------");
//                if (status == TextToSpeech.SUCCESS) {
//                    int result = mTts.setLanguage(Locale.US);
//                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
//                        System.out.println("---------");
//
//                        mTts.speak(speakText, TextToSpeech.QUEUE_FLUSH, null);
//
//                    }
//                }
//            }
//        });
    }




}
