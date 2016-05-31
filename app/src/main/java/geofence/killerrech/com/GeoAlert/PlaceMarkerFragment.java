package geofence.killerrech.com.GeoAlert;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.killerrech.model.Geofencemodel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Chiggy on 22/01/16.
 */
public class PlaceMarkerFragment extends Fragment implements GoogleMap.OnMarkerDragListener,GoogleMap.OnMapClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View view;
    int radiusProgress = 1;
    private double mlat, mlong;
    public static Geofencemodel GeofenceTOAdd;
    private SeekBar mseekbar;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    int radius;
    private String searchLocation;
    LatLng latLng;


    LocationManager locationManager;


    FloatingActionButton currentLocButton;
    private TextView mtxtRadius;

    private EditText medit;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    // TODO: Rename and change types and number of parameters
    public static ManualSearchFragment newInstance(String param1, String param2) {
        ManualSearchFragment fragment = new ManualSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceMarkerFragment() {
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


        view = inflater.inflate(R.layout.fragment_place_picker, container,
                false);


        googleMap = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map)).getMap();
//                googleMap = ((SupportMapFragment) getChildFragmentManager()
//                        .findFragmentById(R.id.map)).getMap();
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapClickListener(this);


        medit = (EditText) view.findViewById(R.id.geofencebutton);


        // Loading map

        mtxtRadius = (TextView) view.findViewById(R.id.txtradius);
        mseekbar = (SeekBar) view.findViewById(R.id.geofenceseekBar);



        view.findViewById(R.id.savemanualfab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GeofenceTOAdd = new Geofencemodel();

                String mGeoFencename = medit.getText().toString();
                System.out.println("<<<<<<<<<<Geofencename" + mGeoFencename);
                if (mlat != 0.0 && mlong != 0.0) {
                    if (!mGeoFencename.equals(null) && !mGeoFencename.equals("")) {


                        final GeocoderTask msaveGeofenceTask = new GeocoderTask();
                        msaveGeofenceTask.execute(mGeoFencename);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (msaveGeofenceTask.getStatus() == AsyncTask.Status.RUNNING) {
                                    msaveGeofenceTask.cancel(true);


                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((AddGeoFence) getActivity()).hidepDialog();

                                            Snackbar.make(mtxtRadius, getResources().getString(R.string.Network_slow), Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }, 30000);

                    } else {
                        Snackbar.make(mseekbar, getResources().getString(R.string.snackbar_name), Snackbar.LENGTH_SHORT).show();
                    }

                } else {
                    Snackbar.make(mseekbar, getResources().getString(R.string.snackbar_loc), Snackbar.LENGTH_SHORT).show();
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

                if (progress > 0) {
                    radiusProgress = progress;
                    mtxtRadius.setText("" + progress + "Km");
                    if ((mlat != 0) && (mlong != 0)) {

                        setGoogleMap(mlat, mlong, radiusProgress);

                    }
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

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mlat=cameraPosition.target.latitude;
                mlong=cameraPosition.target.longitude;







            }
        });


       // for setting current location  first time for marker

        Location mloc = getLocation();
        if(mloc!=null) {
            mlat = mloc.getLatitude();
            mlong = mloc.getLongitude();
        }
        if ((mlat != 0) && (mlong != 0)) {

            setGoogleMap(mlat, mlong, 1);


        }

        // lets place some 10 random markers
        System.out.println("enter in map");

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    private Location getLocation() {
        this.locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (BaseActivity.baseContext != null)
                (BaseActivity.baseContext).getCallingPermission();
            return null;
        }
        return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }

    @Override
    public void onMarkerDrag(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMarkerDragEnd(Marker arg0) {
        // TODO Auto-generated method stub
//        LatLng dragPosition = arg0.getPosition();
//        mlat = dragPosition.latitude;
//        mlong = dragPosition.longitude;
//        setGoogleMap(mlat,mlong,1);
//        Log.i("info", "on drag end :" + mlat + " dragLong :" + mlong);
//        Toast.makeText(getActivity(), "Marker Dragged..! "+"==="+mlat+"----name"+arg0.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));
    }

    public void setGoogleMap(double mlat, double mlong, int r) {
        googleMap.clear();
        System.out
                .println("getLatitudeaddfence" + mlat + "getlonitude" + mlong);

        latLng = new LatLng(mlat, mlong);

//        markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        // markerOptions.title(addressText);
//
//markerOptions.icon(null);
        googleMap.addCircle(new CircleOptions().center(latLng).radius(r * 1000)
                .strokeColor(Color.BLACK).fillColor(0x30ff0000).strokeWidth(2));
        // Locate the first location
        // if(i==0)
        float zoomLevel=14;
        if (r>5 && r <24){
            zoomLevel = zoomLevel - (r / 12 + 3);
        }
        else {
            zoomLevel = zoomLevel - (r / 12 + 2);

        }

        System.out.println("=========================zoomLevel:::"+zoomLevel);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        // googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom);
    }


    private class GeocoderTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((AddGeoFence)getActivity()).showpDialog();
        }

        @Override
        protected String doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            String result = null;
            try {
                List<Address> addressList = geocoder.getFromLocation(
                        mlat, mlong, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());
                    result = sb.toString();


                    if (result != null) {
                        searchLocation = result;


                        ManualSearchFragment.GeofenceTOAdd = new Geofencemodel();
                        ManualSearchFragment.GeofenceTOAdd.setGeoName(locationName[0]);
                        ManualSearchFragment.GeofenceTOAdd.setAddress(searchLocation);
                        ManualSearchFragment.GeofenceTOAdd.setRadius(radiusProgress + "");
                        ManualSearchFragment.GeofenceTOAdd.setLatitude(mlat);
                        ManualSearchFragment.GeofenceTOAdd.setLongitude(mlong);
                        ((AddGeoFence) getActivity()). mGeofencesAdded = true;
                        ((AddGeoFence) getActivity()).addGeofencesButtonHandler(ManualSearchFragment.GeofenceTOAdd.getGeofence());

                             /*((AddGeoFence) getActivity()).mGeofencesAdded = true;
                                    ((AddGeoFence) getActivity()).addGeofencesButtonHandler(GeofenceTOAdd.getGeofence());*/
                    }
                }
            } catch (IOException e) {
                Log.e("TAG", "Unable connect to Geocoder", e);
            }
            return result;
        }


        @Override
        protected void onPostExecute(String addresses) {
            if (addresses==null){
                ((AddGeoFence)getActivity()).hidepDialog();
                Toast.makeText(getActivity(), getResources().getString(R.string.Geofence_notcreate), Toast.LENGTH_SHORT).show();

            }


        }
    }

}
