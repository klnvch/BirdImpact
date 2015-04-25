package com.klnvch.birdimpact;

import com.klnvch.birdimpact.entities.Terrain;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class SelectionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selection);
		
		Button buttonMountains = (Button)findViewById(R.id.buttonMountains);
		buttonMountains.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelectionActivity.this, MainActivity.class);
				i.putExtra("Terrain.Type", Terrain.MOUNTAINS);
				startActivity(i);
				finish();
			}
		});
		
		Button buttonHills = (Button)findViewById(R.id.buttonHills);
		buttonHills.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelectionActivity.this, MainActivity.class);
				i.putExtra("Terrain.Type", Terrain.HILLS);
				startActivity(i);
				finish();
			}
		});
	}

}
