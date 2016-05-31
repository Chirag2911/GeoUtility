package com.killerrech.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.killerrech.model.SettingsModel;

import java.util.ArrayList;

import geofence.killerrech.com.GeoAlert.R;
import geofence.killerrech.com.GeoAlert.Settings;

public class DialogAdapter extends BaseAdapter {
	private Context mcontext;
	// private ArrayList mtagList;
	private LayoutInflater inflator;
	// private ViewHolderTag viewHolder;
private ArrayList<SettingsModel> mgeoList;
	private ViewHolder viewHolder;



	int pos;

	public DialogAdapter(Context mcon, ArrayList<SettingsModel> tagDataList) {

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

			convertView = inflator.inflate(R.layout.dialog_grid_item, null);
			// ImageView img=
			// (ImageView)convertView.findViewById(R.id.tagprofilepic);

			// viewHolder.tagimage = (ImageView) convertView
			// .findViewById(R.id.tagPic);
			viewHolder.mButtonForName = (Button) convertView
					.findViewById(R.id.buttonForText);

			viewHolder.mImageViewForCancel = (CheckBox) convertView
					.findViewById(R.id.checkbox);
			viewHolder.mParentLay=(RelativeLayout)convertView.findViewById(R.id.layout);

			convertView.setTag(viewHolder);
		}

		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.mButtonForName.setText(mgeoList.get(position)
				.getName());
		pos = position;

				viewHolder.mImageViewForCancel.setChecked(mgeoList.get(position)
						.isActivated());


		viewHolder.mButtonForName.setBackgroundColor(mcontext.getResources().getColor(mgeoList.get(position).getColor()));
		viewHolder.mParentLay.setBackgroundColor(mcontext.getResources().getColor(mgeoList.get(position).getColor()));
		viewHolder.mImageViewForCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean active=mgeoList.get(position)
						.isActivated();
				active=!active;
				viewHolder.mImageViewForCancel.setChecked(active);

				if (active){
					((Settings)mcontext).addNearBy(position);
				}else {
					((Settings)mcontext).removeFromNearBy(position);

				}


			}
		});


		return convertView;

	}



class ViewHolder {

//	TextView geofencename;
//	TextView address;
	RelativeLayout mParentLay;
	Button mButtonForName;
	CheckBox mImageViewForCancel;

}}
