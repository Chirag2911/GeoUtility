package com.killerrech.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.killerrech.model.SettingsModel;

import java.util.ArrayList;

import geofence.killerrech.com.GeoAlert.R;
import geofence.killerrech.com.GeoAlert.Settings;

public class SettingsAdapter extends BaseAdapter {
	private Context mcontext;
	// private ArrayList mtagList;
	private LayoutInflater inflator;
	// private ViewHolderTag viewHolder;
private ArrayList<SettingsModel> mgeoList;
	private ViewHolder viewHolder;



	int pos;

	public SettingsAdapter(Context mcon, ArrayList<SettingsModel> tagDataList) {

		mcontext = mcon;

		// mtagList= new ArrayList();
		inflator = (LayoutInflater) mcontext
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		mgeoList=tagDataList;

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
			viewHolder = new ViewHolder();

			convertView = inflator.inflate(R.layout.grid_item, null);
			// ImageView img=
			// (ImageView)convertView.findViewById(R.id.tagprofilepic);

			// viewHolder.tagimage = (ImageView) convertView
			// .findViewById(R.id.tagPic);
			viewHolder.mButtonForName = (Button) convertView
					.findViewById(R.id.buttonForGridItem);

			viewHolder.mImageViewForCancel = (ImageView) convertView
					.findViewById(R.id.imageViewForCancel);
			viewHolder.mParentLay=(RelativeLayout)convertView.findViewById(R.id.relative);

			convertView.setTag(viewHolder);
		}

		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.mButtonForName.setText(mgeoList.get(position)
				.getName());
		pos = position;

		viewHolder.mButtonForName.setBackgroundColor(mcontext.getResources().getColor(mgeoList.get(position).getColor()));
		viewHolder.mParentLay.setBackgroundColor(mcontext.getResources().getColor(mgeoList.get(position).getColor()));
		viewHolder.mImageViewForCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((Settings)mcontext).removeItem(position);
				((Settings)mcontext).notifyMNearBy();


			}
		});


		return convertView;

	}



class ViewHolder {

//	TextView geofencename;
//	TextView address;
	RelativeLayout mParentLay;
	Button mButtonForName;
	ImageView mImageViewForCancel;

}}
