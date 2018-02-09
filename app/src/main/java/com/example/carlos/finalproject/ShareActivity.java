package com.example.carlos.finalproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Inet4Address;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import static com.example.carlos.finalproject.Constants.RESULT_CONFIRM;
import static com.example.carlos.finalproject.Constants.RESULT_DENY;


/*
* code related to sensor data process is modified from the sample code of CS169 Mobile Sensing Winter2016
* */
public class ShareActivity extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = "ShareActivity";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_ADD_ACTIVITY = 4;

    //bluetooth
    private BluetoothAdapter mBluetoothAdapter = null;//for communication

    //String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    //UI interface
    Button tmpButton, addActivityButton;
    ListView listView;

    //share_activity_list
    List<ActivityInfo> activityList;
    private ShareActivityAdapter adapter;

    //decide who start the connection
    private boolean activeRole=false;

    //bluetooth communication buffer
    private String receivedAct=null;

    //indicate which button user click
    private int mPosition=0;

    //sensor stuff
    public class Tag{
        long time;
        double tag;
        public Tag(long ts, double t){
            this.time = ts;
            this.tag = t;
        }

    }
    private String curTag = "";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private int shakeTime = 0;
    private boolean onDetect = false;
    private ArrayBlockingQueue<Tag> mBuffer;
    private final int WINDOW_SIZE = 30;
    private Filter filter;
    private OnSensorChangedTask mAsyncTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        activityList= new LinkedList<>();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mBuffer = new ArrayBlockingQueue<>(WINDOW_SIZE);

        readActivityDataFromDatabase();
        viewSetup();
        blueToothSetUp();

        addActivityButton = findViewById(R.id.addActivityButton);
        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                startActivityForResult(intent,REQUEST_ADD_ACTIVITY);
            }
        });
    }

    //read activity data from database
    public void readActivityDataFromDatabase() {
        activityList.clear();

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
                    ActivityInfo act =  new ActivityInfo(cursor2.getString(0),cursor2.getString(1),cursor2.getString(2)
                            ,cursor2.getString(3),cursor2.getString(4),cursor2.getString(5));
                    activityList.add(act);
                }while (cursor2.moveToNext());
                cursor2.close();
            }
        } finally {
            cursor2.close();
            myDbHelper.close();
        }


        if (!activityList.isEmpty()) {
            Collections.sort(activityList, new Comparator<ActivityInfo>() {
                @Override
                public int compare(ActivityInfo object1,
                                   ActivityInfo object2) {

                    //return ((String) object1.get("text_content")).compareTo((String) object2.get("text_content"));
                    return object1.actTime.compareTo(object2.actTime);
                }
            });
        }

    }

    //set up the views
    private void viewSetup(){
        //listView set up
        listView = findViewById(R.id.share_activity_list);
        adapter = new ShareActivityAdapter
                (this, R.layout.shared_activity_list_cell, activityList);
        listView.setAdapter(adapter);

//        tmpButton=findViewById(R.id.tempButton);
//        tmpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendShareActivity();
//            }
//        });
    }


    //set up the bluetooth
    private void blueToothSetUp(){
        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();// for communication
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    //set up for bluetooth communication
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        //mChatService.start();

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    //The Handler that gets information back from the BluetoothChatService
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.d(TAG, "connected!!!!!!!!!!!!!!!!!!!!!!");
                            startDetect();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            //Toast.makeText(getApplicationContext(), "STATE_CONNECTING", Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:

                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG,writeMessage);
                    Toast.makeText(getApplicationContext(), "Event shared", Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG,readMessage);
                    if (!readMessage.isEmpty()){
                        startConfirmReceivedTask(readMessage);
                    }
                    mChatService.start();
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != this) {
                        Toast.makeText(getApplicationContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != this) {
                        activeRole=false;
                        Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            Log.d(TAG,"not connected!");
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    //connect another device through bluetooth
    private void connectDevice(String address, boolean secure) {
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mChatService != null) {
            mChatService.stop();
        }
        if(mAsyncTask!=null){
            mAsyncTask.cancel(true);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                e.getMessage();
                Log.d("error",e.getMessage());
            }
        }
        if(mSensorManager!=null)
            stopDetect();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String addr = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    if (addr!=null) {
                        connectDevice(addr, true);
                        activeRole=true;
                    }
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String addr = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    if (addr!=null) {
                        connectDevice(addr, false);
                        activeRole=true;
                    }
                }
                break;
            case REQUEST_ADD_ACTIVITY:
                readActivityDataFromDatabase();
                adapter.notifyDataSetChanged();
                break;
        }

        //result back from AddSharedTaskActivity
        switch (resultCode){
            case RESULT_CONFIRM:
                if(receivedAct!=null) {
                    Gson  gson = new Gson();
                    ActivityInfo act  = gson.fromJson(receivedAct,ActivityInfo.class);
                    //to update the view
                    //activityList.add(act);
                    Log.d(TAG,"add activity");
                    //adapter.notifyDataSetChanged();
                    //to database
                    addActivityToDb(act);
                    //update the view
                    readActivityDataFromDatabase();
                    adapter.notifyDataSetChanged();
                }
                receivedAct=null;
                break;
            case RESULT_DENY:
                receivedAct=null;
                break;
        }
    }

    //start the DeviceListActivity for choosing the device to connect through bluetooth
    public void shareActivityTo(int position){
        mPosition = position;
        Intent intent = new Intent(getApplicationContext(), DeviceListActivity.class);
        startActivityForResult(intent,REQUEST_CONNECT_DEVICE_INSECURE);
    }

    public void deleteActivityAt(int position) {
        mPosition = position;

        String activityId = activityList.get(position).actId;
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

        String query = "DELETE FROM ScheduledActivity WHERE _id = " + activityId;

        Cursor cursor = myDbHelper.getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();

        cursor.close();
        myDbHelper.close();

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // update listView
                        readActivityDataFromDatabase();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Activity Deleted", Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this activity?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    //send the activity info through bluetooth as JSON
    //test use
    private void sendShareActivity(){
        Log.d(TAG, "writing!!!!!!!!!!!!!!!!!!!!!!");
        startDetect();
    }

    private void shareEvent(){
        //share event
        if(activeRole!=true)
            return;
        if(!activityList.isEmpty() &&  mPosition<activityList.size()) {
            Gson  gson = new Gson();
            sendMessage(gson.toJson(activityList.get(mPosition)));
        }else
            sendMessage("nothing");
    }

    //after receive the message, start AddSharedTaskActivity to confirm or deny the activity
    private void startConfirmReceivedTask(String readMessage){
        receivedAct=readMessage;
        Intent intent = new Intent(getApplicationContext(), AddSharedTaskActivity.class);
        intent.putExtra("activityDecription",receivedAct);
        startActivityForResult(intent,1);
    }

    //write the activity to database
    private void addActivityToDb(ActivityInfo act) {
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
                " VALUES ('" + act.actName + "','" + act.locName + "'," +
                act.lng + "," + act.lat + ",'" +
                act.actTime + "')";

        Cursor cursor = myDbHelper.getWritableDatabase().rawQuery(query, null);
        cursor.moveToFirst();

        cursor.close();
        myDbHelper.close();
        Toast.makeText(this, "Activity Added", Toast.LENGTH_SHORT).show();
    }


    /**
     * create a new asyncTack for sensor detecting
     * */
    private void startDetect(){
        if(!onDetect){
            mAsyncTask = new OnSensorChangedTask();
            mAsyncTask.execute();
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else{
            return;
        }
        onDetect = true;

        double SMOOTH_FACTOR = 0.1;
        filter = new Filter(SMOOTH_FACTOR);
    }


    /**
     * stop the running asyncTack and release space
     * */
    private void stopDetect() {
        Log.d("detect","stop");
        if(mAsyncTask!=null && mAsyncTask.getStatus()==AsyncTask.Status.RUNNING) {
            mSensorManager.unregisterListener(this);
            mAsyncTask.cancel(true);
        }else {
            return;
        }

        onDetect = false;
        filter = null;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float accel[] = sensorEvent.values;

            //First, Get filtered values
            double mag = Math.sqrt(accel[0] * accel[0] + accel[1] * accel[1] + accel[2] * accel[2]);
            double smoothed = filter.getSmoothedValue(mag);

            // Use blocking queue to reduce letency
            try {
                mBuffer.add(new Tag(System.currentTimeMillis(), smoothed));
            } catch (IllegalStateException e) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                ArrayBlockingQueue<Tag> newBuf = new ArrayBlockingQueue<>(
                        mBuffer.size() * 2);

                mBuffer.drainTo(newBuf);
                mBuffer = newBuf;
                mBuffer.add(new Tag(System.currentTimeMillis(), smoothed));
            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class OnSensorChangedTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            int blockSize = 0;
            FFT fft = new FFT(64);
            double[] accBlock = new double[64];
            double[] re = accBlock;
            double[] im = new double[64];

            double max;

            //parameters to set up sliding window
            Queue<Double> window = new LinkedList<>();
            int window_size = 6;
            double threshold = 0.5;
            int[] tagCnt = new int[4];

            while(true){
                try{
                    if(this.isCancelled()){
                        return null;
                    }
                    Tag cur = mBuffer.take();
                    accBlock[blockSize++] = cur.tag;

                    if(blockSize==64) {
                        blockSize = 0;
                        max = .0;
                        for (double val : accBlock) {
                            if (max < val) {
                                max = val;
                            }
                        }
                        fft.fft(re, im);

                        ArrayList<Double> featVect = new ArrayList<Double>();
                        for (int i = 0; i < re.length; i++) {
                            double mag = Math.sqrt(re[i] * re[i] + im[i]
                                    * im[i]);
                            featVect.add(mag);
                            im[i] = .0; // Clear the field
                        }
                        featVect.add(max);
                        Object[] array = (Object[]) featVect.toArray();
                        double rv = .0;
                        rv = WekaClassifier.classify(array);
                        Log.d("classifier",rv+"");

                        window.offer(rv);
                        tagCnt[(int)rv]++;

                        //determine the kind of activity of a fixed time
                        if(window.size()>window_size){
                            int tmp = (int) Math.floor(window.remove());
                            tagCnt[tmp]--;
                            if(tagCnt[0] > window_size*threshold || tagCnt[1] > window_size*threshold){
                                Log.d("detected","wthhhhhhhh");
                                shareEvent();
                                stopDetect();
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            stopDetect();
        }

        @Override
        protected void onCancelled() {
            return;
        }
    }

}
