package geofence.killerrech.com.GeoAlert;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.killerrech.database.DBHelper;
import com.killerrech.database.TablesController;

public class Settings extends ActionBarActivity {
    TablesController tbController;
    Spinner profile_modeIn;
    Spinner profile_modeOut;
    ToggleButton geo_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        tbController=TablesController.getTablesController(this);
        tbController.open();
        profile_modeIn=(Spinner)findViewById(R.id.spinnerProfileIn);
        profile_modeOut=(Spinner)findViewById(R.id.spinnerProfileOut);
        geo_notification=(ToggleButton)findViewById(R.id.toggleButtonForGeoNot);
        Cursor cr=tbController.getGeoSettings(1 + "");

        if (cr.getCount()>0){
            cr.moveToFirst();
            profile_modeIn.setSelection(Integer.parseInt(cr.getString(cr.getColumnIndex(DBHelper.PROFILE_MODE_IN))));
            profile_modeOut.setSelection(Integer.parseInt(cr.getString(cr.getColumnIndex(DBHelper.PROFILE_MODE_OUT))));
            geo_notification.setChecked(Boolean.parseBoolean(cr.getString(cr.getColumnIndex(DBHelper.GEO_NOTIFICATION))));

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
        long insertedid= tbController.addSettings(1+"",profile_modeIn.getSelectedItemPosition()+"",profile_modeOut.getSelectedItemPosition()+"",geo_notification.isChecked()+"");
        System.out.println("======================inserted at" + insertedid);

        super.onBackPressed();
    }
}
