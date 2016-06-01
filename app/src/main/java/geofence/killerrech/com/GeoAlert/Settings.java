package geofence.killerrech.com.GeoAlert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.killerrech.Geofence.Constants;
import com.killerrech.Geofence.GpsTrackingService;
import com.killerrech.adapter.DialogAdapter;
import com.killerrech.adapter.SettingsAdapter;
import com.killerrech.database.DBHelper;
import com.killerrech.database.TablesController;
import com.killerrech.model.SettingsModel;

import java.util.ArrayList;

public class Settings extends BaseActivity {
    TablesController tbController;
    TextView profile_modeIn;
    TextView profile_modeOut;
    TextView mLocname, mSave;
    LinearLayout mNotificationLayout, mAlaramLayout;
    Animation animFade;
    private boolean forEdit = false;

    String Geo_Id = null, mLocationName;
    CheckBox checkbox_enter_notification, checkbox_exit_notification, checkbox_enter_alarm, checkbox_exit_alarm;
    Switch mSwitchForNotication, mSwitchForAlarm;
    ImageButton imageButtonForProfileIn, imageButtonForProfileOut;

    //  see  later for localisation
    public static final CharSequence[] array = {"Silent", "Meeting", "General", "None"};


    public static final int[] color = {R.color.ColorPrimary};

    // see later for localisation
    public static final String[] places = {"food", "hospital", "atm", "police", "gas_station","pharmacy","shopping_mall","night_club"};
    GridView gridView;
    SettingsAdapter adapter;
    DialogAdapter dialogAdapter;
    ArrayList<SettingsModel> mNearByField = new ArrayList<>();
    ArrayList<SettingsModel> mDialogList = new ArrayList<>();
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        mNotificationLayout = (LinearLayout) findViewById(R.id.mlineaNotification);
        mAlaramLayout = (LinearLayout) findViewById(R.id.mlineaAlaram);

        tbController = TablesController.getTablesController(this);
        try {
            forEdit = getIntent().getBooleanExtra("editflagkey", false);
            System.out.println("<<<<<get" + forEdit);
//            Log.d("<<<<<<<get",""+isComingFromEdit);
        } catch (Exception e) {
            //just in case not coming from edit
            System.out.println("<<<<<get" + forEdit);

//            Log.d("<<<<<<<get",""+isComingFromEdit);

        }
        tbController.open();
        mSave = (TextView) findViewById(R.id.tag_geofence_save);

        Geo_Id = getIntent().getStringExtra("Key_GeoId");
        System.out.println("------inside setting geo id::" + Geo_Id);
        mLocationName = getIntent().getStringExtra("Key_GeoName");
        mLocname = (TextView) findViewById(R.id.mlocname);
        mLocname.setText(mLocationName);
        profile_modeIn = (TextView) findViewById(R.id.textProfileIn);
        profile_modeOut = (TextView) findViewById(R.id.textProfileOut);
        imageButtonForProfileIn = (ImageButton) findViewById(R.id.imgBtnChangeProfileIn);
        imageButtonForProfileOut = (ImageButton) findViewById(R.id.imgBtnChangeProfileOut);
        imageButtonForProfileIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileDialog(1, getIndexOf(profile_modeIn.getText().toString()));
            }
        });
        imageButtonForProfileOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileDialog(2, getIndexOf(profile_modeOut.getText().toString()));
            }
        });
        checkbox_enter_notification = (CheckBox) findViewById(R.id.chekNofiEnter);
        checkbox_exit_notification = (CheckBox) findViewById(R.id.chekNotiexit);
        checkbox_enter_alarm = (CheckBox) findViewById(R.id.chekAlaramEntry);
        checkbox_exit_alarm = (CheckBox) findViewById(R.id.chekAlaramExit);

        mSwitchForNotication = (Switch) findViewById(R.id.switchNoti);
        mSwitchForAlarm = (Switch) findViewById(R.id.switchAlwaram);


        mSwitchForAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAlaramLayout.setVisibility(View.VISIBLE);

                    animFade = AnimationUtils.loadAnimation(Settings.this, R.anim.animation_up);
                    mAlaramLayout.startAnimation(animFade);
                } else {
                    mAlaramLayout.setVisibility(View.INVISIBLE);

                    animFade = AnimationUtils.loadAnimation(Settings.this, R.anim.animation_down);
                    mAlaramLayout.startAnimation(animFade);


                }
            }
        });

        mSwitchForNotication.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    animFade = AnimationUtils.loadAnimation(Settings.this, R.anim.animation_up);

                    mNotificationLayout.startAnimation(animFade);
                    mNotificationLayout.setVisibility(View.VISIBLE);


                } else {
                    mNotificationLayout.setVisibility(View.INVISIBLE);

                    animFade = AnimationUtils.loadAnimation(Settings.this, R.anim.animation_down);

                    mNotificationLayout.startAnimation(animFade);
                }
            }
        });
//        mSwitchForAlarm.setChecked(SharedPrefrence.getBooleanSharedPrefernces(Settings.this, ConstantsForSharedPrefrences.IS_ALARM_SET));
//        mSwitchForNotication.setChecked(SharedPrefrence.getBooleanSharedPrefernces(Settings.this, ConstantsForSharedPrefrences.IS_NOTIFICATION_SET));
        Cursor cr = tbController.getGeoSettings(Geo_Id);
        gridView = (GridView) findViewById(R.id.gridview);
        adapter = new SettingsAdapter(this, mNearByField);
        dialogAdapter = new DialogAdapter(this, mDialogList);
        gridView.setAdapter(adapter);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                checkAndSetResult(forEdit);


//                // add Chek satement here
//                Intent mintent = new Intent(Settings.this,MainActivity.class);
//                mintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(mintent);
            }
        });
        if (cr.getCount() > 0) {
            cr.moveToFirst();
            profile_modeIn.setText(array[Integer.parseInt(cr.getString(cr.getColumnIndex(DBHelper.PROFILE_MODE_IN)))]);
            profile_modeOut.setText(array[Integer.parseInt(cr.getString(cr.getColumnIndex(DBHelper.PROFILE_MODE_OUT)))]);
            checkbox_enter_notification.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.NOTIFICATION_ENTER))));
            checkbox_exit_notification.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.NOTIFICATION_EXIT))));
            checkbox_enter_alarm.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.ALARM_ENTER))));
            checkbox_exit_alarm.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.ALARM_EXIT))));
            mSwitchForNotication.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.IS_NOTIFICATION))));
            mSwitchForAlarm.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.IS_ALARM))));

            initialiseList(cr.getString(cr.getColumnIndex(DBHelper.GEO_NEARBY_PLACES)));


        }
        initialiseDialogList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        mAdView = (AdView) findViewById(R.id.ad_view);
        mInterstitialAd = new InterstitialAd(this);

        AdRequest adRequest = new AdRequest.Builder()

                .build();

        mInterstitialAd.setAdUnitId("ca-app-pub-5812953486178475/4766502347");

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


    private void checkAndSetResult(boolean isEdit) {

        if (checkAndSetView()) {
            saveToDb();
            if (!isEdit)
                setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private boolean checkAndSetView() {
        if (!mSwitchForAlarm.isChecked() && profile_modeIn.getText().toString().equalsIgnoreCase(array[3].toString())
                && profile_modeOut.getText().toString().equalsIgnoreCase(array[3].toString())
                && mNearByField.size() == 0) {
            mSwitchForNotication.setChecked(true);
            checkbox_enter_notification.setChecked(true);
            checkbox_exit_notification.setChecked(true);
        }


        return true;
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()

                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    private void initialiseList(String string) {
        if (string.equals(""))
            return;

        String[] active = string.split("-");
        for (int i = 0; i < active.length; i++) {
            SettingsModel model = new SettingsModel();
            model.setName(active[i]);
            //  Random rand = new Random();

            // int n = rand.nextInt(color.length);
            model.setColor(color[0]);
            model.setIsActivated(false);
            mNearByField.add(model);
        }
        adapter.notifyDataSetChanged();
        //  initialiseDialogList();

    }

    private int getIndexOf(String s) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == s) {
                return i;
            }
        }
        return 3;
    }

    int select;

    private void selectProfileDialog(final int profileType, int selected) {
        //Initialize the Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Source of the data in the DIalog
        select = selected;

        builder.setTitle("Select Profile")
                .setSingleChoiceItems(array, selected, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        select = which;

                    }
                })

                .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if (profileType == 1) {
                            profile_modeIn.setText(array[select]);
                        } else {
                            profile_modeOut.setText(array[select]);

                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();
    }

    private void initialiseDialogList() {
        mDialogList.clear();
        if (mNearByField.size() > 0) {
            for (int i = 0; i < places.length; i++) {
                boolean isActive = false;
                int j = 0;
                for (j = 0; j < mNearByField.size(); j++) {
                    if (mNearByField.get(j).getName().equalsIgnoreCase(places[i])) {
                        isActive = true;
                        break;
                    }
                }

                SettingsModel model = new SettingsModel();
                model.setName(places[i]);
                //  Random rand = new Random();

                // int n = rand.nextInt(color.length);
//                if (isActive)
//                    model.setColor(mNearByField.get(j).getColor());
//                else
                model.setColor(color[0]);
                model.setIsActivated(isActive);
                mDialogList.add(model);


            }
        } else {
            for (int i = 0; i < places.length; i++) {
                SettingsModel model = new SettingsModel();
                model.setName(places[i]);
                // Random rand = new Random();

                //  int n = rand.nextInt(color.length);
                model.setColor(color[0]);
                model.setIsActivated(false);
                mDialogList.add(model);
            }
        }

    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {

        checkAndSetResult(forEdit);

//        if (!forEdit) {
//            saveToDb();
//            setResult(RESULT_OK);
//
//        }
//
//
//        super.onBackPressed();
    }

    private void saveToDb() {
        String nearby = getNearBy();
        long insertedid = tbController.addSettings(Geo_Id, getIndexOf(profile_modeIn.getText().toString()) + "", getIndexOf(profile_modeOut.getText().toString()) + "", checkbox_enter_notification.isChecked() + ""
                , checkbox_exit_notification.isChecked() + "",
                checkbox_enter_alarm.isChecked() + ""
                , checkbox_exit_alarm.isChecked() + "", mSwitchForNotication.isChecked() + "", mSwitchForAlarm.isChecked() + "", nearby);
        System.out.println("======================inserted at" + insertedid);

        Toast.makeText(Settings.this, getResources().getString(R.string.Setting_save), Toast.LENGTH_SHORT).show();

    }

    private String getNearBy() {
        String places = "";
        for (int i = 0; i < mNearByField.size(); i++) {
            if (i == mNearByField.size() - 1) {
                places += mNearByField.get(i).getName();
            } else {
                places += mNearByField.get(i).getName() + "-";
            }
        }
        return places;
    }


    private void showConfirmationDialog() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.custom_dialog, null);
        // set title
        alertDialogBuilder.setTitle(getResources().getString(R.string.dialog_nearby));
        alertDialogBuilder.setView(view);

        GridView grid = (GridView) view.findViewById(R.id.gridViewForDailog);
        grid.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        alertDialogBuilder

                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.Done), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        adapter.notifyDataSetChanged();


                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void removeItem(int index) {
        mNearByField.remove(index);
        adapter.notifyDataSetChanged();
        initialiseDialogList();

    }

    public void removeFromNearBy(int index) {
        mDialogList.get(index).setIsActivated(false);

        for (int i = 0; i < mNearByField.size(); i++) {
            if (mNearByField.get(i).getName().equalsIgnoreCase(mDialogList.get(index).getName())) {
                mNearByField.remove(i);
                break;
            }
        }

    }

    public void addNearBy(int index) {
        mDialogList.get(index).setIsActivated(true);
        mNearByField.add(mDialogList.get(index));
    }




    public void notifyMNearBy() {
        adapter.notifyDataSetChanged();
    }


}
