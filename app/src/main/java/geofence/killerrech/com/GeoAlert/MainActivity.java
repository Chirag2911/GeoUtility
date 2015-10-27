
package geofence.killerrech.com.GeoAlert;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;


import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.killerrech.adapter.GeofenceAdapter;
import com.killerrech.database.DBHelper;
import com.killerrech.database.TablesController;
import com.killerrech.model.Geofencemodel;

import java.util.ArrayList;

import geofence.killerrech.com.GeoAlert.AddGeoFence;
import geofence.killerrech.com.GeoAlert.R;

public class MainActivity extends FragmentActivity {
    private GoogleMap googleMap;
    private GeofenceAdapter madapter;
    private ArrayList<Geofencemodel> mgeo_list;
    ListView mainListView;
    TablesController tbController;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_geofence);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);


        mgeo_list = new ArrayList<>();
        fab=(FloatingActionButton)findViewById(R.id.fab);
        tbController = TablesController.getTablesController(this);
        tbController.open();
        Cursor cr = tbController.getAllGeoLocation();
        if (cr.getCount() > 0) {
            cr.moveToFirst();
            Log.d("TAGCOunt", cr.getCount() + "");


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
        Log.d("Taglistcount", mgeo_list.size() + "");

        madapter = new GeofenceAdapter(MainActivity.this, mgeo_list);

        mainListView = (ListView) findViewById(R.id.mlistgeofenceplaces);
        mainListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        fab.setVisibility(View.GONE);
                        break;
                    case MotionEvent.ACTION_UP:
                        fab.setVisibility(View.VISIBLE);
                        break;
                }
                return false;
            }
        });

        mainListView.setAdapter(madapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mintent = new Intent(MainActivity.this, AddGeoFence.class
                );
                startActivity(mintent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
