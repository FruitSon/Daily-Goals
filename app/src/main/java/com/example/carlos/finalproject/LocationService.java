package com.example.carlos.finalproject;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;


public class LocationService extends Service implements GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Handler mHandler;
    private Runnable onRequestLocation;
    DatabaseHelper mDbHelper;

    public Criteria criteria;
    private Location lastKnownLocation, curLocation;
    private GoogleApiClient mGoogleApiClient;

    //REAL
//    private long UPDATE_DURATION = 30000L;
    //FOR DEMO
    private long UPDATE_DURATION = 10000L;


    public LocationService() {

    }

    @Override
    public void onCreate() {
        buildGoogleApiClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent.getAction().equals("startTracking")) {
            Log.d("onStartCommand", "on");

            lastKnownLocation = null;
            curLocation = null;

            //set up the criteria for provider
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setSpeedRequired(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

            mDbHelper = new DatabaseHelper(getApplicationContext());

            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    curLocation = location;
                    String str = "Location update:"+location.getLongitude()+","+location.getLatitude();
                    Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
                    DateFormat df = new SimpleDateFormat("HH:mm");
                    String time = df.format(Calendar.getInstance().getTime());
                    updateActualLocation(location, time);
                    mLocationManager.removeUpdates(mLocationListener);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {
                }

                @Override
                public void onProviderDisabled(String s) {
                }
            };

            mHandler = new Handler();
            onRequestLocation = new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    //String provider = mLocationManager.getBestProvider(criteria, true);
                    //Toast.makeText(getApplication(),"the"+cnt+" "+provider,Toast.LENGTH_SHORT).show();
                    //use recommended provider
                    //mLocationManager.requestLocationUpdates(provider,0,0,mLocationListener);

                    //use network provider
                    mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                    mHandler.postDelayed(onRequestLocation, UPDATE_DURATION);

//                //use place api to get current location and the type of current location
//                if(!mGoogleApiClient.isConnected()) buildGoogleApiClient();
//
//                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient,null);
//                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
//                    @Override
//                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
//                        float likelihood = -1;
//                        Place tarPlace = null;
//                        if(likelyPlaces==null || likelyPlaces.getCount()==0){
//                            Toast.makeText(getApplicationContext(),"no result returned",Toast.LENGTH_SHORT).show();
//                        }
//                        else{
//                            for(PlaceLikelihood pl:likelyPlaces){
//                                if(pl.getLikelihood()>likelihood) {
//                                    likelihood = pl.getLikelihood();
//                                    tarPlace = pl.getPlace();
//                                }
//                            }
//                        }
//                        DateFormat df = new SimpleDateFormat("HH:mm");
//                        String time = df.format(Calendar.getInstance().getTime());
//
//                        updateActualPlace(tarPlace, time);
//                        String str;
//                        if(tarPlace!=null)
//                            str = tarPlace.getAttributions().toString();
//                        else str = "nothing";
//                        Toast.makeText(getApplicationContext(), "cur location" + str + " " + time, Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//                mHandler.postDelayed(onRequestLocation, UPDATE_DURATION);
//                    showActivities();
                }
            };

            mHandler.post(onRequestLocation);
        }else if(intent.getAction().equals("stopTracking")){
            Log.d("onStartCommand", "off");
            if (mHandler != null) {
                mHandler.removeCallbacks(onRequestLocation);
            }
            stopSelf();

        }
        return START_STICKY;
    }


    /**
     *
     * @param location
     * @param time
     *
     * function for record location data with mock up type into database
     */
    private void updateActualLocation(Location location, String time) {

        try {
            mDbHelper.createDataBase();
        } catch (IOException e) {
            throw new Error("Unable to create database");
        }
        try {
            mDbHelper.openDataBase();
        } catch (SQLException e) {
            throw e;
        }

        //Type mock up when use network provider
        int type = (int)Math.floor(Math.random() * 5);
        String query = "INSERT INTO ActualActivity (LocationLat,LocationLon,StartTime,Type) VALUES ('" + location.getLatitude()
                + "','" + location.getLongitude() + "','" + time + "','" + type + "')";
        Cursor cur = mDbHelper.getWritableDatabase().rawQuery(query, null);
        cur.moveToFirst();
        cur.close();
        mDbHelper.close();
    }

    /**
     *
     * @param place
     * @param time
     *
     * function for insert location data with type into database
     */
    private void updateActualPlace(Place place, String time) {

        try {
            mDbHelper.createDataBase();
        } catch (IOException e) {
            throw new Error("Unable to create database");
        }

        try {
            mDbHelper.openDataBase();
        } catch (SQLException e) {
            throw e;
        }
        String query;
        if(place!=null) {
            query = "INSERT INTO ActualActivity (LocationLat,LocationLon,StartTime,Type) VALUES ('" + place.getLatLng().latitude
                    + "','" + place.getLatLng().longitude + "','" + time + "','" + place.getPlaceTypes().get(0) + "')";
        }else{
            query = "INSERT INTO ActualActivity (LocationLat,LocationLon,StartTime,Type) VALUES ('-1.0','-1.0','11:00','1')";
        }
        Cursor cur = mDbHelper.getWritableDatabase().rawQuery(query, null);

        cur.moveToFirst();
        cur.close();
        mDbHelper.close();
        //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
    }


    public void showActivities() {
        // Displays all activities in a Toast
        //TODO: remove

        try {
            mDbHelper.createDataBase();
        } catch (IOException e) {
            throw new Error("Unable to create database");
        }


        try {
            mDbHelper.openDataBase();
        } catch (SQLException e) {
            throw e;
        }


        String query2 = "SELECT * FROM ActualActivity";
        Cursor cursor2 = mDbHelper.getWritableDatabase().rawQuery(query2, null);
        String message = "";

        try {
            if (cursor2.moveToFirst()) {
                do {
                    message = message + " --------- " + cursor2.getString(0) + ',' + cursor2.getString(1)
                            + ',' + cursor2.getString(2) + ',' + cursor2.getString(3) + ',' + cursor2.getString(4);
                } while (cursor2.moveToNext());
                cursor2.close();
            }
        } finally {
            cursor2.close();
            mDbHelper.close();
        }

       // Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),connectionResult.getErrorMessage(),Toast.LENGTH_SHORT);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }
}
