package com.killerrech.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.killerrech.model.NearByModel;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Chiggy on 16/11/15.
 */
public class NearByParser {
    String json = "";
    ArrayList<NearByModel> mNearByDataList;


    public void getJsonResponse() {
    }


    public String getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            URL url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setRequestMethod("GET");

// read the response
            System.out.println("Response Code: " + conn.getResponseCode());
            InputStream in = new BufferedInputStream(conn.getInputStream());
            json = IOUtils.toString(in, "UTF-8");


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ArrayList<NearByModel> getNearBYListFromJson(String json) {


        if (json != null) {
            mNearByDataList = new ArrayList<>();
            try {
                JSONObject mRootObj = new JSONObject(json);
                JSONArray mRootArray = mRootObj.getJSONArray("results");
                for (int i = 0; i < mRootArray.length(); i++) {

                    NearByModel model = new NearByModel();


                    JSONObject mSubObj = mRootArray.getJSONObject(i);

                    model.setLatitude(mSubObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                    model.setLongitude(mSubObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));

                    model.setIcon(mSubObj.getString("icon"));

                    model.setBitmap(getBitmapFromURL(mSubObj.getString("icon")));
                    model.setPlaceId(mSubObj.getString("place_id"));
                    model.setName(mSubObj.getString("name"));
                    model.setAddressName(mSubObj.getString("vicinity"));

                    mNearByDataList.add(model);


                }


            } catch (Exception e) {

            }

        }


        return mNearByDataList;

    }


}
