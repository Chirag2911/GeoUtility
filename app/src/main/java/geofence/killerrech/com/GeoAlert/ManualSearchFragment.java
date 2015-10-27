package geofence.killerrech.com.GeoAlert;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.killerrech.model.Geofencemodel;


public class ManualSearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    Location mlocation, location;

    double latitude, longitude;
    LocationManager locationManager;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View view;
    int radiusProgress;
    private double mlat, mlong;
    public static Geofencemodel GeofenceTOAdd;
    private SeekBar mseekbar;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    int radius;
    private String searchLocation;
    LatLng latLng;
    AutoCompleteTextView atvPlaces;


    private TextView mtxtRadius;

    private EditText medit;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton currentLocButton;


    // TODO: Rename and change types and number of parameters
    public static ManualSearchFragment newInstance(String param1, String param2) {
        ManualSearchFragment fragment = new ManualSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ManualSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_manual_search, container,
                false);


        googleMap = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map)).getMap();
//                googleMap = ((SupportMapFragment) getChildFragmentManager()
//                        .findFragmentById(R.id.map)).getMap();


        medit = (EditText) view.findViewById(R.id.geofencebutton);


        // Loading map
        atvPlaces = (AutoCompleteTextView) view.findViewById(R.id.atv_places);
        mtxtRadius = (TextView) view.findViewById(R.id.txtradius);
        mseekbar = (SeekBar) view.findViewById(R.id.geofenceseekBar);
        currentLocButton=(FloatingActionButton)view.findViewById(R.id.locationfab2);

        currentLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location mloc= getLocation();
                mlat=mloc.getLatitude();
                mlong=mloc.getLongitude();
                if ((mlat != 0) && (mlong != 0)) {

                    setGoogleMap(mlat, mlong, 1);

                }


            }
        });
        view.findViewById(R.id.savemanualfab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(mlat!=0.0 && mlong!=0.0) {
                Log.d("<<<<Loc", mlat + " " + mlong);
                GeofenceTOAdd = new Geofencemodel();
                GeofenceTOAdd.setGeoName(medit.getText().toString());
                GeofenceTOAdd.setAddress(searchLocation);
                GeofenceTOAdd.setRadius(radiusProgress + "");
                GeofenceTOAdd.setLatitude(mlat);
                GeofenceTOAdd.setLongitude(mlong);
                ((AddGeoFence) getActivity()).mGeofencesAdded = true;
                ((AddGeoFence) getActivity()).addGeofencesButtonHandler(GeofenceTOAdd.getGeofence());
            }else{
                Snackbar.make(mseekbar,"Please Add Location",Snackbar.LENGTH_SHORT).show();
            }

            }
        });
        mseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                radiusProgress = progress;
                mtxtRadius.setText("" + progress + "Km");


                if ((mlat != 0) && (mlong != 0)) {

                    setGoogleMap(mlat, mlong, progress);

                }
            }
        });


        // Changing map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

        // Showing / hiding your current location
        googleMap.setMyLocationEnabled(true);

        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(true);

        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        // lets place some 10 random markers
        System.out.println("enter in map");

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event


    public void setGoogleMap(double mlat, double mlong, int r) {
        googleMap.clear();
        System.out
                .println("getLatitudeaddfence" + mlat + "getlonitude" + mlong);

        latLng = new LatLng(mlat, mlong);

        markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        // markerOptions.title(addressText);

        googleMap.addMarker(markerOptions);

        googleMap.addCircle(new CircleOptions().center(latLng).radius(r * 1000)
                .strokeColor(Color.BLACK).fillColor(0x30ff0000).strokeWidth(2));
        // Locate the first location
        // if(i==0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        // googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom);
    }


    boolean isGPSEnabled, isNetworkEnabled;



    public Location getLocation() {
        this.locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                   return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) ;
        }else{
            return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) ;

        }
    }



    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


}
