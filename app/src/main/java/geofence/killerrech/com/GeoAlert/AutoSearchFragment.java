package geofence.killerrech.com.GeoAlert;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.killerrech.Utility.DelayAutoCompleteTextView;
import com.killerrech.adapter.PlacesAutoCompleteAdapter;
import com.killerrech.model.Geofencemodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AutoSearchFragment extends Fragment implements PlacesAutoCompleteAdapter.HideKeyBoard {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    int radiusProgress = 1;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean flag = false;
    private SeekBar mseekbar;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    private String searchLocation;
    LatLng latLng;
    DelayAutoCompleteTextView atvPlaces;

    private TextView mtxtRadius;
    List<HashMap<String, String>> places = null;
    private double mlat, mlong;
    public static boolean isFlag;
    boolean isRequestResponse;
    SimpleAdapter adapter;

    private EditText medit;
    View view;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    PlacesAutoCompleteAdapter myAdapter;


    // TODO: Rename and change types and number of parameters
    public static AutoSearchFragment newInstance(String param1, String param2) {
        AutoSearchFragment fragment = new AutoSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    public AutoSearchFragment() {
        // Required empty public constructor

    }

    List<HashMap<String, String>> mlist = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_auto_search, container,
                false);

        medit = (EditText) view.findViewById(R.id.geofencebutton);
        atvPlaces = (DelayAutoCompleteTextView) view.findViewById(R.id.atv_places);
        mtxtRadius = (TextView) view.findViewById(R.id.txtradius);
        mseekbar = (SeekBar) view.findViewById(R.id.geofenceseekBar);

        myAdapter = new PlacesAutoCompleteAdapter(getActivity(),this);


        atvPlaces.setAdapter(myAdapter);
        atvPlaces.setThreshold(3);


        atvPlaces.setLoadingIndicator(
                (android.widget.ProgressBar) view.findViewById(R.id.pb_loading_indicator));

        googleMap = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map)).getMap();


        view.findViewById(R.id.fab_save_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mGeoFencename = medit.getText().toString();
                if (mlat != 0.0 && mlong != 0.0) {
                    if (!mGeoFencename.equals(null) && !mGeoFencename.equals("")) {

                        final SaveGeofence msaveGeofenceTask = new SaveGeofence();
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
                        Snackbar.make(atvPlaces, getResources().getString(R.string.snackbar_name), Snackbar.LENGTH_SHORT).show();
                    }

                } else {
                    Snackbar.make(atvPlaces, getResources().getString(R.string.snackbar_loc), Snackbar.LENGTH_SHORT).show();
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
                    mtxtRadius.setText("" + radiusProgress + "Km");


                    if ((mlat != 0) && (mlong != 0)) {

                        setGoogleMap(mlat, mlong, radiusProgress);

                    }
                }
            }
        });


        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                String location = (String) parent.getItemAtPosition(position);

                if (location != null && !location.equals("")) {
                    searchLocation = location;
                    new GeocoderTask().execute(location);
                }

            }
        });


        // Changing map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


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


    private class SaveGeofence extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((AddGeoFence) getActivity()).showpDialog();
            isFlag = true;

//            ManualSearchFragment.GeofenceTOAdd.setRadius(mseekbar.getProgress() + "");

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }

        @Override
        protected String doInBackground(String... params) {

            ManualSearchFragment.GeofenceTOAdd = new Geofencemodel();
            ManualSearchFragment.GeofenceTOAdd.setGeoName(params[0]);
            ManualSearchFragment.GeofenceTOAdd.setAddress(searchLocation);
            ManualSearchFragment.GeofenceTOAdd.setRadius(radiusProgress + "");
            ManualSearchFragment.GeofenceTOAdd.setLatitude(mlat);
            ManualSearchFragment.GeofenceTOAdd.setLongitude(mlong);
            ((AddGeoFence) getActivity()).mGeofencesAdded = true;
            ((AddGeoFence) getActivity()).addGeofencesButtonHandler(ManualSearchFragment.GeofenceTOAdd.getGeofence());


            return null;
        }
    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text

                addresses = geocoder.getFromLocationName(locationName[0], 2);
            } catch (IOException e) {
                e.printStackTrace();
                flag = true;
//                setlocationcoordinate(locationName[0]);
                System.out.println("getlocationin" + e);
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (!flag) {
                if (addresses == null || addresses.size() == 0) {
                    Toast.makeText(getActivity(), "No Location found",
                            Toast.LENGTH_SHORT).show();
                }

                // Clears all the existing markers on the map
                googleMap.clear();

                // Adding Markers on Google Map for each matching address

                for (int i = 0; i < addresses.size(); i++) {

                    Address address = (Address) addresses.get(i);

                    // Creating an instance of GeoPoint, to display in Google
                    // Map
                    mlat = address.getLatitude();
                    mlong = address.getLongitude();

                    setGoogleMap(mlat, mlong, radiusProgress);


                }
            } else {

                flag = false;

                setGoogleMap(mlat, mlong, radiusProgress);
                // googleMap.clear();

            }
        }
    }

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
        float zoomLevel = 14;

        if (r > 5 && r < 24) {
            zoomLevel = zoomLevel - (r / 12 + 3);
        } else if (r < 6) {
            zoomLevel = zoomLevel - (r / 12 + 2);

        } else {
            zoomLevel = zoomLevel - (r / 12 + 2);

        }


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        // googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom);
    }

    private void drawCircle(LatLng point) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(20);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        googleMap.addCircle(circleOptions);
    }

    public String getSpiltString(String name) {
        String cname = null;
        String a[] = name.split(" ");
        cname = a[0];
        for (int i = 0; i < a.length; i++) {
            if (i > 0)
                cname = cname + a[i];

        }
        return cname;
    }

    @Override
    public void hideKeyBoard() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            if (medit != null)
                imm.hideSoftInputFromWindow(medit.getWindowToken(), 0);
        }catch (Exception e){}}

}
