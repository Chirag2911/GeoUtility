package geofence.killerrech.com.GeoAlert;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import com.killerrech.Utility.PlaceJSONParser;
import com.killerrech.database.TablesController;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;


public class AutoSearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    TablesController tbController;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean flag = false;
    private SeekBar mseekbar;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    int radius;
    private String searchLocation;
    LatLng latLng;
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    private TextView mtxtRadius;
    List<HashMap<String, String>> places = null;
    private double mlat, mlong;

    private EditText medit;
View view;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



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




    /***** Sets up the map if it is possible to do so *****/



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

            view = inflater.inflate(R.layout.fragment_auto_search, container,
                    false);

        medit=(EditText)view.findViewById(R.id.geofencebutton);
        tbController= TablesController.getTablesController(getActivity());
        tbController.open();
        view.findViewById(R.id.fab_save_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                long insertedid=tbController.addGeoLocation(""+mlat,""+mlong,mtxtRadius.getText().toString(),searchLocation,medit.getText().toString());
//                System.out.println("-----insert--id"+insertedid);

                Intent mintent = new Intent(getActivity(),MainActivity.class
                );
                startActivity(mintent);
                getActivity().finish();
            }
        });

//                googleMap = ((SupportMapFragment) getChildFragmentManager()
//                        .findFragmentById(R.id.map)).getMap();
        atvPlaces=(AutoCompleteTextView)view.findViewById(R.id.atv_places);
        mtxtRadius=(TextView)view.findViewById(R.id.txtradius);
        mseekbar = (SeekBar) view.findViewById(R.id.geofenceseekBar);



        googleMap =    ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map)).getMap();


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
                mtxtRadius.setText("" + progress + "Km");


                if ((mlat != 0) && (mlong != 0)) {

                    setGoogleMap(mlat, mlong, progress);

                }
            }
        });

        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                System.out.println("getlocation"
                        + places.get(position).get("description")
                        .toString());

                String location = places.get(position).get("description")
                        .toString();

                if (location != null && !location.equals("")) {
                    searchLocation=location;
                    new GeocoderTask().execute(location);
                }

            }
        });
        radius = Integer.parseInt(mtxtRadius.getText().toString());

        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                System.out.println("<<<<<<<<change");
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        // Loading map


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
    class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + "AIzaSyDAf-VXPDt_XGBB-8LvBQuN_-rkTnX708k";

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");

                System.out.println("<<<<<<<<<change2" + input);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
                    + output + "?" + parameters;

            try {
                // Fetching the data from web service in background
                System.out.println("<<<<<<<<<cha" + url);
                data = downloadUrl(url);

                System.out.println("<<<<<<<<<change3" + data);
            } catch (Exception e) {
                System.out.println("<<<<<<<<<change4" + e);
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask

            System.out.println("<<<<<<<change121" + result);
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

//    public void setlocationcoordinate(String loc_name) {
//        String response = null;
//        try {
//
//            loc_name = getSpiltString(loc_name);
//            System.out.println("getjsonstring" + loc_name);
//            response = getJson("https://maps.googleapis.com/maps/api/place/textsearch/json?query="
//                    + loc_name + "&key=AIzaSyA1Z5DYzIxn10Sv0B6l3rM0tWsB2fKKqA0");
//            System.out.println("getJson" + response);
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            System.out.println("getJsonexception" + response);
//            e.printStackTrace();
//        }
//
//        try {
//            JSONObject json = new JSONObject(response);
//            JSONArray jarry = json.getJSONArray("results");
//            JSONObject mjosnobj = jarry.getJSONObject(0);
//            mlat = mjosnobj.getJSONObject("geometry").getJSONObject("location")
//                    .getDouble("lat");
//            mlong = mjosnobj.getJSONObject("geometry")
//                    .getJSONObject("location").getDouble("lng");
//
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }

//    public String getJson(String url) {
//        StringBuilder builder = new StringBuilder();
//        HttpClient client = new DefaultHttpClient();
//        HttpGet httpGet = new HttpGet(url);
//        try {
//            HttpResponse response = client.execute(httpGet);
//            StatusLine statusLine = response.getStatusLine();
//            int statusCode = statusLine.getStatusCode();
//            if (statusCode == 200) {
//                HttpEntity entity = response.getEntity();
//                InputStream content = entity.getContent();
//                BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(content));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    builder.append(line);
//                }
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return builder.toString();
//
//    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while  ", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends
            AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(
                String... jsonData) {

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

                System.out.println("<<<<<<<<change5" + places);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[] { "description" };
            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), result,
                    android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);

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

                    setGoogleMap(mlat, mlong, radius);


                }
            } else {

                flag = false;

                setGoogleMap(mlat, mlong, radius);
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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
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
        System.out.println("getjsonaarray" + a);
        cname = a[0];
        for (int i = 0; i < a.length; i++) {
            if (i > 0)
                cname = cname + a[i];

        }
        return cname;
    }



}
