package geofence.killerrech.com.GeoAlert;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.killerrech.Geofence.GpsTrackingService;

public class BaseActivity extends AppCompatActivity {
    public static BaseActivity baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseContext=this;
    }
    public void getCallingPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 111);


        } else {

            // No explanation needed, we can request the permission.

            this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 111);
//            Fragment.requestPermissions(
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    111);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, GpsTrackingService.class));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("=======inside on onRequest Permission Result");

        switch (requestCode) {
            case 111:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this,GpsTrackingService.class));

                } else {

                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
