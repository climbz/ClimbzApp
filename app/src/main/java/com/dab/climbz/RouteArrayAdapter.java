package com.dab.climbz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RouteArrayAdapter extends ArrayAdapter<RouteObject>{
	LayoutInflater mInflater;
	List<RouteObject> mRouteList = new ArrayList<RouteObject>();
	Context mContext;
	boolean mDidSetItemClickListener = false;
	
	
	
	public String getRouteColorByPosition(int pos){
		return mRouteList.get(pos).getColor();
	}

	public RouteArrayAdapter(Context context, int textViewResourceId) {
	    super(context, textViewResourceId);
	}

	public RouteArrayAdapter(Context context, List<RouteObject> routes) {
		super(context, 0, routes);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext= context;
		mRouteList = routes;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       final RouteObject route = getItem(position);    
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_route, parent, false);
       }
       // Lookup view for data population
       TextView grade = (TextView) convertView.findViewById(R.id.route_name);
       TextView location = (TextView) convertView.findViewById(R.id.route_location);
        TextView setter = (TextView) convertView.findViewById(R.id.route_setter);
        TextView color = (TextView) convertView.findViewById(R.id.route_color);

        TextView date = (TextView) convertView.findViewById(R.id.route_date);
       // Populate the data into the template view using the data object
       grade.setText(route.getGrade());
       location.setText(route.getLocation());
       color.setText(route.getColor());
        setter.setText(route.getSetter());

        date.setText(route.getDate().toString());

       // Return the completed view to render on screen
       return convertView;
   }
}
