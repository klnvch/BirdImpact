package com.klnvch.birdimpact;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

public class SettingsActivity extends Activity {
	
	public static final String prefVolume = "Sound.isVolumeOn";
	public static final String prefVibration = "VibrationOn";
	public static final String prefControl = "isControl1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
		
		final Button buttonVolume = (Button)findViewById(R.id.buttonVolume);
		Drawable imgVolume = null;
		if(sharedPref.getBoolean(prefVolume, true)){
			imgVolume = getResources().getDrawable(R.drawable.volume_on);
		}else{
			imgVolume = getResources().getDrawable(R.drawable.volume_off);
		}
		buttonVolume.setCompoundDrawablesWithIntrinsicBounds(null, null, imgVolume, null);
		buttonVolume.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isVolumeOn = sharedPref.getBoolean(prefVolume, true);
				Drawable imgVolume = null;
				if(!isVolumeOn){
					imgVolume = getResources().getDrawable(R.drawable.volume_on);
				}else{
					imgVolume = getResources().getDrawable(R.drawable.volume_off);
				}
				buttonVolume.setCompoundDrawablesWithIntrinsicBounds(null, null, imgVolume, null);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean(prefVolume, !isVolumeOn);
				editor.commit();
			}
		});
		
		final Button buttonVibration = (Button)findViewById(R.id.buttonVibration);
		Drawable imgVibration = null;
		if(sharedPref.getBoolean(prefVibration, true)){
			imgVibration = getResources().getDrawable(R.drawable.vibrate_on);
		}else{
			imgVibration = getResources().getDrawable(R.drawable.vibrate_off);
		}
		buttonVibration.setCompoundDrawablesWithIntrinsicBounds(null, null, imgVibration, null);
		buttonVibration.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isVibrationOn = sharedPref.getBoolean(prefVibration, true);
				Drawable imgVibration = null;
				if(!isVibrationOn){
					imgVibration = getResources().getDrawable(R.drawable.vibrate_on);
				}else{
					imgVibration = getResources().getDrawable(R.drawable.vibrate_off);
				}
				buttonVibration.setCompoundDrawablesWithIntrinsicBounds(null, null, imgVibration, null);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean(prefVibration, !isVibrationOn);
				editor.commit();
			}
		});
		//*****************************************************************************************************
		final Button buttonControl = (Button)findViewById(R.id.buttonControl);
		Drawable imgControl = null;
		if(sharedPref.getBoolean(prefControl, true)){
			imgControl = getResources().getDrawable(R.drawable.control2);
		}else{
			imgControl = getResources().getDrawable(R.drawable.control3);
		}
		buttonControl.setCompoundDrawablesWithIntrinsicBounds(null, null, imgControl, null);
		buttonControl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isControl1 = sharedPref.getBoolean(prefControl, true);
				Drawable imgControl = null;
				if(!isControl1){
					imgControl = getResources().getDrawable(R.drawable.control2);
				}else{
					imgControl = getResources().getDrawable(R.drawable.control3);
				}
				buttonControl.setCompoundDrawablesWithIntrinsicBounds(null, null, imgControl, null);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean(prefControl, !isControl1);
				editor.commit();
			}
		});
	}
}
