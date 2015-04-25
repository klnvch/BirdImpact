package com.klnvch.birdimpact;

import com.klnvch.birdimpact.scores.ScoresActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		Button buttonSettings = (Button)findViewById(R.id.buttonSettings);
		buttonSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainMenuActivity.this, SettingsActivity.class);
				startActivity(i);
			}
		});
		
		Button buttonScores = (Button)findViewById(R.id.buttonScores);
		buttonScores.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainMenuActivity.this, ScoresActivity.class);
				startActivity(i);
			}
		});
		
		Button buttonNewGame = (Button)findViewById(R.id.buttonNewGame);
		buttonNewGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainMenuActivity.this, SelectionActivity.class);
				startActivity(i);
			}
		});
	}

}
