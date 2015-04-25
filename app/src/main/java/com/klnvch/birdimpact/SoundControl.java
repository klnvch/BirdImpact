package com.klnvch.birdimpact;

import com.klnvch.birdimpact.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundControl{
	
	private SoundPool soundPool = null;
	
	//private static int soundId1 = -1;	// propeller
	//private static int soundId2 = -1;	// geese
	private int soundId3 = -1;	// collision
	private int soundId4 = -1;	// explosion
	//private static int streamId1 = -1;
	private int streamId3 = -1;
	private int streamId4 = -1;
	private boolean loaded = false;
	
	private MediaPlayer mp1 = null;	// too heavy
	private MediaPlayer mp2 = null;
	
	public SoundControl(Context context){
		
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		
		soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {			
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loaded = true;
			}
		});
		soundId3 = soundPool.load(context, R.raw.collision, 1);
		soundId4 = soundPool.load(context, R.raw.explosion, 1);
			
		mp1 = MediaPlayer.create(context, R.raw.propelleridle);			// Exception !!!
		if(mp1 != null){
			mp1.setLooping(true);
		}
				
		mp2 = MediaPlayer.create(context, R.raw.geese);
		if(mp2 != null){
			mp2.setLooping(true);
		}
		
	}
	
	public void startEngineAndBirdSounds() {
		if(mp1 != null){
			mp1.start();
		}
		
		if(mp2 != null){
			mp2.start();
		}
	}
	
	public void pause(){
		if(mp1 != null){
			mp1.pause();
		}
		
		if(mp2 != null){
			mp2.pause();
		}
	}
	
	public void playColision(){
		if(loaded && soundPool != null){
			soundPool.stop(streamId3);
			streamId3 = soundPool.play(soundId3, 1.0f, 1.0f, 1, 0, 1.0f);
		}
	}
	
	public void playExplosion(){
		// stop engine
		if(mp1 != null){
			mp1.pause();
		}
		// play explosion
		if(loaded && soundPool != null){
			soundPool.stop(streamId4);
			streamId4 = soundPool.play(soundId4, 1.0f, 1.0f, 1, 0, 1.0f);
		}
	}
	
	public void setGeeseVolume(float volume){
		if(soundPool!=null){
			if(mp2 != null){
				mp2.setVolume(volume, volume);
			}
		}
	}
	
	public void release(){
		
		if(soundPool != null){
			soundPool.release();
			soundPool = null;
		}
		
		if(mp1 != null){
			mp1.release();
			mp1 = null;
		}
		
		if(mp2 != null){
			mp2.release();
			mp2 = null;
		}
	}
}
