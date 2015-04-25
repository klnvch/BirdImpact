package com.klnvch.birdimpact.scores;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.entities.Terrain;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class ScoresActivity extends Activity {
	
	private static final String filename = "scores.dat";
	
	private ArrayList<HighScore> scores = null;
	private HighScore bestScore = null;
	
	private class HighScoreComparator implements Comparator<HighScore> {
	    @Override
	    public int compare(HighScore hs1, HighScore hs2) {
	        return hs2.killedBirds - hs1.killedBirds;
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_scores);
		
		try{
		    FileInputStream fis = openFileInput(filename);
		    ObjectInputStream ois = new ObjectInputStream(fis);
		    HighScore[] temp = (HighScore[])ois.readObject();
		    scores = new ArrayList<HighScore>(Arrays.asList(temp));
		    ois.close();
		    
		}catch(Exception e){
			//Log.d("dbg", e.getMessage());
		}
		
		if(scores == null){
			scores = new ArrayList<HighScore>();
		}

		Bundle bundle = getIntent().getExtras();
		
		if(bundle != null){
			bestScore = (HighScore)bundle.getSerializable(HighScore.class.getName());
			if(!scores.contains(bestScore)){
				scores.add(bestScore);
			}
		}
		
		Collections.sort(scores, new HighScoreComparator());
		
		updateRows();
	}
	
	private void updateRows(){
		
		Resources r = getResources();
		float fontSize = r.getDimension(R.dimen.scores_text_size);
		
		TableLayout tl = (TableLayout)findViewById(R.id.TableLayout);
		
		tl.removeAllViews();
		
		int index = 0;
		for (HighScore highScore : scores) {

			++index;
			
			int color = Color.GREEN;
			if(bestScore != null && highScore.equals(bestScore)){
				color = Color.RED;
			}
			
			TableRow tr = new TableRow(this);
			
			TextView t0 = new TextView(this);
			t0.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT ,LayoutParams.WRAP_CONTENT, 2.0f/26.0f));
			t0.setWidth(0);
			t0.setText(Integer.toString(index) + ".");
			t0.setTextColor(color);
			t0.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
			tr.addView(t0);
			
			TextView t1 = new TextView(this);
			t1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 7.0f/26.0f));
			t1.setWidth(0);
			t1.setText(highScore.name);
			t1.setTextColor(color);
			t1.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
			tr.addView(t1);
			
			TextView t2 = new TextView(this);
			t2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 5.0f/26.0f));
			t2.setWidth(0);
			t2.setText(Integer.toString(highScore.killedBirds));
			t2.setTextColor(color);
			t2.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
			tr.addView(t2);
			
			TextView t3 = new TextView(this);
			t3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 5.0f/26.0f));
			t3.setWidth(0);
			t3.setText(highScore.timeSpent);
			t3.setTextColor(color);
			t3.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
			tr.addView(t3);
			
			TextView t4 = new TextView(this);
			t4.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 7.0f/26.0f));
			t4.setWidth(0);
			if(highScore.terrainType == Terrain.MOUNTAINS){
				t4.setText(R.string.selection_mountains);
			}else if(highScore.terrainType == Terrain.HILLS){
				t4.setText(R.string.selection_hills);
			}else{
				t4.setText("");
			}
			t4.setTextColor(color);
			t4.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
			tr.addView(t4);
			
			tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}
	
	@Override
	protected void onDestroy() {
		
		try{
			FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(scores.toArray(new HighScore[scores.size()]));
			oos.close();
		}catch(Exception e){
			//Log.d("dbg", e.getMessage());
		}
		
		
		super.onDestroy();
	}
}
