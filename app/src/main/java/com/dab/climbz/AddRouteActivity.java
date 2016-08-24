package com.dab.climbz;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SendCallback;

import java.util.Date;

public class AddRouteActivity extends Activity {

    private ClimbzService mClimbzService;


    private ServiceConnection mClimbzServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            ClimbzService.ClimbzServiceBinder climbzBinder = (ClimbzService.ClimbzServiceBinder)iBinder;
            mClimbzService = climbzBinder.getService();

            Spinner locSpinner = (Spinner) findViewById(R.id.location_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> lAdapter = ArrayAdapter.createFromResource(AddRouteActivity.this,
                    R.array.location_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            lAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            locSpinner.setAdapter(lAdapter);
            locSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if(position>8){
                        setRouteOrBoulder(false);
                    }else{
                        setRouteOrBoulder(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // do nothing
                }
            });

            Spinner colorSpinner = (Spinner) findViewById(R.id.color_spinner);
            ArrayAdapter<CharSequence> cAdapter = ArrayAdapter.createFromResource(AddRouteActivity.this,
                    R.array.color_array, android.R.layout.simple_spinner_item);
            cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            colorSpinner.setAdapter(cAdapter);

            Spinner setterSpinner = (Spinner) findViewById(R.id.setter_spinner);
            ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(AddRouteActivity.this,
                    R.array.setter_array, android.R.layout.simple_spinner_item);
            sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            setterSpinner.setAdapter(sAdapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_add_route);

        bindService(new Intent(this, ClimbzService.class), mClimbzServiceConnection, Context.BIND_AUTO_CREATE);



	}
	
	private void setRouteOrBoulder(boolean norope){
		Spinner gradespinner = (Spinner) findViewById(R.id.grade_spinner);
		if(norope){
			ArrayAdapter<CharSequence> gadapter = ArrayAdapter.createFromResource(this,
					R.array.boulder_grade_array, android.R.layout.simple_spinner_item);
			gadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			gradespinner.setAdapter(gadapter);
		}else{
			ArrayAdapter<CharSequence> gadapter = ArrayAdapter.createFromResource(this,
			        R.array.route_grade_array, android.R.layout.simple_spinner_item);
			gadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			gradespinner.setAdapter(gadapter);
		}
		
		Button button = (Button) findViewById(R.id.add_route_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	CalendarView cv = (CalendarView) findViewById(R.id.calendar);
            	Date day = new Date(cv.getDate());
            	Spinner colorSpinner = (Spinner) findViewById(R.id.color_spinner);
            	String color = colorSpinner.getSelectedItem().toString();
            	Spinner gradeSpinner = (Spinner) findViewById(R.id.grade_spinner);
            	String grade = gradeSpinner.getSelectedItem().toString();
            	Spinner locSpinner = (Spinner) findViewById(R.id.location_spinner);
            	String location = locSpinner.getSelectedItem().toString();
                Spinner setSpinner = (Spinner) findViewById(R.id.setter_spinner);
                String setter = setSpinner.getSelectedItem().toString();
                mClimbzService.addRoute(new RouteObject(day, grade, color, location, setter, ""));
                ParsePush push = new ParsePush();
                push.setMessage("Route was added! " + color + " " + grade + " @ " + location);
                push.sendInBackground(new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("com.parse.push","Push notification didn't work...why?", e);
                    }
                });
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_route, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
