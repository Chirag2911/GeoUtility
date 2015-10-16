package com.killerrech.adapter;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.killerrech.model.Geofencemodel;


import geofence.killerrech.com.GeoAlert.R;

public class GeofenceAdapter extends BaseAdapter {
	private Context mcontext;
	// private ArrayList mtagList;
	private LayoutInflater inflator;
	// private ViewHolderTag viewHolder;
private ArrayList<Geofencemodel> mgeoList;
	private ViewHolderGeoTag viewHolder;

	int x1, x2;
	int i;

	private Button medit, delete;


	private boolean array[];
	int pos;

	public GeofenceAdapter(Context mcon, ArrayList<Geofencemodel> tagDataList) {

		mcontext = mcon;

		// mtagList= new ArrayList();
		inflator = (LayoutInflater) mcontext
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

mgeoList= new ArrayList<>();
		mgeoList=tagDataList;
		// mtagList= new ArrayList<TagDataItem>();
		// mtagList=tagDataList;




		// mTagImage = ImageLoader.getInstance();
		//
		// options = new DisplayImageOptions.Builder().cacheInMemory(true)
		// .cacheOnDisc(true).resetViewBeforeLoading(true)
		// .showImageForEmptyUri(R.drawable.ic_launcher)
		// .showImageOnFail(R.drawable.ic_launcher)
		// .showImageOnLoading(R.drawable.ic_launcher).build();
		//

	}

	// public void setData(ArrayList<Danger_Tag_List_Items> data) {
	// mtagList.addAll(data);
	// }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return mtagList.size();

		return mgeoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			viewHolder = new ViewHolderGeoTag();

			convertView = inflator.inflate(R.layout.geofencelistitem, null);
			// ImageView img=
			// (ImageView)convertView.findViewById(R.id.tagprofilepic);

			// viewHolder.tagimage = (ImageView) convertView
			// .findViewById(R.id.tagPic);
			viewHolder.address = (TextView) convertView
					.findViewById(R.id.mgeofencetaglocation);
			viewHolder.relative = (RelativeLayout) convertView
					.findViewById(R.id.mgeofencelinear);
			viewHolder.mdelete = (Button) convertView
					.findViewById(R.id.mdelete);
			viewHolder.medit = (Button) convertView.findViewById(R.id.medit);

			viewHolder.geofencename = (TextView) convertView
					.findViewById(R.id.mgeofenceTagname);

			convertView.setTag(viewHolder);
		}

		else {
			viewHolder = (ViewHolderGeoTag) convertView.getTag();
		}

		viewHolder.medit.setTag(position);
		viewHolder.mdelete.setTag(position);
		System.out.println("posi...." + position);
//		if (array[position] == true) {
//			i = position;
//			viewHolder.medit.setVisibility(View.VISIBLE);
//			viewHolder.medit.setAnimation(AnimationUtils.loadAnimation(
//					mcontext, R.anim.fade_in));
//			viewHolder.mdelete.setVisibility(View.VISIBLE);
//			viewHolder.mdelete.setAnimation(AnimationUtils.loadAnimation(
//					mcontext, R.anim.fade_in));
//			array[position] = false;
//
//		} else {
//			array[position] = false;
//			viewHolder.medit.setVisibility(View.GONE);
//			viewHolder.mdelete.setVisibility(View.GONE);
//			if (position == i) {
//				viewHolder.medit.setAnimation(AnimationUtils.loadAnimation(
//						mcontext, R.anim.fade_out));
//
//				viewHolder.mdelete.setAnimation(AnimationUtils.loadAnimation(
//						mcontext, R.anim.fade_out));
//			}
//		}

		viewHolder.geofencename.setText(mgeoList.get(position)
				.getGeoName());
		pos = position;
		viewHolder.address.setText(mgeoList.get(position).getAddress());
		viewHolder.medit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		viewHolder.mdelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


			}
		});
		// viewHolder.tagname.setText(mtagList.get(position).getTagName());
		// viewHolder.tagAddress.setText(mtagList.get(position).getLocationName());
		// viewHolder.relative.setOnLongClickListener(new OnLongClickListener()
		// {
		//
		// @Override
		// public boolean onLongClick(View v) {
		// // TODO Auto-generated method stub
		// viewHolder.mdelete.setVisibility(View.VISIBLE);
		// viewHolder.medit.setVisibility(View.VISIBLE);
		// return true;
		// }
		// });

		return convertView;

	}



class ViewHolderGeoTag {

	TextView geofencename;
	TextView address;
	Button medit, mdelete;
	RelativeLayout relative;

	//

}}
