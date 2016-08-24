package com.dab.climbz;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ListActivity {

    private static final String LOG_TAG = "MainActivity";
    private Bus mBus;
    private ClimbzService mClimbzService;
    private RouteArrayAdapter mRouteArrayAdapter = null;

    public static List<RouteObject> mRoutes = new ArrayList<RouteObject>();
    public static List<RouteObject> mRoutesTemporary = new ArrayList<RouteObject>();

    @Subscribe public void onRouteUpdate(final ClimbzService.RouteUpdateEvent event) {
        mRouteArrayAdapter.clear();
        mRouteArrayAdapter.addAll(event.getRoutes());
        mRouteArrayAdapter.notifyDataSetChanged();
    }

    private ServiceConnection mClimbzServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ClimbzService.ClimbzServiceBinder climbzBinder = (ClimbzService.ClimbzServiceBinder)iBinder;
            mClimbzService = climbzBinder.getService();
            mBus = mClimbzService.getEventBus();
            mBus.register(MainActivity.this);

            ListView listview = (ListView) findViewById(android.R.id.list);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position,
                                        long id) {
                    Toast.makeText(getApplicationContext(),
                            "Click ListItem Number " + position, Toast.LENGTH_LONG)
                            .show();
                    Intent singleRouteIntent = new Intent(MainActivity.this, SingleRouteActivity.class);

                    singleRouteIntent.putExtra("routepos", position);
                    singleRouteIntent.putExtra("route object", mClimbzService.getRoute(position));
                    MainActivity.this.startActivity(singleRouteIntent);

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBus.unregister(MainActivity.this);
        }
    };



    private void initRouteAdapter() {
    	mRouteArrayAdapter = new RouteArrayAdapter(MainActivity.this, mRoutes);
    	getListView().setAdapter(mRouteArrayAdapter);
    	mRouteArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initRouteAdapter();
        //request route list
        Intent intent = new Intent(this, ClimbzService.class);
        startService(intent);
        bindService(intent, mClimbzServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public class compareRoutesGrade implements Comparator<RouteObject> {
        @Override
        public int compare(RouteObject obj1, RouteObject obj2){
            return (obj1.getGrade().compareTo(obj2.getGrade()));
        }
    }

    public class compareRoutesLocation implements Comparator<RouteObject> {
        @Override
        public int compare(RouteObject obj1, RouteObject obj2){
            return (obj1.getLocation().compareTo(obj2.getLocation()));
        }
    }

    public class compareRoutesDate implements Comparator<RouteObject> {
        @Override
        public int compare(RouteObject obj1, RouteObject obj2){
            return (obj1.getDate().compareTo(obj2.getDate()));
        }
    }

    public class compareRoutesSetter implements Comparator<RouteObject> {
        @Override
        public int compare(RouteObject obj1, RouteObject obj2){
            return (obj1.getSetter().compareTo(obj2.getSetter()));
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_add_route:
            Intent addRouteIntent = new Intent(MainActivity.this, AddRouteActivity.class);
            MainActivity.this.startActivity(addRouteIntent);
            return true;
        case R.id.action_website:
        	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bkbs.brooklynboulders.com"));
        	startActivity(browserIntent);
            return true;
        case R.id.action_map:
            Intent browserIntentMap = new Intent(Intent.ACTION_VIEW, Uri.parse("http://i.imgur.com/ayQOdRF.png"));
            startActivity(browserIntentMap);
            return true;
        case R.id.action_sortG:
            //temporarily coding a sorting function here.
            //TODO: move this function somewhere else
            mRoutesTemporary = mRoutes;
            Collections.sort(mRoutes, new compareRoutesGrade());
            mRouteArrayAdapter.notifyDataSetChanged();
            return true;
        case R.id.action_sortD:
            //temporarily coding a sorting function here.
            //TODO: move this function somewhere else
            mRoutesTemporary = mRoutes;
            Collections.sort(mRoutes, new compareRoutesDate());
            mRouteArrayAdapter.notifyDataSetChanged();
            return true;
        case R.id.action_sortL:
            //temporarily coding a sorting function here.
            //TODO: move this function somewhere else
            mRoutesTemporary = mRoutes;
            Collections.sort(mRoutes, new compareRoutesLocation());
            mRouteArrayAdapter.notifyDataSetChanged();
            return true;
        case R.id.action_sortS:
            //temporarily coding a sorting function here.
            //TODO: move this function somewhere else
            mRoutesTemporary = mRoutes;
            Collections.sort(mRoutes, new compareRoutesSetter());
            mRouteArrayAdapter.notifyDataSetChanged();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}