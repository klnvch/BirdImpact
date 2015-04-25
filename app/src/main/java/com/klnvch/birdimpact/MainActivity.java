package com.klnvch.birdimpact;

import java.lang.ref.WeakReference;

import com.klnvch.birdimpact.scores.ScoresActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener, OnTouchListener{
	
	public static final String MESSAGE_COUNTER_VALUE = "COUNTER_VALUE";
	
	public static final int MESSAGE_FINISH_ACTIVITY = 1;
	public static final int MESSAGE_START_LOADING = 4;
	public static final int MESSAGE_START_COUNTING = 6;
	public static final int MESSAGE_START_PLAYING = 7;
	public static final int MESSAGE_START_PAUSING = 8;
	public static final int MESSAGE_UPDATE_COUNTER = 5;
	
	private GLSurfaceView glView = null;
	private MyRenderer renderer = null;
	
	private SensorManager mSensorManager = null;
    private Sensor mAccelerometer = null;
    private int rotation;
    
    private boolean isControl1 = true;
    
    private int showFPScounter = 0;
    
    private static class GameViewHandler extends Handler{
		private final WeakReference<MainActivity> mActivity;
		 
	    public GameViewHandler(MainActivity activity) {
	    	mActivity = new WeakReference<MainActivity>(activity);
	    }
	 
	    @Override
	    public void handleMessage(Message msg) {
	    	
	    	MainActivity activity = mActivity.get();
	    	
	    	ProgressBar progressBar = (ProgressBar)activity.findViewById(R.id.progressBar);
	    	View imgagePlay = (View)activity.findViewById(R.id.buttons_layout);
	    	ImageView imgagePause = (ImageView)activity.findViewById(R.id.imagePause);
	    	TextView textViewCounter = (TextView)activity.findViewById(R.id.textViewCounter);
	    	
	    	if (activity != null) {
	    		
	    		switch (msg.what) {
				case MESSAGE_FINISH_ACTIVITY:
					imgagePlay.setVisibility(View.GONE);
					imgagePause.setVisibility(View.GONE);
					textViewCounter.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
		    		
		   			// return to the main menu
		    		Intent i = new Intent(activity, ScoresActivity.class);
		            Bundle bundle = msg.getData();
		            i.putExtras(bundle);
		            activity.startActivity(i);
		            
		            // finish current activity
		            activity.finish();
					break;
				case MESSAGE_START_LOADING:
					imgagePlay.setVisibility(View.GONE);
					imgagePause.setVisibility(View.GONE);
					textViewCounter.setVisibility(View.GONE);
					progressBar.setVisibility(View.VISIBLE);
					break;
				case MESSAGE_START_COUNTING:
					imgagePlay.setVisibility(View.GONE);
					imgagePause.setVisibility(View.GONE);
					textViewCounter.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
					break;
				case MESSAGE_UPDATE_COUNTER:
					Bundle b = msg.getData();
					int counterValue = b.getInt(MESSAGE_COUNTER_VALUE);
					textViewCounter.setText(Integer.toString(counterValue));
					break;
				case MESSAGE_START_PLAYING:
					imgagePlay.setVisibility(View.GONE);
					imgagePause.setVisibility(View.VISIBLE);
					textViewCounter.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					break;
				case MESSAGE_START_PAUSING:
					imgagePlay.setVisibility(View.VISIBLE);
					imgagePause.setVisibility(View.GONE);
					textViewCounter.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					break;
				default:
					break;
				}
	    	}
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("dbg", "MainActivity onCreate");
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_play);
		
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    
	    Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	    rotation = display.getRotation();
		
	    DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    float density = dm.density;	// 1.0f = 160 dots per inch
		
	    glView = (GLSurfaceView)findViewById(R.id.glSurfaceView);
	   
	    glView.setEGLContextClientVersion(2);
	    renderer = new MyRenderer(this, density, getIntent().getExtras().getInt("Terrain.Type"), new GameViewHandler(this));
	    glView.setRenderer(renderer);

	    glView.requestFocus();
	    glView.setFocusableInTouchMode(true);
	    glView.setOnTouchListener(this);
	    
	    mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
	@Override
	protected void onPause() {
		Log.d("dbg", "MainActivity onPause");
		
		glView.onPause();
		mSensorManager.unregisterListener(this);
		
		renderer.releaseSoundControl();
		
		renderer.pause();
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.d("dbg", "MainActivity onResume");
		super.onResume();
		
		glView.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
		
		final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
		final boolean isVolumeOn = sharedPref.getBoolean(SettingsActivity.prefVolume, true);
		if(isVolumeOn){
			renderer.setSoundControl(new SoundControl(this));
		}
		isControl1 = sharedPref.getBoolean(SettingsActivity.prefControl, true);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("dbg", "MainActivity onSaveInstanceState " + (outState != null));
		if(outState != null){
			renderer.onSave(outState);
		}
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d("dbg", "MainActivity onRestoreInstanceState" + (savedInstanceState != null));
		if(savedInstanceState != null){
			renderer.onRestore(savedInstanceState);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onDestroy() {
		Log.d("dbg", "MainActivity onDestroy");
		super.onDestroy();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && isControl1){

			float y = 0;
			if(rotation == Surface.ROTATION_90){
				y = event.values[1];
			}else if(rotation == Surface.ROTATION_0){
				y = -event.values[0];
			}
			renderer.setAngleZ(y);
			
			//Log.d("dbg", x + " " + y + " " + z);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		float touched_x = event.getX();
		float touched_y = event.getY();
		final float width = v.getWidth();
		final float height = v.getHeight();
		
		float x = 2.0f * (touched_x/width - 0.5f);
		float y = 2.0f * (0.5f - touched_y/height);
		
		
		if(!isControl1){
			boolean toLeft = false;
			boolean toRight = false;
				
			final int touchNumber = event.getPointerCount();
		    final int action = event.getAction() & MotionEvent.ACTION_MASK;
			    
		    //Log.d("dbg", "n: " + touchNumber + ", event: " + action);
			    
			switch(action){
				//case MotionEvent.ACTION_HOVER_ENTER:
				//case MotionEvent.ACTION_HOVER_MOVE:
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
				case MotionEvent.ACTION_MOVE:
				//case MotionEvent.ACTION_SCROLL:
					//Log.d("dbg", "on");
					for (int i = 0; i!=touchNumber; ++i) {
				    	touched_x = event.getX(i);
				    	touched_y = event.getY(i);
				    	
				    	x = 2.0f * (touched_x/width - 0.5f);
						y = 2.0f * (0.5f - touched_y/height);
							
						if(x > 0){	
							toRight = true;
						}else if(x < 0){
							toLeft = true;
						}
				    }
					break;
				//case MotionEvent.ACTION_CANCEL:
				//case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				//case MotionEvent.ACTION_HOVER_EXIT:
					//Log.d("dbg", "off");
					for (int i = 0; i!=touchNumber; ++i) {
						if(i == event.getActionIndex())	continue;
						
					   	touched_x = event.getX(i);
					   	touched_y = event.getY(i);
					   	
					   	x = 2.0f * (touched_x/width - 0.5f);
						y = 2.0f * (0.5f - touched_y/height);
						
						if(x > 0){	
							toRight = true;
						}else if(x < 0){
							toLeft = true;
						}
					}
						
					break;
				default:
			}
				
			if(toLeft && !toRight){
				//Log.d("dbg", "left");
				Param.isLeftTouched = true;
				Param.isRightTouched = false;
			}else if(!toLeft && toRight){
				//Log.d("dbg", "right");
				Param.isLeftTouched = false;
				Param.isRightTouched = true;
			}else{
				//Log.d("dbg", "nothing");
				Param.isLeftTouched = false;
				Param.isRightTouched = false;
			}
				
		}
				
		int action = event.getAction();
		switch(action){
		   case MotionEvent.ACTION_DOWN:
			   break;
		   case MotionEvent.ACTION_MOVE:
			   break;
		   case MotionEvent.ACTION_UP:
		   case MotionEvent.ACTION_POINTER_UP:
				if(x>-0.1f && x<0.1f && y>0.8f){
					++showFPScounter;
					if(showFPScounter > 3)	Param.showFPS = true;
				}else{
					showFPScounter = 0;
					Param.showFPS = false;
				}
			   break;
		   case MotionEvent.ACTION_CANCEL:
			   break;
		   case MotionEvent.ACTION_OUTSIDE:
			   break;
		   default:
		   }
		return true;
	}
	
	public void onPlayClick(View v) {
		renderer.resume();
	}
	
	public void onPauseClick(View v) {
		renderer.pause();
	}
	
	public void onMainMenuClick(View v) {
		finish();
	}
	
	@Override
	public void onBackPressed() {
		renderer.pause();
	}
}
