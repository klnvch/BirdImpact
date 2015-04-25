package com.klnvch.birdimpact.counters;

import java.util.Locale;

import com.klnvch.birdimpact.Const;
import com.klnvch.birdimpact.entities.Restorable;

import android.content.SharedPreferences;
import android.os.Bundle;

public class ScoreCounter implements Restorable{
	private int score = 0;
	private int min = 0;
	private int minTotal = 0;
	private float sec = 0;
	private float secTotal = 0;
	private float timeTotal = 0;
	
	private int[] scoreHistory = new int[60];
	private int frameCountFPS = 0;
	private float fps = 0.0f;
	private int secPrev = 0;
	
	// temporary buffers
	private int[] resultT = new int[5];
	private int[] resultS = new int[6];
	private int[] resultFPS = new int[6];
	
	/**
	 * updates, called in the renderer onDraw 
	 * 
	 * @param elapsed time interval in seconds
	 * @return false if timer fired
	 */
	public boolean update(float interval){
		
		// do not update zero timer
		if(min <= 0 && (int)sec <= 0)	return true;
		
		// update game timer
		sec -= interval;
		if(sec < 0.0f){
			--min;
			sec += 60.0f;
		}
		
		// update total timer
		secTotal += interval;
		if(secTotal >= 60.0f){
			++minTotal;
			secTotal -= 60.0f;
		}
		
		// update total time
		timeTotal += interval;
		
		// update score history
		int index = (int)secTotal;
		if(index >= 0 && index <= 59)	scoreHistory[(int)secTotal] = score;
		//Log.d("rate", "write: " + Integer.toString((int)secTotal));
		
		// update FPS
		calculateFPS();
		
		// return false if timer fired
		return min < 0;
	}
	
	public void increaseScore(){
		// update score
		this.score += 1;
		if(score > 999999)	score = 999999;
		
		// update game timer
		sec += 2.0f;
		if(sec >= 60.0f){
			++min;
			sec -= 60.0f;
		}
		if(min > 99)	min = 99;
	}
	
	private int lastSpeedUp = 0;
	public boolean isTimeToIncreaseSpeed(){
		if(lastSpeedUp > 20)	return false;
		if((minTotal * 60 + (int)secTotal)/10 > lastSpeedUp){
			++lastSpeedUp;
			return true;
		}
		return false;
	}
	
	public void setTimer(int min, float sec){
		this.min = min;
		this.sec = sec;
	}
	
	public int getScore(){
		return score;
	}
	
	public String getTimerS(){
		if(sec > 9){
			return Integer.toString(min) + ":" + (int)sec;
		}else{
			return Integer.toString(min) + ":0" + (int)sec;
		}
	}
	
	public float getTotalTime(){
		return timeTotal;
	}
	
	public int getTimer(){
		return 60*min + (int)sec;
	}
	
	public int[] getTimerI(){
		
		resultT[0] = min/10;
		resultT[1] = min%10;
		resultT[2] = -1;
		resultT[3] = ((int)sec)/10;
		resultT[4] = ((int)sec)%10;
		
		return resultT;
	}
	
	public int[] getStopWatchI(){
		resultT[0] = minTotal/10;
		resultT[1] = minTotal%10;
		resultT[2] = -1;
		resultT[3] = ((int)secTotal)/10;
		resultT[4] = ((int)secTotal)%10;
		
		return resultT;
	}
	
	public String getStopWatchS(){
		return String.format(Locale.getDefault(), "%d:%02d", minTotal, (int)secTotal);
	}

	public int[] getScoreI(){

			int temp = score;
		
			for(int i=0; i!=6; ++i){
				resultS[5-i] = temp%10;
				temp /= 10;
			}
		
			return resultS;
	}
	
	public int[] getRateI(){
		int prevSec = (int)secTotal + 1;
		if(prevSec > 59)	prevSec = 0;
		int temp = score - scoreHistory[(int)prevSec];
		//Log.d("rate", "read: " + Integer.toString((int)prevSec));
		
		for(int i=0; i!=6; ++i){
			resultS[5-i] = temp%10;
			temp /= 10;
		}
		
		return resultS;
	}
	
	public int[] getFPSI(){
		int fpsI = (int)(fps*1000.0f);
		
		for(int i=0; i!=6; ++i){
			resultFPS[6-i-1] = fpsI % 10;
			fpsI /= 10;
		}
		
		return resultFPS;
	}
	
	private void calculateFPS(){
	    ++frameCountFPS;
	 
	    if(secPrev != ((int)secTotal)){
	        //  calculate the number of frames per second
	        fps = frameCountFPS;
	        //  Set time
	        secPrev = (int)secTotal;
	        //  Reset frame count
	        frameCountFPS = 0;
	    }
	}
	
	public void onSave(SharedPreferences.Editor editor){
		
	}

	@Override
	public void onSave(Bundle bundle) {

		bundle.putInt("Counter.score", score);
		bundle.putInt("Counter.min", min);
		bundle.putInt("Counter.minTotal", minTotal);
		bundle.putFloat("Counter.sec", sec);
		bundle.putFloat("Counter.secTotal", secTotal);
		bundle.putFloat("Counter.timeTotal", timeTotal);
		bundle.putInt("Counter.frameCountFPS", frameCountFPS);
		bundle.putFloat("Counter.fps", fps);
		bundle.putInt("Score.secPrev", secPrev);
		
		for(int i=0; i!=60; ++i){
			bundle.putInt("Counter.scoreHistory" + i, scoreHistory[i]);
		}	
	}

	@Override
	public void onRestore(Bundle bundle) {

		score = bundle.getInt("Counter.score", 0);
		min = bundle.getInt("Counter.min", Const.TIMER_INITIAL_MINUTES_MOUNTAINS);
		minTotal = bundle.getInt("Counter.minTotal", 0);
		sec = bundle.getFloat("Counter.sec", Const.TIMER_INITIAL_SECUNDES_MOUNTAINS);
		secTotal = bundle.getFloat("Counter.secTotal", 0);
		timeTotal = bundle.getFloat("Counter.timeTotal", 0);
		frameCountFPS = bundle.getInt("Counter.frameCountFPS", 0);
		fps = bundle.getFloat("Counter.fps", 0);
		secPrev = bundle.getInt("Counter.secPrev", 0);
		
		for(int i=0; i!=60; ++i){
			scoreHistory[i] = bundle.getInt("Counter.scoreHistory" + i, 0);
		}
	}
}
