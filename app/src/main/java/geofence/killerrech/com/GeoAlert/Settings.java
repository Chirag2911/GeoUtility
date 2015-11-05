package geofence.killerrech.com.GeoAlert;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.killerrech.adapter.SettingsAdapter;
import com.killerrech.database.DBHelper;
import com.killerrech.database.TablesController;
import com.killerrech.model.SettingsModel;

import java.util.ArrayList;
import java.util.Random;

public class Settings extends ActionBarActivity {
    TablesController tbController;
    TextView profile_modeIn;
    TextView profile_modeOut;
    ToggleButton geo_notification;
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
        geo_notification=(ToggleButton)findViewById(R.id.toggleButtonForGeoNot);
        Cursor cr=tbController.getGeoSettings(1 + "");
        gridView=(GridView)findViewById(R.id.gridview);
        adapter=new SettingsAdapter(this,mNearByField);
        gridView.setAdapter(adapter);


        if (cr.getCount()>0){
            cr.moveToFirst();
            profile_modeIn.setText(array[Integer.parseInt(cr.getString(cr.getColumnIndex(DBHelper.PROFILE_MODE_IN)))]);
            profile_modeOut.setText(array[Integer.parseInt(cr.getString(cr.getColumnIndex(DBHelper.PROFILE_MODE_OUT)))]);
            geo_notification.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.GEO_NOTIFICATION))));
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
        long insertedid= tbController.addSettings(1+"",getIndexOf(profile_modeIn.getText().toString())+"",getIndexOf(profile_modeOut.getText().toString())+"",geo_notification.isChecked()+"",nearby);
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
        Button btn1=(Button)view.findViewById(R.id.button1);
        Button btn2=(Button)view.findViewById(R.id.button2);
        Button btn3=(Button)view.findViewById(R.id.button3);
        Button btn4=(Button)view.findViewById(R.id.button4);
        final ImageView imageView1=(ImageView)view.findViewById(R.id.imageView1);
        final ImageView imageView2=(ImageView)view.findViewById(R.id.imageView2);
        final ImageView imageView3=(ImageView)view.findViewById(R.id.imageView3);
        final ImageView imageView4=(ImageView)view.findViewById(R.id.imageView4);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               boolean active= mDialogList.get(0).isActivated();
                active=!active;
                mDialogList.get(0).setIsActivated(active);
                if (active){
                    imageView1.setVisibility(View.VISIBLE);
                    mNearByField.add(mDialogList.get(0));
                }else {
                    imageView1.setVisibility(View.GONE);
                    for (int i=0;i<mNearByField.size();i++) {
                        if (mNearByField.get(i).getName().equalsIgnoreCase(mDialogList.get(0).getName())){
                            mNearByField.remove(i);
                            break;
                        }
                    }
                }

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active= mDialogList.get(1).isActivated();
                active=!active;
                mDialogList.get(1).setIsActivated(active);
                if (active){
                    imageView2.setVisibility(View.VISIBLE);
                    mNearByField.add(mDialogList.get(1));
                }else {
                    imageView2.setVisibility(View.GONE);
                    for (int i=0;i<mNearByField.size();i++) {
                        if (mNearByField.get(i).getName().equalsIgnoreCase(mDialogList.get(1).getName())){
                            mNearByField.remove(i);
                            break;
                        }
                    }
                }

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active= mDialogList.get(2).isActivated();
                active=!active;
                mDialogList.get(2).setIsActivated(active);
                if (active){
                    imageView3.setVisibility(View.VISIBLE);
                    mNearByField.add(mDialogList.get(2));
                }else {
                    imageView3.setVisibility(View.GONE);
                    for (int i=0;i<mNearByField.size();i++) {
                        if (mNearByField.get(i).getName().equalsIgnoreCase(mDialogList.get(2).getName())){
                            mNearByField.remove(i);
                            break;
                        }
                    }
                }

            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active= mDialogList.get(3).isActivated();
                active=!active;
                mDialogList.get(3).setIsActivated(active);
                if (active){
                    imageView4.setVisibility(View.VISIBLE);
                    mNearByField.add(mDialogList.get(3));
                }else {
                    imageView4.setVisibility(View.GONE);
                    for (int i=0;i<mNearByField.size();i++) {
                        if (mNearByField.get(i).getName().equalsIgnoreCase(mDialogList.get(3).getName())){
                            mNearByField.remove(i);
                            break;
                        }
                    }
                }

            }
        });



        // set dialog message
        alertDialogBuilder

                .setCancelable(false)
                .setPositiveButton("Done",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
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



}
