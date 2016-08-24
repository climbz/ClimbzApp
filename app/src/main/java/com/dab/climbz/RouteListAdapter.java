package com.dab.climbz;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class RouteListAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	List<String> mRouteList = new ArrayList<String>();
	Context mContext;
	
	OnClickListener mRouteInfoListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
	        Intent singleRouteIntent = new Intent(mContext, SingleRouteActivity.class);
	        mContext.startActivity(singleRouteIntent); 			
		}
	};
	
	OnCheckedChangeListener mRouteCheckBoxListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		}
	};
	
	public RouteListAdapter(Context context, List<String> routeList) {
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext= context;
		updateRouteList(routeList);
	}
	
	public void updateRouteList(List<String> routeList)
	{
		mRouteList.clear();
		if (null != routeList) {
			mRouteList.addAll(routeList);
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.list_item_route, null);
		}
		
		TextView title = (TextView)convertView.findViewById(R.id.route_name);
		title.setText(mRouteList.get(index));
		title.setOnClickListener(mRouteInfoListener);
		
		CheckBox check = (CheckBox)convertView.findViewById(R.id.route_checkbox);
		check.setOnCheckedChangeListener(mRouteCheckBoxListener);
		
		return convertView;
	}

	@Override
	public int getCount() {
		return mRouteList.size();
	}

	@Override
	public Object getItem(int position) {
		return mRouteList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
