package geofence.killerrech.com.GeoAlert;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.killerrech.Geofence.Constants;
import com.killerrech.Geofence.GpsTrackingService;

public class BaseActivity extends AppCompatActivity {
    public static BaseActivity baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseContext=this;
    }



    @Override
    protected void onResume() {
        super.onResume();

    }



}
