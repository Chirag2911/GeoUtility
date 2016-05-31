package com.killerrech.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.killerrech.model.Geofencemodel;

import java.util.ArrayList;

import geofence.killerrech.com.GeoAlert.MainActivity;
import geofence.killerrech.com.GeoAlert.R;


public class ListAdapter extends RecyclerView
        .Adapter<ListAdapter.DataObjectHolder > {
private static String LOG_TAG = "MyRecyclerViewAdapter";
//    private ArrayList<Person> mDataset;
    private ArrayList<Geofencemodel> mDataset;
    private Context mContext;

    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView tag_name;
        TextView tag_address;
        LinearLayout btn_edit;
        LinearLayout btn_delete;







        public DataObjectHolder(View itemView) {
            super(itemView);
            tag_name = (TextView) itemView.findViewById(R.id.mgeofenceTagname);
            tag_address = (TextView) itemView.findViewById(R.id.mgeofencetaglocation);


            btn_edit = (LinearLayout) itemView.findViewById(R.id.medit);
            btn_delete = (LinearLayout) itemView.findViewById(R.id.mdelete);


            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public ListAdapter(ArrayList<Geofencemodel> myDataset, Context mContext) {
        mDataset = myDataset;
        this.mContext=mContext;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        holder.tag_name.setText(mDataset.get(position).getGeoName());
        holder.tag_address.setText(mDataset.get(position).getAddress());


        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editGeofence(position);

            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGeofence(position);

            }
        });


    }

    public void addItem(Geofencemodel dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }




    private void deleteGeofence(int position) {
        ((MainActivity)mContext).deleteFromDb(position);

    }

    private void editGeofence(int position) {
        ((MainActivity)mContext).editGeofence(position);

    }

}