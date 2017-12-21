package com.npu.carsecretary.msghandling;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.ContactsContract.CommonDataKinds.Event;

public class MyOritationListener implements SensorEventListener {

	private SensorManager mSensormanager;
	private Context mContxt;
	private Sensor mSensor;
	private float lastX;
	
	public MyOritationListener(Context context)
	{
		this.mContxt = context;
		
	}
	
	public void start()
	{
		mSensormanager = (SensorManager) mContxt.getSystemService(Context.SENSOR_SERVICE);
		if(mSensormanager!= null)
		{
			
			//huode fangxiang chuanganqi
			mSensor = mSensormanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		
		
		if(mSensor!= null)
		{
			mSensormanager.registerListener(this, mSensor,SensorManager.SENSOR_MAX);
		}
	}
	
	public void stop()
	{
		mSensormanager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.sensor.getType() == Sensor.TYPE_ORIENTATION )
		{
			float x = arg0.values[SensorManager.DATA_X];
			
			if(Math.abs(x-lastX) > 1.0)
			{
				if(mOnorientationListener != null)
				{
					mOnorientationListener.onOrientationChange(x);				}
			}
			
			lastX = x;
			
		}
	}
	
	private OnOrientationListener mOnorientationListener;
	
	
	public void setOnorientationListener(
			OnOrientationListener mOnorientationListener) {
		this.mOnorientationListener = mOnorientationListener;
	}


	public interface OnOrientationListener
	{
		void onOrientationChange(float x);
	}

}
