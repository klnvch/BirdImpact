package com.klnvch.birdimpact.counters;

import com.klnvch.birdimpact.Const;
import com.klnvch.birdimpact.MainActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class StartCounter {
	
	private static final long GAP = 700000000;	// 1 seconds
	private static final int N = 3;				// number of gaps
	
	private long time;
	private long lastTime;
	private int timerValue;
	private boolean isCounting;
	
	private final Handler handler;
	
	public StartCounter(Handler handler) {
		this.handler = handler;
	}
	
	public void start(){
		time = GAP * N;	// 3 seconds
		timerValue = N + 1;
		lastTime = System.nanoTime();
		isCounting = true;
		
		handler.sendEmptyMessage(MainActivity.MESSAGE_START_COUNTING);
	}
	
	public boolean isCounting(){
		return isCounting;
	}
	
	public boolean update(){
		
		if(time < 0){
			isCounting = false;
			return true;
		}
		
		// update time
		long currentTime = System.nanoTime();
		if(Const.DEBUG){
			time -= Const.MIN_TIME_INTERVAL;
		}else{
			time -= (currentTime - lastTime);
		}
		lastTime = currentTime;
		
		//
		int newTimerValue = (int)((double)time/GAP) + 1;
		if(newTimerValue != timerValue){
			timerValue = newTimerValue;
			Message message = handler.obtainMessage(MainActivity.MESSAGE_UPDATE_COUNTER);
			Bundle b = new Bundle();
			b.putInt(MainActivity.MESSAGE_COUNTER_VALUE, newTimerValue);
			message.setData(b);
			handler.sendMessage(message);
		}
		
		return false;
	}
}
