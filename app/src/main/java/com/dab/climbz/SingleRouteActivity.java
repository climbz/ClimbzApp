package com.dab.climbz;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

public class SingleRouteActivity extends Activity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private ClimbzService mClimbzService;
    private VideoView mVideoView;

    private ServiceConnection mClimbzServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ClimbzService.ClimbzServiceBinder climbzBinder = (ClimbzService.ClimbzServiceBinder)iBinder;
            mClimbzService = climbzBinder.getService();

            Intent i = getIntent();
            int routepos = i.getExtras().getInt("routepos");
            //TODO:make getparcelableextra work
            //RouteObject routeObj = (RouteObject)i.getParcelableExtra("route object");
            RouteObject routeObj = mClimbzService.getRoute(routepos);
            TextView routetext = (TextView) findViewById(R.id.route_color);
            routetext.setText(routeObj.getColor() + " " + routeObj.getGrade() + " located at the " + routeObj.getLocation());
            //setContentView(routetext); //why was this line here? It ruins everything
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_route);
        bindService(new Intent(this, ClimbzService.class), mClimbzServiceConnection, Context.BIND_AUTO_CREATE);

        mVideoView = (VideoView) findViewById(R.id.videoView1);
        Button button = (Button) findViewById(R.id.button_capture);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });

	}

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            mVideoView.setVisibility(View.VISIBLE);
            mVideoView.setVideoURI(videoUri);
            //TODO: Figure out if the above code is useful and how to make it work (display established video)
        }
    }*/

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.single_route, menu);
		return true;
	}
}
