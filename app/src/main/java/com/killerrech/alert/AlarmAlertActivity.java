/* Copyright 2014 Sheldon Neilson www.neilson.co.za
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.killerrech.alert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import geofence.killerrech.com.GeoAlert.NearBy;
import geofence.killerrech.com.GeoAlert.R;

public class AlarmAlertActivity extends Activity implements OnClickListener {
	private static final float ROTATE_FROM = 0.0f;
	private static final float ROTATE_TO = -10.0f * 360.0f;// 3.141592654f * 32.0f;

	private MediaPlayer mediaPlayer;
	String title="";
	String nearby="";
	String nearByMsg="";
	boolean isAlarm=true;


	private Vibrator vibrator;

	private boolean alarmActive;

	private TextView problemView;
	private TextView answerView;
	int mPreviousProfileMode;
	private void hideNavigationBar() {
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


		hideNavigationBar();

		// Close dialogs and window shade, so this is fully visible
		sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		setContentView(R.layout.alarm_alert);
		/**
		 * 	mathAlarmAlertActivityIntent.putExtra("content",intent.getStringExtra("content"));
		 mathAlarmAlertActivityIntent.putExtra("nearBy", intent.getStringExtra("nearBy"));
		 mathAlarmAlertActivityIntent.putExtra("nearBYMessage",intent.getStringExtra("content"));

		 */
		mPreviousProfileMode=getDeviceProfile(this);
		try
		{
			title=getIntent().getStringExtra("content");
			nearby=getIntent().getStringExtra("nearBy");
			nearByMsg=getIntent().getStringExtra("nearBYMessage");

		}catch (Exception e){

		}



		this.setTitle("GeoAlert");


		problemView = (TextView) findViewById(R.id.textView1);
		problemView.setText("GeoAlert\n"+title);

		answerView = (TextView) findViewById(R.id.textView2);
		if (!nearby.equalsIgnoreCase(""))
		answerView.setText(nearByMsg);


	//((Button) findViewById(R.id.Button_clear)).setOnClickListener(this);
		final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.shake);
		FloatingActionButton a=((FloatingActionButton) findViewById(R.id.fab));
		a.setOnClickListener(this);
		if (title.equalsIgnoreCase("")){
			a.setVisibility(View.GONE);
			isAlarm=false;
		}

			a.setAnimation(animRotate);
		FloatingActionButton b=((FloatingActionButton) findViewById(R.id.dismiss));
		b.setOnClickListener(this);
		if (nearby.equalsIgnoreCase("")){
			b.setVisibility(View.GONE);
		}

		b.setAnimation(animRotate);
		animRotate.start();

		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					Log.d(getClass().getSimpleName(), "Incoming call: "
							+ incomingNumber);
					try {
						mediaPlayer.pause();
					} catch (IllegalStateException e) {

					}
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					Log.d(getClass().getSimpleName(), "Call State Idle");
					try {
						mediaPlayer.start();
					} catch (IllegalStateException e) {

					}
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};

		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// Toast.makeText(this, answerString, Toast.LENGTH_LONG).show();

		startAlarm();

	}

	@Override
	protected void onResume() {
		super.onResume();
		alarmActive = true;
	}

	private void startAlarm() {
		 String alarmTonePath;
		if (isAlarm) {
			alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
		}else {
			alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();

		}
		System.out.println("=================tone:::"+alarmTonePath);

		if (alarmTonePath != "") {
			mediaPlayer = new MediaPlayer();
			//if (alarm.getVibrate()) {
				vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				long[] pattern = { 1000, 200, 200, 200 };
			if (isAlarm) {
				vibrator.vibrate(pattern, 0);
			}else {
				vibrator.vibrate(pattern, -1);

			}
		//	}
			try {
				mediaPlayer.setVolume(1.0f, 1.0f);
				mediaPlayer.setDataSource(this,
						Uri.parse(alarmTonePath));
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				if (isAlarm) {
					mediaPlayer.setLooping(true);
				}
				else {
					mediaPlayer.setLooping(false);
				}
				mediaPlayer.prepare();
				mediaPlayer.start();

			} catch (Exception e) {
				mediaPlayer.release();
				alarmActive = false;
			}
		}




//
//		if (alarm.getAlarmTonePath() != "") {
//			mediaPlayer = new MediaPlayer();
//			if (alarm.getVibrate()) {
//				vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//				long[] pattern = { 1000, 200, 200, 200 };
//				vibrator.vibrate(pattern, 0);
//			}
//			try {
//				mediaPlayer.setVolume(1.0f, 1.0f);
//				mediaPlayer.setDataSource(this,
//						Uri.parse(alarm.getAlarmTonePath()));
//				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
//				mediaPlayer.setLooping(true);
//				mediaPlayer.prepare();
//				mediaPlayer.start();
//
//			} catch (Exception e) {
//				mediaPlayer.release();
//				alarmActive = false;
//			}
//		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (!alarmActive)
			super.onBackPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		StaticWakeLock.lockOff(this);
	}

	@Override
	protected void onDestroy() {
		try {
			if (vibrator != null)
				vibrator.cancel();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.stop();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.release();
		} catch (Exception e) {

		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (!alarmActive)
			return;
		String button = (String) v.getTag();
		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		if (button.equalsIgnoreCase("clear")) {

			alarmActive = false;
			if (vibrator != null)
				vibrator.cancel();
			try {
				mediaPlayer.stop();
			} catch (IllegalStateException ise) {

			}
			try {
				mediaPlayer.release();
			} catch (Exception e) {

			}

		}
		else if(button.equalsIgnoreCase("nearby")){
			alarmActive = false;
			if (vibrator != null)
				vibrator.cancel();
			try {
				mediaPlayer.stop();
			} catch (IllegalStateException ise) {

			}
			try {
				mediaPlayer.release();
			} catch (Exception e) {

			}
			Intent i=new Intent(this, NearBy.class);
			i.putExtra("content",title);
			i.putExtra("nearBy", nearby);
			startActivity(i);
		}

		changeDeviceProfile(this,mPreviousProfileMode);
		this.finish();



	}



	private int getDeviceProfile(Context context) {
	//	System.out.println("inside change profile mode:::"+profile_in);
		AudioManager am;
		am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		return am.getRingerMode();


//		switch (profile_in){
//			case 0:
//				//For Silent mode
//				am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//				break;
//			case 1:
//				//For Vibrate mode
//				am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//				break;
//			case 2:
//				//For Normal mode
//				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//				break;
//			default:
//				break;
//		}


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


}
