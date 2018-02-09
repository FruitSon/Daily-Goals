package com.example.carlos.finalproject;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by Carlos on 17/11/18.
 */

public class ReportActivity extends AppCompatActivity {

    private Map<String,String> dict;

    private List<ActivityInfo> planActivityList;
    private List<ActivityInfo> realActivityList;

    private PieChartView picChart;
    private ColumnChartView colChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        dict = new HashMap<>();
        dict.put("0","Apartment");
        dict.put("1","Restaurant");
        dict.put("2","Library");
        dict.put("3","Road");
        dict.put("4","Campus");

        planActivityList = new ArrayList<>();
        readPlanActivityDataFromDatabase();

        realActivityList=new ArrayList<>();
        readRealActivityDataFromDatabase();

        setUpPieChart();
        setUpColChart();
    }



    private void setUpPieChart(){
        picChart  =  findViewById(R.id.pieChart);
        List<SliceValue> values = new ArrayList<SliceValue>();
        Map<String,Integer> map=new HashMap<>();
        for(int i=0;i<realActivityList.size();i++){
            if(map.containsKey(realActivityList.get(i).locType)){
                int a = map.get(realActivityList.get(i).locType);
                map.put(realActivityList.get(i).locType, a+1);
            }else {
                map.put(realActivityList.get(i).locType, 1);
            }
        }

        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            SliceValue sliceValue = new SliceValue((float) entry.getValue(), ChartUtils.pickColor());
            sliceValue.setLabel(dict.get( entry.getKey() ));
            values.add(sliceValue);
            //System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        PieChartData data;
        data = new PieChartData(values);
        data.setHasLabels(true);
        data.setHasLabelsOnlyForSelected(false);
        data.setHasLabelsOutside(false);
        data.setHasCenterCircle(false);
        picChart.setPieChartData(data);

    }

    private void setUpColChart(){
        colChart =  findViewById(R.id.colChart);
        ColumnChartData data;


        List<AxisValue> xList =  new ArrayList<>();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for(int i=0;i<planActivityList.size();i++){
            //construct X-axis
            AxisValue tmp=  new AxisValue(i);
            tmp.setLabel(planActivityList.get(i).actTime);
            xList.add(tmp);

            //Construct the column
            values = new ArrayList<SubcolumnValue>();
            SubcolumnValue a = new SubcolumnValue((float) 5, ChartUtils.pickColor());
            a.setLabel(planActivityList.get(i).actName);

            int j=0;
            DateFormat format = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            Time timeA = new Time(System.currentTimeMillis());
            try {
                timeA = new java.sql.Time(format.parse(planActivityList.get(i).actTime).getTime());
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            for(;j<realActivityList.size();j++){
                try {
                    Time timeB=new Time(format.parse(realActivityList.get(j).actTime).getTime());;
                    long diffMinutes = (timeA.getTime()-timeB.getTime()) / (60 * 1000) % 60;
                    if(Math.abs(diffMinutes)<5)
                        break;
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }


            }
            SubcolumnValue b = new SubcolumnValue((float) 5, ChartUtils.pickColor());
            if(j>=0 && j<realActivityList.size()) {
                b.setLabel(dict.get(realActivityList.get(j).locType));
            }else{
                b.setLabel("None");
            }
            values.add(a);
            values.add(b);

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }


        data = new ColumnChartData(columns);

        // Set stacked flag.
        data.setStacked(true);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setValues(xList);
        if (true) {
            axisX.setName("Time");
            axisY.setName("Comparison");
        }
        data.setAxisXBottom(axisX);
        //data.setAxisYLeft(axisY);

        colChart.setColumnChartData(data);
    }



    //read activity data from database
    public void readPlanActivityDataFromDatabase() {
        planActivityList.clear();

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
                    planActivityList.add(act);
                }while (cursor2.moveToNext());
                cursor2.close();
            }
        } finally {
            cursor2.close();
            myDbHelper.close();
        }

        if (!planActivityList.isEmpty()) {
            Collections.sort(planActivityList, new Comparator<ActivityInfo>() {
                @Override
                public int compare(ActivityInfo object1,
                                   ActivityInfo object2) {

                    //return ((String) object1.get("text_content")).compareTo((String) object2.get("text_content"));
                    return object1.actTime.compareTo(object2.actTime);
                }
            });
        }

    }


    public void readRealActivityDataFromDatabase() {
        realActivityList.clear();
        DatabaseHelper myDbHelper = new DatabaseHelper(this);
        try {
            myDbHelper.createDataBase();

        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        myDbHelper.openDataBase();
        String query2 = "SELECT * FROM ActualActivity";
        Cursor cursor2 = myDbHelper.getWritableDatabase().rawQuery(query2, null);
        String message = "";

        //String id, String lng, String lat, String time, String locType
        try {
            if (cursor2.moveToFirst()) {
                do {
                    ActivityInfo act =  new ActivityInfo(cursor2.getString(0),cursor2.getString(1),cursor2.getString(2)
                            ,cursor2.getString(3),cursor2.getString(4));
                    realActivityList.add(act);
                }while (cursor2.moveToNext());
                cursor2.close();
            }
        } finally {
            cursor2.close();
            myDbHelper.close();
        }
    }
}
