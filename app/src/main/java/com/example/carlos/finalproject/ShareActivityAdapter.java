package com.example.carlos.finalproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by Carlos on 17/11/16.
 */

public class ShareActivityAdapter extends ArrayAdapter<ActivityInfo> {
    private int resourceId;

    public ShareActivity delegate;

    public ShareActivityAdapter(Context context, int textViewResourceId,
                          List<ActivityInfo> objects) {
        super(context, textViewResourceId, objects);
        delegate= (ShareActivity) context;
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyListener myListener=null;
        myListener = new MyListener(position);

        ActivityInfo activityDes = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

        TextView actDes = view.findViewById(R.id.shareActivityInCell);
        Button shaButton = view.findViewById(R.id.shareButtonInCell);
        Button delButton = view.findViewById(R.id.delButttonInCell);

        actDes.setText("Activity: "+activityDes.actName +'\n'+"Location: "+activityDes.locName+'\n'+"Time: "+activityDes.actTime);
        shaButton.setOnClickListener(myListener);
        delButton.setOnClickListener(myListener);
//        delButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                deleteActivity();
//            }
//        });

        return view;
    }

    private void deleteActivity() {
        DatabaseHelper myDbHelper = new DatabaseHelper(getContext());
        try {
            myDbHelper.createDataBase();

        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        myDbHelper.openDataBase();
    }

    private class MyListener implements View.OnClickListener {
        int mPosition;
        public MyListener(int inPosition){
            mPosition = inPosition;
        }
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.delButttonInCell:
                    Log.d("DeleteActivityItem", "Click" + mPosition +" button");
                    delegate.deleteActivityAt(mPosition);
                    break;
                case R.id.shareButtonInCell:
                    Log.d("ShareActivityItem","Click " + mPosition +" button");
                    delegate.shareActivityTo(mPosition);
                    break;
            }
        }
    }
}
