/**************************************************************************
*
* ClimbzService is in charge of maintaining a cache for the REST queries. This will enable 
* the UI to update quickly with the last known values while we wait for the
* network operation to complete in the background. 
* 
* ClimbzService receives a request via intent and broadcasts any cached copy of the result via intent.
* It will then use ClimbzRESTService to perform the network operation and broadcast
* the new results to the UI
*
***************************************************************************/
package com.dab.climbz;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import java.util.ArrayList;
import java.util.List;

public class ClimbzService extends Service {
    
    private static final String LOG_TAG = "ClimbzService";
    private static final String SERVER_URI = "http://alexamsterwebsite.appspot.com/climbing_routes";
    private static final String ROUTES_PREFERENCE_KEY = "routes_preference_key";
    private static final String PREFS_FILENAME = "prefs";

    private static final String PARSE_ROUTES_OBJECT_NAME = "routes";
    private static final String PARSE_JSON_KEY = "json";
    public static final String ACTION_GET_CLIMBING_ROUTES = "com.dab.climbz.ACTION_GET_CLIMBING_ROUTES";
    public static final String ACTION_RECEIVED_CLIMBING_ROUTES = "com.dab.climbz.ACTION_RECEIVED_CLIMBING_ROUTES";
    public static final String EXTRA_CLIMBING_ROUTES = "com.dab.climbz.EXTRA_CLIMBING_ROUTES";

    private List<RouteObject> mRoutesList = new ArrayList<RouteObject>();
    private IBinder mBinder = new ClimbzServiceBinder();
    private final Bus mBus = new Bus();
    private final Gson mGson = new Gson();
    private ParseObject mParseRoutes;

    @Produce public RouteUpdateEvent produceCurrentRoutes() {
        return new RouteUpdateEvent(mRoutesList);
    }

    @Override public void onCreate() {
    	super.onCreate();
        mBus.register(ClimbzService.this);
        loadRouteList();
    }
    
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

    	
        return START_NOT_STICKY;
    }
    
    @Override public void onDestroy() {
    	super.onDestroy();
    }

    public Bus getEventBus() {
        return mBus;
    }

    public synchronized RouteObject getRoute(int position) {
        return mRoutesList.get(position);
    }

    public synchronized List<RouteObject> getRoutes(){
        return mRoutesList;
    }

    public synchronized void addRoute(RouteObject newRoute) {

        mRoutesList.add(newRoute);
        ParseObject route = new ParseObject(PARSE_ROUTES_OBJECT_NAME);
        route.put(PARSE_JSON_KEY, mGson.toJson(newRoute));

        route.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                loadRouteList();
            }
        });

    }

    public synchronized void removeRoute(RouteObject oldRoute) {
        mRoutesList.remove(oldRoute);

    }

    public synchronized void clearRoutes() {
        mRoutesList.clear();
    }

    private synchronized void loadRouteList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_ROUTES_OBJECT_NAME);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                mRoutesList.clear();

                if (null == parseObjects) {
                    return;
                }

                for(ParseObject route : parseObjects){
                    String json = (String)route.get(PARSE_JSON_KEY);
                    RouteObject newRoute = mGson.fromJson(json, RouteObject.class);
                    newRoute.setID(route.getObjectId());
                    mRoutesList.add(newRoute);
                }
                mBus.post(new RouteUpdateEvent(mRoutesList));
            }
        });

        /*SharedPreferences prefs = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        String routeList = prefs.getString(ROUTES_PREFERENCE_KEY, "");
        if (!routeList.equals("")) {
            mRoutesList.clear();
            mRoutesList.addAll(Arrays.asList(mGson.fromJson(routeList, RouteObject[].class)));
        }*/
    }


	@Override public IBinder onBind(Intent intent) {
		return mBinder;
	}

    public class ClimbzServiceBinder extends Binder {
        ClimbzService getService() {
            return ClimbzService.this;
        }
    }

    public static class RouteUpdateEvent {
        private final List<RouteObject> mRouteList;

        public RouteUpdateEvent(List<RouteObject> routeList) {
            mRouteList = routeList;
        }

        public List<RouteObject> getRoutes() {
            return mRouteList;
        }
    }
}
