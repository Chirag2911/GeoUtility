package com.killerrech.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by renu on 9/17/15.
 */
public class TablesController {
    private DBHelper dbHelper;
    private Context ourcontext;
    private SQLiteDatabase database;
// for setting
    private static final String[] PROJECTION_SETTINGS = new String[] { DBHelper.ID,
            DBHelper.GEO_ID, DBHelper.PROFILE_MODE_IN,DBHelper.PROFILE_MODE_OUT,DBHelper.GEO_NOTIFICATION};


    //for GeoLoc
    private static final String[] PROJECTION_GEOLOCATION = new String[] { DBHelper.GEOLOC_ID,
            DBHelper.GEO_LATITUDE, DBHelper.GEO_LONGITUDE,DBHelper.GEO_RADIUS,DBHelper.GEO_Address,DBHelper.GEO_NAME};


    private static TablesController tablesController =null;
    private boolean isOpen=false;

    private TablesController(Context context){
        ourcontext=context;
    }

    public static TablesController getTablesController(Context context){
        if(tablesController ==null)
            tablesController =new TablesController(context);
        return tablesController;
    }

    public TablesController open() throws SQLException {
        dbHelper = DBHelper.getDbHelper(ourcontext);
        database = dbHelper.getWritableDatabase();
        isOpen=true;
        return this;

    }
    public void close() {
        dbHelper.close();
        isOpen=false;
    }

    public boolean isOpen() {
        return isOpen;
    }
// Insert Setting
  public long addSettings(String geo_id, String profile_in, String profile_out, String geo_notification){
      if (!hasSettings(geo_id)){
          ContentValues contentValue = new ContentValues();
          contentValue.put(dbHelper.GEO_ID,geo_id);
          contentValue.put(dbHelper.PROFILE_MODE_IN,profile_in);
          contentValue.put(dbHelper.PROFILE_MODE_OUT,profile_out);
          contentValue.put(dbHelper.GEO_NOTIFICATION, geo_notification);

          long id=  database.insert(dbHelper.SETTINGS_TABLE, null, contentValue);
          System.out.println("===========inserted id is:::"+id);
          return id;
      }else {
          return updateSettings(geo_id,profile_in,profile_out,geo_notification);
      }

  }

    // Insert Geolocation
    public long addGeoLocation(String geo_id, String geo_latitude, String geo_longitude, String geo_radius,String geo_address,String geo_name){
        if (!hasGeoLocation(geo_id)){
            ContentValues contentValue = new ContentValues();
            contentValue.put(dbHelper.GEOLOC_ID,geo_id);
            contentValue.put(dbHelper.GEO_LATITUDE,geo_latitude);
            contentValue.put(dbHelper.GEO_LONGITUDE,geo_longitude);
            contentValue.put(dbHelper.GEO_RADIUS,geo_radius);
            contentValue.put(dbHelper.GEO_Address,geo_address);
            contentValue.put(dbHelper.GEO_NAME,geo_name);


            long id=  database.insert(dbHelper.GEOLOCATION_TABLE, null, contentValue);
            System.out.println("===========inserted id is:::"+id);
            return id;
        }else {
            return updateGeoLocation(geo_id,geo_latitude,geo_longitude,geo_radius,geo_address,geo_name);
        }

    }







    //return true if there is any settings present
    public boolean hasSettings(String geo_id) {
        Cursor cr=database.query(dbHelper.SETTINGS_TABLE, null, dbHelper.GEO_ID + " = ? ", new String[] { geo_id },
                null, null, null);

        return ((cr.getCount()>0))?true:false;
    }

    public boolean hasGeoLocation(String geo_id) {
        Cursor cr=database.query(dbHelper.GEOLOCATION_TABLE, null, dbHelper.GEOLOC_ID + " = ? ", new String[] { geo_id },
                null, null, null);

        return ((cr.getCount()>0))?true:false;
    }

    public long updateSettings(String geo_id, String profile_in, String profile_out, String geo_notification){
            ContentValues contentValue = new ContentValues();
            contentValue.put(dbHelper.GEO_ID,geo_id);
            contentValue.put(dbHelper.PROFILE_MODE_IN,profile_in);
            contentValue.put(dbHelper.PROFILE_MODE_OUT,profile_out);
            contentValue.put(dbHelper.GEO_NOTIFICATION, geo_notification);

            long id=  database.update(dbHelper.SETTINGS_TABLE, contentValue, dbHelper.GEO_ID + "= ?", new String[]{geo_id});
            System.out.println("===========updated id is:::"+id);
            return id;

    }


// Update----------------------GEoLocation

    public long updateGeoLocation(String geo_id, String geo_latitude, String geo_longitude, String geo_radius,String geo_addres,String geo_name){
        ContentValues contentValue = new ContentValues();
        contentValue.put(dbHelper.GEOLOC_ID,geo_id);
        contentValue.put(dbHelper.GEO_LATITUDE,geo_latitude);
        contentValue.put(dbHelper.GEO_LONGITUDE,geo_longitude);
        contentValue.put(dbHelper.GEO_RADIUS, geo_radius);
        contentValue.put(dbHelper.GEO_Address, geo_addres);
        contentValue.put(dbHelper.GEO_NAME,geo_name);

        long id=  database.update(dbHelper.GEOLOCATION_TABLE, contentValue, dbHelper.GEOLOC_ID + "= ?", new String[]{geo_id});
        System.out.println("===========updated id is:::"+id);
        return id;

    }


    //return true if there is any settings present
    public Cursor getGeoSettings(String geo_id) {
        Cursor cr=database.query(dbHelper.SETTINGS_TABLE, PROJECTION_SETTINGS, dbHelper.GEO_ID + " = ? ", new String[]{geo_id},
                null, null, null);

        return cr;
    }
//Get row------------------------------geolocation




    public Cursor getAllGeoLocation() {
        Cursor cr=database.query(dbHelper.GEOLOCATION_TABLE, null, null,null,
                null, null, null);

        return cr;
    }

    public Cursor getGeoLocation(String geo_id) {
        Cursor cr=database.query(dbHelper.GEOLOCATION_TABLE, PROJECTION_GEOLOCATION, dbHelper.GEOLOC_ID + " = ? ", new String[]{geo_id},
                null, null, null);

        return cr;
    }




}
