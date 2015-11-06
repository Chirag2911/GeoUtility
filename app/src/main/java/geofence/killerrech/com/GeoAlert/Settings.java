package geofence.killerrech.com.GeoAlert;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.killerrech.adapter.DialogAdapter;
import com.killerrech.adapter.SettingsAdapter;
import com.killerrech.constants.ConstantsForSharedPrefrences;
import com.killerrech.database.DBHelper;
import com.killerrech.database.TablesController;
import com.killerrech.model.SettingsModel;
import com.killerrech.sharedPrefrences.SharedPrefrence;

import java.util.ArrayList;
import java.util.Random;

public class Settings extends ActionBarActivity {
    TablesController tbController;
    TextView profile_modeIn;
    TextView profile_modeOut;
    CheckBox checkbox_enter_notification,checkbox_exit_notification,checkbox_enter_alarm,checkbox_exit_alarm;
    Switch mSwitchForNotication,mSwitchForAlarm;
    ImageButton imageButtonForProfileIn,imageButtonForProfileOut;
    final CharSequence[] array = {"Silent","Meeting", "General","None"};


    public static final int [] color={R.color.Red,
            R.color.green,
            R.color.blue
            ,R.color.orange,
            R.color.greytext,
            R.color.grey};
    public static final String [] places={"ATM",
            "Banks","Hotels","Hospitals"};
    GridView gridView;
    SettingsAdapter adapter;
    DialogAdapter dialogAdapter;
    ArrayList<SettingsModel> mNearByField=new ArrayList<>();
    ArrayList<SettingsModel> mDialogList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        tbController=TablesController.getTablesController(this);
        tbController.open();
        profile_modeIn=(TextView)findViewById(R.id.textProfileIn);
        profile_modeOut=(TextView)findViewById(R.id.textProfileOut);
        imageButtonForProfileIn=(ImageButton)findViewById(R.id.imgBtnChangeProfileIn);
        imageButtonForProfileOut=(ImageButton)findViewById(R.id.imgBtnChangeProfileOut);
        imageButtonForProfileIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileDialog(1,getIndexOf(profile_modeIn.getText().toString()));
            }
        });
        imageButtonForProfileOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileDialog(2,getIndexOf(profile_modeOut.getText().toString()));
            }
        });
        checkbox_enter_notification=(CheckBox)findViewById(R.id.chekNofiEnter);
        checkbox_exit_notification=(CheckBox)findViewById(R.id.chekNotiexit);
        checkbox_enter_alarm=(CheckBox)findViewById(R.id.chekAlaramEntry);
        checkbox_exit_alarm=(CheckBox)findViewById(R.id.chekAlaramExit);

        mSwitchForNotication=(Switch)findViewById(R.id.switchNoti);
        mSwitchForAlarm=(Switch)findViewById(R.id.switchAlwaram);
        mSwitchForAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefrence.saveBooleanSharedPrefernces(Settings.this, ConstantsForSharedPrefrences.IS_ALARM_SET,isChecked);
            }
        });

        mSwitchForNotication.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefrence.saveBooleanSharedPrefernces(Settings.this, ConstantsForSharedPrefrences.IS_NOTIFICATION_SET,isChecked);
            }
        });
        mSwitchForAlarm.setChecked(SharedPrefrence.getBooleanSharedPrefernces(Settings.this, ConstantsForSharedPrefrences.IS_ALARM_SET));
        mSwitchForNotication.setChecked(SharedPrefrence.getBooleanSharedPrefernces(Settings.this,ConstantsForSharedPrefrences.IS_NOTIFICATION_SET));
        Cursor cr=tbController.getGeoSettings(1 + "");
        gridView=(GridView)findViewById(R.id.gridview);
        adapter=new SettingsAdapter(this,mNearByField);
        dialogAdapter=new DialogAdapter(this,mDialogList);
        gridView.setAdapter(adapter);


        if (cr.getCount()>0){
            cr.moveToFirst();
            profile_modeIn.setText(array[Integer.parseInt(cr.getString(cr.getColumnIndex(DBHelper.PROFILE_MODE_IN)))]);
            profile_modeOut.setText(array[Integer.parseInt(cr.getString(cr.getColumnIndex(DBHelper.PROFILE_MODE_OUT)))]);
            checkbox_enter_notification.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.NOTIFICATION_ENTER))));
            checkbox_exit_notification.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.NOTIFICATION_EXIT))));
            checkbox_enter_alarm.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.ALARM_ENTER))));
            checkbox_exit_alarm.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.ALARM_EXIT))));

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

    }

    private void initialiseList(String string) {
        String [] active=string.split("-");
        for (int i=0;i<active.length;i++){
            SettingsModel model = new SettingsModel();
            model.setName(active[i]);
            Random rand = new Random();

            int n = rand.nextInt(color.length);
            model.setColor(color[n]);
            model.setIsActivated(false);
            mNearByField.add(model);
        }
        adapter.notifyDataSetChanged();
      //  initialiseDialogList();

    }

    private int getIndexOf(String s) {
        for (int i=0;i<array.length;i++){
            if (array[i]==s){
                return i;
            }
        }
        return 3;
    }

    int select;

    private void selectProfileDialog(final int profileType,int selected) {
        //Initialize the Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//Source of the data in the DIalog
        select=selected;

        builder.setTitle("Select Profile")
                .setSingleChoiceItems(array, selected, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
                        select = which;

                    }
                })

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        if (mNearByField.size()>0) {
            for (int i = 0; i < places.length; i++) {
                boolean isActive=false;
                int j=0;
                for ( j=0;j<mNearByField.size();j++) {
                    if (mNearByField.get(j).getName().equalsIgnoreCase(places[i])){
                        isActive=true;
                        break;
                    }
                }

                SettingsModel model = new SettingsModel();
                model.setName(places[i]);
                Random rand = new Random();

                int n = rand.nextInt(color.length);
                if (isActive)
                    model.setColor(mNearByField.get(j).getColor());
                else
                    model.setColor(color[n]);
                model.setIsActivated(isActive);
                mDialogList.add(model);


            }
        }else {
            for (int i = 0; i < places.length; i++) {
                SettingsModel model = new SettingsModel();
                model.setName(places[i]);
                Random rand = new Random();

                int n = rand.nextInt(color.length);
                model.setColor(color[n]);
                model.setIsActivated(false);
                mDialogList.add(model);
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        String nearby=getNearBy();
        long insertedid= tbController.addSettings(1+"",getIndexOf(profile_modeIn.getText().toString())+"",getIndexOf(profile_modeOut.getText().toString())+"", checkbox_enter_notification.isChecked()+""
                ,checkbox_exit_notification.isChecked()+"",
                checkbox_enter_alarm.isChecked()+""
                ,checkbox_exit_alarm.isChecked()+"",nearby);
        System.out.println("======================inserted at" + insertedid);

        super.onBackPressed();
    }

    private String getNearBy() {
        String places="";
        for (int i=0;i<mNearByField.size();i++){
            if (i==mNearByField.size()-1){
                places+=mNearByField.get(i).getName();
            }else {
                places += mNearByField.get(i).getName() + "-";
            }
        }
        return places;
    }


    private void showConfirmationDialog() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.custom_dialog, null);
        // set title
        alertDialogBuilder.setTitle("Select Things you want to find");
        alertDialogBuilder.setView(view);

        GridView grid =(GridView)view.findViewById(R.id.gridViewForDailog);
        grid.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        alertDialogBuilder

                .setCancelable(false)
                .setPositiveButton("Done",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        adapter.notifyDataSetChanged();


                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    public void removeItem(int index){
        mNearByField.remove(index);
        adapter.notifyDataSetChanged();
        initialiseDialogList();

    }

    public void removeFromNearBy(int index){
        mDialogList.get(index).setIsActivated(false);

        for (int i=0;i<mNearByField.size();i++) {
            if (mNearByField.get(i).getName().equalsIgnoreCase(mDialogList.get(index).getName())){
                mNearByField.remove(i);
                break;
            }
        }

    }

    public void addNearBy(int index){
        mDialogList.get(index).setIsActivated(true);
                mNearByField.add(mDialogList.get(index));
    }



    public void notifyMNearBy(){
        adapter.notifyDataSetChanged();
    }


}
