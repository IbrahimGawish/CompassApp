package com.gawish.app.compassapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.widget.RelativeLayout.*;


public class MainActivity extends ActionBarActivity implements SensorEventListener{

    private float compCurrentDegree=0f;
    private float qiblaCurrentDegree = 0f;
    Spinner lang ;
    private ImageView compImg,qiblaImg ;
    private SensorManager sensor;
    double latitude,longitude;
    double meccaLatitude = 21.422483;
    double meccaLongitude = 39.826181;
    float qiblaAngle;
    float compDegree,qiblaDegree;

    String testText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*lang= (Spinner)findViewById(R.id.lang);
        final String c = getBaseContext().getResources().getString(R.string.chose);
        final String ar = getBaseContext().getResources().getString(R.string.ar);
        final String en = getBaseContext().getResources().getString(R.string.en);
        final String fr = getBaseContext().getResources().getString(R.string.fr);
        List<String>values= new ArrayList<String>();
        values.add(c);
        values.add(ar);
        values.add(en);
        values.add(fr);

        ArrayAdapter<String>adapter;
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
                values);
        lang.setAdapter(adapter);

        lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String option = (String) parent.getItemAtPosition(position);
                //Toast.makeText(getBaseContext(),option,Toast.LENGTH_LONG).show();

                if (option == en) {


                    Locale locale = new Locale("en_US");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getApplicationContext().getResources().updateConfiguration(config, null);
                    Toast.makeText(getBaseContext(), option, Toast.LENGTH_LONG).show();

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                }
                if (option == ar) {
                    Locale locale = new Locale("ar");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, null);

                    //  Intent intent = getIntent();
                    //  finish();
                    // startActivity(intent);
                    // LinearLayout ll = (LinearLayout) findViewById(R.id.lay);
                    // ll.setGravity(Gravity.RIGHT);
                    Toast.makeText(getBaseContext(), option, Toast.LENGTH_LONG).show();
                }
                if (option == fr) {
                    Locale locale = new Locale("fr");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, null);

                    Toast.makeText(getBaseContext(), option, Toast.LENGTH_LONG).show();

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }


            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */
        compImg = (ImageView)findViewById(R.id.compassImg);
        qiblaImg =(ImageView)findViewById(R.id.qiblaImg);
        sensor = (SensorManager)getSystemService(SENSOR_SERVICE);

        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.getIsGPSTrackingEnabled()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            qiblaAngle = getQiblaAngle(latitude, longitude, meccaLatitude, meccaLongitude);


        }else {
            gpsTracker.showSettingsAlert();
           //Toast.makeText(this, "toot", Toast.LENGTH_LONG).show();
        }

    }



    @Override
    protected void onPause(){

        super.onPause();
        sensor.unregisterListener((SensorEventListener) this);
    }
    @Override
    protected void onResume(){
        super.onResume();
        sensor.registerListener((SensorEventListener) this, sensor.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

    }

//21.422483, 39.826181
    @Override
    public void onSensorChanged(SensorEvent event){

        compDegree = Math.round(event.values[0]);
        RotateAnimation compRotate = new RotateAnimation(compCurrentDegree ,-compDegree,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        compRotate.setDuration(210);
        compRotate.setFillAfter(true);
        compImg.startAnimation(compRotate);
        compCurrentDegree= -compDegree;

        qiblaDegree = Math.round(event.values[0])+qiblaAngle-90;
        RotateAnimation qiblaRotate = new RotateAnimation(qiblaCurrentDegree,-qiblaDegree,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        qiblaRotate.setDuration(210);
        qiblaRotate.setFillAfter(true);
        qiblaImg.startAnimation(qiblaRotate);
        qiblaCurrentDegree = -qiblaDegree;
        /*TextView textView = (TextView)findViewById(R.id.testText);
        testText = "your lat is "+latitude+" & your long is "+longitude +" &your angle is "+qiblaAngle+" &degree is "+ Math.round(event.values[0]) ;
        textView.setText(testText);
        */

    }

    public float getQiblaAngle(double lat1,double long1,double lat2,double long2){
        double angle,dy,dx;
        dy = lat2 - lat1;
        dx = Math.cos(Math.PI/ 180 * lat1) * (long2 - long1);
        angle = Math.atan2(dy, dx);
        angle = Math.toDegrees(angle);
        return (float)angle;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
