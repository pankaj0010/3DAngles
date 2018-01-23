package com.example.pankaj.compass;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private TextView text_azimuth;
    private TextView text_pitch;
    private TextView text_roll;
    private float geomagnetic[] = new float[3];
    private float gravity[] = new float[3];
    private float RMat[]= new float[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer  = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer , SensorManager.SENSOR_DELAY_GAME);

        text_azimuth=(TextView)findViewById(R.id.azimuth);
        text_pitch=(TextView)findViewById(R.id.pitch);
        text_roll=(TextView)findViewById(R.id.roll);
    }

    private void algo(float R[], float gravity[], float geomagnetic[]){
        float Ax = gravity[0];        float Ay = gravity[1];        float Az = gravity[2];
        final float Ex = geomagnetic[0];        final float Ey = geomagnetic[1];        final float Ez = geomagnetic[2];
        float Hx = Ey*Az - Ez*Ay;        float Hy = Ez*Ax - Ex*Az;        float Hz = Ex*Ay - Ey*Ax;
        final float normH = (float)Math.sqrt(Hx*Hx + Hy*Hy + Hz*Hz);
        final float invH = 1.0f / normH;
        Hx *= invH;        Hy *= invH;        Hz *= invH;
        final float invA = 1.0f / (float)Math.sqrt(Ax*Ax + Ay*Ay + Az*Az);
        Ax *= invA;        Ay *= invA;        Az *= invA;
        final float Mx = Ay*Hz - Az*Hy;        final float My = Az*Hx - Ax*Hz;        final float Mz = Ax*Hy - Ay*Hx;
        R[0] = Hx;     R[1] = Hy;     R[2] = Hz;
        R[3] = Mx;     R[4] = My;     R[5] = Mz;
        R[6] = Ax;     R[7] = Ay;     R[8] = Az;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor==mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))
            geomagnetic=event.values;
        if(event.sensor==mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
            gravity=event.values;

        algo(RMat,gravity,geomagnetic);
        int azimuth = (int)(Math.atan2(RMat[1], RMat[4])*(180/Math.PI));
        int pitch = (int)(Math.asin(-RMat[7])*(180/Math.PI));
        int roll = (int)(Math.atan2(-RMat[6],RMat[8])*(180/Math.PI));

        if(azimuth<0)   azimuth+=360;
        if(pitch<0)   pitch+=360;
        if(roll<0)   roll+=360;

        text_azimuth.setText("Azimuth: "+Integer.toString(azimuth));
        text_pitch.setText("Pitch: "+Integer.toString(pitch));
        text_roll.setText("Roll: "+Integer.toString(roll));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
