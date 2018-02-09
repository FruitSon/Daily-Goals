package com.example.carlos.finalproject;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks {

    GoogleApiClient mGoogleApiClient;
    GoogleMap map;
    LocationRequest locationRequest;
    Location lastLocation;
    Button addActivityButton, setTimeButton;
    EditText activityNameEditText, activityTimeEditText, activityLocationEditText;
    TextView timeLabel;
    private LatLng activityLngLat;
    TimePicker timePicker;
    String selectedTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setTitle("Add Activity");
        //showActivities();

        activityNameEditText = findViewById(R.id.activityNameEditText);
        activityLocationEditText = findViewById(R.id.activityLocationEditText);

        // Set selected time to current time in case user doesn't change the default (current) time
        String delegate = "HH:mm";
        String currentTime = (String)DateFormat.format(delegate, Calendar.getInstance().getTime());
//        String hour = currentTime.split("\\:")[0];
//        String minute = currentTime.split("\\:")[1];
//        selectedTime = getTime(Integer.parseInt(hour), Integer.parseInt(minute));
        selectedTime = currentTime;

        timeLabel = findViewById(R.id.timeLabel);
        timeLabel.setText(currentTime);

        addActivityButton = findViewById(R.id.addButton);
        setTimeButton = findViewById(R.id.setTimeButton);

        final Context context = this;

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        selectedTime = getTime(selectedHour, selectedMinute);
                        timeLabel.setText(selectedTime);
                    }
                }, 24, 0, true);
                dialog.show();
            }
        });

        addActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onAddButtonClicked();
            }
        });

    }

    // converts military time to AM/PM
    private String getTime(int hr,int min) {
        Time time = new Time(hr,min,0);//seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(time);
    }

    private void onAddButtonClicked() {
        showActivities();
        if (activityNameEditText.getText().length() == 0) {
            Toast.makeText(this, "Please enter valid values in all fields.", Toast.LENGTH_SHORT).show();
        }

        else if (activityLngLat == null) {
            Toast.makeText(this, "Please select location on map", Toast.LENGTH_SHORT).show();
        }

        else {
            addActivityToDb();
        }
    }

    private void addActivityToDb() {
        DatabaseHelper myDbHelper = new DatabaseHelper(this);

        try {
            myDbHelper.createDataBase();

        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper.openDataBase();
        }

        catch (SQLException sqle) {
            Toast.makeText(this, "Cannot connect to the database.", Toast.LENGTH_LONG).show();
            throw sqle;
        }

        String query = "INSERT INTO ScheduledActivity (ActivityName, LocationName, LocationLon, LocationLat, StartTime) " +
                " VALUES ('" + activityNameEditText.getText() + "','" + activityLocationEditText.getText() + "'," +
                String.valueOf(activityLngLat.longitude) + "," + String.valueOf(activityLngLat.longitude) + ",'" +
                selectedTime + "')";

        Cursor cursor = myDbHelper.getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();

        cursor.close();
        myDbHelper.close();
        Toast.makeText(this, "Activity Added", Toast.LENGTH_SHORT).show();

        // Back to Share Activity
        finish();
    }

    public void showActivities() {
        // Displays all activities in a Toast
        //TODO: remove

        DatabaseHelper myDbHelper = new DatabaseHelper(this);
        try {
            myDbHelper.createDataBase();

        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        myDbHelper.openDataBase();
        String query2 = "SELECT * FROM ScheduledActivity";
        Cursor cursor2 = myDbHelper.getWritableDatabase().rawQuery(query2, null);
        String message = "";

        try {
            if (cursor2.moveToFirst()) {
                do {
                    message = message + " --------- " + cursor2.getString(0) +','+ cursor2.getString(1)
                            +','+ cursor2.getString(2)+','+ cursor2.getString(3)+','+ cursor2.getString(4)
                            +','+ cursor2.getString(5);
                }while (cursor2.moveToNext());
                cursor2.close();
            }
        } finally {
            cursor2.close();
            myDbHelper.close();
        }
    }

    //TODO: Check permissions on app start

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }

                }

                else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                activityLngLat = point;
                map.clear();
                map.addMarker(new MarkerOptions().position(point));
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                map.setMyLocationEnabled(true);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                lastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                LatLng latLng = new LatLng(43.7068, -72.2874);
                if (lastLocation != null) {
                    latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

            } else {
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(AddActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

}
