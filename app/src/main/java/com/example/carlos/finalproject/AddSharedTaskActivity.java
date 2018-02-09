package com.example.carlos.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import static com.example.carlos.finalproject.Constants.RESULT_CONFIRM;
import static com.example.carlos.finalproject.Constants.RESULT_DENY;

/**
 * Created by Carlos on 17/11/16.
 */

public class AddSharedTaskActivity extends Activity {

    private Button confirmButton;
    private Button denyButton;
    private TextView activityDescriptionTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Lifecycle", "On Create");
        setContentView(R.layout.activity_add_event);

        confirmButton= findViewById(R.id.confirmButton);
        denyButton = findViewById(R.id.denyButton);
        activityDescriptionTextView =  findViewById(R.id.shareActivityDes);

        Intent intent = getIntent();
        String actDes = intent.getStringExtra("activityDecription");

        if(actDes!=null) {
            Gson gson = new Gson();
            ActivityInfo act  = gson.fromJson(actDes,ActivityInfo.class);
            activityDescriptionTextView.setText("Activity: "+act.actName +'\n'+"Location: "+act.locName+'\n'+"Time: "+act.actTime);
            activityDescriptionTextView.setTextSize(20);
        }else{
            activityDescriptionTextView.setText("No Decription for the activity!");
        }


        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //go back to map activity
                Intent data = new Intent();
                setResult(RESULT_CONFIRM, data);  // the result is going to be passed to onActivityResult()
                // Actually finish; this closes the activity and restores the calling Activity
                finish();
            }
        });

        denyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //go back to map activity
                Intent data = new Intent();
                setResult(RESULT_DENY, data);  // the result is going to be passed to onActivityResult()
                // Actually finish; this closes the activity and restores the calling Activity
                finish();
            }
        });
    }

}
