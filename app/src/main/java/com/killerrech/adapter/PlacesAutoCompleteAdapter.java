package com.killerrech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.killerrech.Utility.CreateInukshk;

import java.util.ArrayList;
import java.util.List;

import geofence.killerrech.com.GeoAlert.R;


public class PlacesAutoCompleteAdapter extends BaseAdapter implements
        Filterable {
    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<String> resultList = new ArrayList<>();
    private HideKeyBoard mListener;

    public PlacesAutoCompleteAdapter(Context context,HideKeyBoard listener) {
        mContext = context;
        mListener=listener;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public  String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.auto_search_item, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.title)).setText(getItem(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering( CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null && constraint.length()>3) {
                    // Retrieve the autocomplete results.

                    resultList = CreateInukshk.autocomplete(constraint
                            .toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                    mListener.hideKeyBoard();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }



public interface HideKeyBoard{
    public void  hideKeyBoard();
}

}









//
//public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements
//        Filterable {
//    private ArrayList<String> resultList;
//    Runnable runnable;
//    String medittext;
//    Handler handler = new Handler();
//
//    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
//        super(context, textViewResourceId);
//    }
//
//    @Override
//    public int getCount() {
//        return resultList.size();
//    }
//
//    @Override
//    public String getItem(int index) {
//        return resultList.get(index);
//    }
//    double prevtime,currentTimel;
//
//    @Override
//    public Filter getFilter() {
//        Filter filter = new Filter() {
//            @Override
//            protected FilterResults performFiltering( CharSequence constraint) {
//                FilterResults filterResults = new FilterResults();
//                if (constraint != null && constraint.length()>3) {
//                    // Retrieve the autocomplete results.
//
//                    resultList = CreateInukshk.autocomplete(constraint
//                            .toString());
//
//                    // Assign the data to the FilterResults
//                    filterResults.values = resultList;
//                    filterResults.count = resultList.size();
//                }
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint,
//                                          FilterResults results) {
//                if (results != null && results.count > 0) {
//                    notifyDataSetChanged();
//                } else {
//                    notifyDataSetInvalidated();
//                }
//            }
//        };
//        return filter;
//    }
//}
//
