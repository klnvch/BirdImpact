package com.klnvch.birdimpact;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.klnvch.birdimpact.counters.ScoreCounter;
import com.klnvch.birdimpact.counters.StartCounter;
import com.klnvch.birdimpact.entities.Flock;
import com.klnvch.birdimpact.entities.Explosion;
import com.klnvch.birdimpact.entities.Restorable;
import com.klnvch.birdimpact.entities.Terrain;
import com.klnvch.birdimpact.entities.TerrainHills;
import com.klnvch.birdimpact.entities.TerrainMountains;
import com.klnvch.birdimpact.entities.Meet;
import com.klnvch.birdimpact.entities.ParticalSystem;
import com.klnvch.birdimpact.entities.Plane;
import com.klnvch.birdimpact.entities.Propeller;
import com.klnvch.birdimpact.entities.Radar;
import com.klnvch.birdimpact.entities.SkyDome;
import com.klnvch.birdimpact.entities.TargetsSystem;
import com.klnvch.birdimpact.entities.TextDigits;
import com.klnvch.birdimpact.entities.TextSpeedUp;
import com.klnvch.birdimpact.entities.TextTimer;
import com.klnvch.birdimpact.scores.HighScore;
import com.klnvch.birdimpact.utils.MatrixHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

public class MyRenderer implements GLSurfaceView.Renderer, Restorable{
	// entities
	private Plane			plane			= null;
	private Propeller		propeller		= null;
	private SkyDome			skydome			= null;
	private Terrain			terrain			= null;
	//private Bird			bird			= null;
	private Flock		flock			= null;
	private ParticalSystem	particalSystem	= null;
	private Meet			meet			= null;
	private TextTimer		textTimer		= null;
	private TextDigits		textDigits		= null;
	private Radar			radar			= null;
	private TargetsSystem	targetsSystem	= null;
	private Explosion		explosion		= null;
	private TextSpeedUp		textSpeedUp		= null;
	
	private ScoreCounter scoreCounter = null;
	private SoundControl soundControl = null;
	   
	private float angleY = 0.0f;		// turn around Y
	private float worldAngleZ = 0.0f;		// turn around Z
	private float angleZ = 0.0f;
	private float newAngleZ = 0.0f;
	private float planeAngleZ = 0.0f;
	private boolean isCollisionPlaneLand = false;
	
	private Context context;
	private float displayDensity;
	private int terrainType;
	private Handler handler;
	private boolean isVibrationOn;
	private boolean isControl1;
	
	//
	private boolean isOpenGLDataLoaded = false;
	private boolean isGamePlaying = false;
	private boolean isGamePaused = false;
	private boolean isFinished = false;
	private final StartCounter startCounter;
    
	private float[] mPerspectiveMatrix = new float[16];
	private float[] mLookAtMatrix = new float[16];
	private float[] mCameraMatrix = new float[16];
	private float[] mFinalMatrix = new float[16];
	private float[] mRotMatrix = new float[16];	// rotation x
	private float[] mRotYMatrix = new float[16];	// rotation y
	private float[] mRotZMatrix = new float[16];	// rotation z
	private float[] mRotYZMatrix = new float[16];	// rotation YZ
	private float[] mRotCameraYZMatrix = new float[16];	// rotation YZ
	private float[] mTranslateMatrix = new float[16];
	private float[] mScaleMatrix = new float[16];
    
    private long globalPrevTime;
    
    // temporary buffer
	private float[] pos = new float[4];
	
	// 
	private Bundle bundle = null;
	
	// Constructor
	public MyRenderer(Context context, float density, int terrainType, Handler handler) {
		Log.d("dbg", "MyRenderer constructor");
		this.context = context;
		this.displayDensity = density;
		this.terrainType = terrainType;
		this.handler = handler;
		
		startCounter = new StartCounter(handler);

		// always should be restored
		SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
		isVibrationOn = sharedPref.getBoolean(SettingsActivity.prefVibration, true);
		isControl1 = sharedPref.getBoolean(SettingsActivity.prefControl, true);
	}
	
	public void setSoundControl(SoundControl soundControl){
		this.soundControl = soundControl;
	}
	
	public void releaseSoundControl(){
		if(soundControl != null){
			soundControl.release();
			soundControl = null;
		}
	}
	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		Log.d("dbg", "MyRenderer onSurfaceCreated");
		GLES20.glClearDepthf(1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);

		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		//
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		// set the view matrix
		Matrix.setLookAtM(mLookAtMatrix, 0, 0, 5.0f, 30.0f, 0.0f, 5.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		
		// load everything
		isOpenGLDataLoaded = false;
		
		handler.sendEmptyMessage(MainActivity.MESSAGE_START_LOADING);
	
		loadOpenGLData();
			
		isOpenGLDataLoaded = true;
   		
		if(!isGamePaused){
			startCounter.start();
		}else{
			handler.sendEmptyMessage(MainActivity.MESSAGE_START_PAUSING);
		}
	}
	
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		Log.d("dbg", "MyRenderer onSurfaceChanged");
		GLES20.glViewport(0, 0, width, height);
		float aspect = (float) width / height;
		MatrixHelper.perspectiveM(mPerspectiveMatrix, Const.FOVY, aspect, Const.ZNEAR, Const.ZFAR);
		
		Matrix.multiplyMM(mCameraMatrix, 0, mPerspectiveMatrix, 0, mLookAtMatrix, 0);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {

		if(isOpenGLDataLoaded){
			
			GLES20.glClearColor(Const.BACKGROUND_COLOR_RED, Const.BACKGROUND_COLOR_GREEN, Const.BACKGROUND_COLOR_BLUE, Const.BACKGROUND_COLOR_ALPHA);
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		
			long globalCurrTime = System.nanoTime();
			final float globalTimeInterval;
			if(isGamePlaying){
				globalTimeInterval = /*0.02f;/*/(globalCurrTime - globalPrevTime)/1000000000.0f;
			}else{
				globalTimeInterval = 0.0f;
			} 
		
			//************************************************************************************
			// set rotation matrixes
			//************************************************************************************
			Matrix.setRotateM(mRotZMatrix, 0, worldAngleZ, 0.0f, 0.0f, 1.0f);
			Matrix.setRotateM(mRotYMatrix, 0, angleY, 0.0f, 1.0f, 0.0f);
			Matrix.multiplyMM(mRotYZMatrix, 0, mRotZMatrix, 0, mRotYMatrix, 0);
			Matrix.multiplyMM(mRotCameraYZMatrix, 0, mCameraMatrix, 0, mRotYZMatrix, 0);
			//*************************************************************************************
			// draw particles
			//*************************************************************************************
			particalSystem.draw(mRotCameraYZMatrix, scoreCounter.getTotalTime());
			//*************************************************************************************
			// draw meet
			//*************************************************************************************
			for(int i=0; i!=Meet.N; ++i){
				Matrix.setRotateEulerM(mRotMatrix, 0, meet.a[i], meet.a[i], meet.a[i]);
				Matrix.setIdentityM(mTranslateMatrix, 0);
				Matrix.translateM(mTranslateMatrix, 0, meet.x[i], meet.y[i], meet.z[i]);
				
				Matrix.multiplyMM(mFinalMatrix, 0, mTranslateMatrix, 0, mRotMatrix, 0);
				Matrix.multiplyMM(mFinalMatrix, 0, mRotCameraYZMatrix, 0, mFinalMatrix, 0);
				
				meet.draw(i, mFinalMatrix);
			}
			//*************************************************************************************
			// draw plane
			//*************************************************************************************
			Matrix.setRotateM(mRotMatrix, 0, planeAngleZ, 0.0f, 0.0f, 1.0f);
			Matrix.multiplyMM(mFinalMatrix, 0, mCameraMatrix, 0, mRotMatrix, 0);
			plane.draw(mFinalMatrix);
			//*************************************************************************************
			// draw birds
			//*************************************************************************************
			for(int i=0; i!=flock.boids.length; ++i){
			
				if(!flock.boids[i].isAlive)	continue;
			
				Matrix.setRotateM(mRotMatrix, 0, -flock.boids[i].angle, 0.0f, 1.0f, 0.0f);
				Matrix.setIdentityM(mTranslateMatrix, 0);
				Matrix.translateM(mTranslateMatrix, 0, flock.boids[i].pX, 0.0f, flock.boids[i].pZ);
				Matrix.setIdentityM(mScaleMatrix, 0);
				Matrix.scaleM(mScaleMatrix, 0, 2.0f, 2.0f, 2.0f);
				
				Matrix.multiplyMM(mFinalMatrix, 0, mRotMatrix, 0, mScaleMatrix, 0);
				Matrix.multiplyMM(mFinalMatrix, 0, mTranslateMatrix, 0, mFinalMatrix, 0);
				Matrix.multiplyMM(mFinalMatrix, 0, mRotCameraYZMatrix, 0, mFinalMatrix, 0);
				
				flock.draw(mFinalMatrix, i);
			}
			//*************************************************************************************
			// draw sky dome
			//*************************************************************************************
			skydome.draw(mRotCameraYZMatrix);
			//*************************************************************************************
			// draw Landscape4
			//*************************************************************************************
			terrain.checkVisibility(angleY);
			terrain.preDraw();
			if(terrain instanceof TerrainMountains){
				for(int k=0; k!=terrain.N; ++k){
					// draw top
					for(int l=0; l!=(k+1)*2; ++l){
						int i = terrain.N - 1 - k;
						int j = terrain.N - 1 - k + l;
						drawTerrain(i, j);					
					}
					// draw bottom
					for(int l=0; l!=(k+1)*2; ++l){
						int i = terrain.N + k;
						int j = terrain.N - 1 - k + l;
						drawTerrain(i, j);					
					}
					// draw left
					for(int l=0; l!=k*2; ++l){
						int i = terrain.N - k + l;
						int j = terrain.N - 1 - k;
						drawTerrain(i, j);
					}
					// draw right
					for(int l=0; l!=k*2; ++l){
						int i = terrain.N - k + l;
						int j = terrain.N + k;
						drawTerrain(i, j);
					}
				}
			}else{
				for(int i=0; i!=terrain.N; ++i){			// Landscape4
					for(int j=0; j!=terrain.N; ++j){		// Landscape4
						
						Matrix.setIdentityM(mTranslateMatrix, 0);
						float dx = (-1.0f + j)*terrain.SIZE;
						float dz = (-1.0f + i)*terrain.SIZE;
						Matrix.translateM(mTranslateMatrix, 0, (terrain.x + dx), Const.HORIZONT_Y, (terrain.z + dz));
						Matrix.setIdentityM(mScaleMatrix, 0);
						Matrix.scaleM(mScaleMatrix, 0, 44.0f, 11.0f, 44.0f);
					
						Matrix.multiplyMM(mFinalMatrix, 0, mTranslateMatrix, 0, mScaleMatrix, 0);
						Matrix.multiplyMM(mFinalMatrix, 0, mRotCameraYZMatrix, 0, mFinalMatrix, 0);
					
						terrain.draw(mFinalMatrix, i, j);
					}
				}
			}
			terrain.postDraw();
			//*************************************************************************************
			// draw propeller
			//*************************************************************************************
			propeller.draw(mCameraMatrix);
			//*************************************************************************************
			//	draw explosion
			//*************************************************************************************
			Matrix.setRotateM(mRotMatrix, 0, planeAngleZ, 0.0f, 0.0f, 1.0f);
			Matrix.multiplyMM(mFinalMatrix, 0, mCameraMatrix, 0, mRotMatrix, 0);
			explosion.draw(mFinalMatrix);
			//*************************************************************************************
			// draw text timer, score, FPS, rate
			//*************************************************************************************
			textTimer.draw(scoreCounter.getTimerI());
			textDigits.draw(scoreCounter);
			textSpeedUp.draw();
			//*************************************************************************************
			// draw radar
			//*************************************************************************************
			Matrix.setIdentityM(mTranslateMatrix, 0);
			Matrix.translateM(mTranslateMatrix, 0, -0.75f, 0.75f, 0.0f);
			Matrix.setRotateM(mRotMatrix, 0, radar.angle, 0.0f, 0.0f, 1.0f);
			
			Matrix.multiplyMM(mFinalMatrix, 0, mTranslateMatrix, 0, mRotMatrix, 0);
			
			radar.draw(mFinalMatrix);
			//*************************************************************************************
			// draw targets on the radar
			//*************************************************************************************
			Matrix.setIdentityM(mTranslateMatrix, 0);
			Matrix.translateM(mTranslateMatrix, 0, -0.75f, 0.75f, 0.0f);
			Matrix.setRotateM(mRotMatrix, 0, angleY, 0.0f, 0.0f, 1.0f);
			
			Matrix.multiplyMM(mFinalMatrix, 0, mTranslateMatrix, 0, mRotMatrix, 0);
			
			targetsSystem.draw(mFinalMatrix);
			//*************************************************************************************
			// update positions
			//*************************************************************************************
			//Plane.speed = 0;
			
			
			if(isControl1){
				angleZ = newAngleZ;
				worldAngleZ = angleZ;
			}else{
				final float dAngle = Const.TURN_ACCELERATION * globalTimeInterval;
				if(Param.isLeftTouched){		// turn left, go to minimum
					if(newAngleZ > Const.MIN_PLANE_ANGLE)	newAngleZ -= dAngle;
				}else if(Param.isRightTouched){	// turn right, go to maximum
					if(newAngleZ < Const.MAX_PLANE_ANGLE)	newAngleZ += dAngle;
				}else{							// do nothing, go to zero
					if(newAngleZ > dAngle)			newAngleZ -= dAngle;
					else if(newAngleZ < -dAngle)	newAngleZ += dAngle;
				}
				
				angleZ = newAngleZ;
				planeAngleZ = -angleZ;
			}
			angleY  += angleZ * globalTimeInterval * Plane.turn_speed;
			//angleY = 0; //for testing
	    
			//final float minDistance = bird.update(angleY, globalTimeInterval, scoreCounter.getTimer());
			final float minDistance = flock.update(angleY, globalTimeInterval, Plane.speed);
			if(soundControl != null && isGamePlaying){
				soundControl.setGeeseVolume(1.0f - minDistance/10000.0f);
			}
			terrain.update(angleY, globalTimeInterval);
			propeller.update(globalTimeInterval);
			radar.update(globalTimeInterval);
			meet.update(globalTimeInterval);
			plane.update(globalTimeInterval);
			particalSystem.update(globalTimeInterval, scoreCounter.getTotalTime(), angleY);
			explosion.update(globalTimeInterval);
			textSpeedUp.update(globalTimeInterval);
			
			if(startCounter.isCounting()){
				if(startCounter.update()){
					isGamePlaying = true;
					handler.sendEmptyMessage(MainActivity.MESSAGE_START_PLAYING);
					
					if(soundControl != null){
						soundControl.startEngineAndBirdSounds();
					}
					
					globalPrevTime = System.nanoTime();
				}
			}
	    
			//	update target system
			for(int i=0; i!=flock.boids.length; ++i){
				if(flock.boids[i].isAlive){
					targetsSystem.set(i, flock.boids[i].pX, -flock.boids[i].pZ);
				}else{
					targetsSystem.set(i, 1000.0f, 1000.0f);
				}
			}
			
			// detect collision
			Matrix.setRotateM(mRotZMatrix, 0, angleZ, 0.0f, 0.0f, 1.0f);
			Matrix.setRotateM(mRotYMatrix, 0, angleY, 0.0f, 1.0f, 0.0f);
			Matrix.multiplyMM(mRotYZMatrix, 0, mRotZMatrix, 0, mRotYMatrix, 0);
			
			for(int i=0; i!=flock.boids.length; ++i){
				if(flock.boids[i].isAlive && flock.boids[i].distance < 400 && detectBirdCollision(flock.boids[i].pX, 0, flock.boids[i].pZ)){
					flock.killBird(i);
					scoreCounter.increaseScore();
				}
			}
			
			// detect collision with land
			if(!isCollisionPlaneLand){
				detectLandCollision();
			}

			// update timer
			if(scoreCounter.update(globalTimeInterval)){
				
				// timer finished, so finish game and change activity
				Message msg = handler.obtainMessage();
				msg.what = MainActivity.MESSAGE_FINISH_ACTIVITY;
				Bundle bundle = new Bundle();
				bundle.putString(HighScore.KEY_NAME, new Date().toString());
				bundle.putInt(HighScore.KEY_KILLED_BIRDS, scoreCounter.getScore());
				bundle.putString(HighScore.KEY_TIME_SPENT, scoreCounter.getStopWatchS());
				bundle.putInt(HighScore.KEY_TERRAIN_TYPE, terrainType);
				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
				HighScore highscore = new HighScore(df.format(Calendar.getInstance().getTime()), scoreCounter.getScore(), scoreCounter.getStopWatchS(), terrainType);
				bundle.putSerializable(HighScore.class.getName(), highscore);
				msg.setData(bundle);
				handler.sendMessage(msg);
				
				isOpenGLDataLoaded = false;
				isFinished = true;
			}
			
			// increase speed
			if(scoreCounter.isTimeToIncreaseSpeed()){
				textSpeedUp.start();
				//Plane.speed *= 1.1f;
				//Plane.turn_speed *= 1.1f;
				//Bird.speed *= 1.1f;
			}

	      
			globalPrevTime = globalCurrTime;
			
		}
	}
	
	private void drawTerrain(int i, int j){
		if(!terrain.isVisible(i, j))	return;
		
		Matrix.setIdentityM(mTranslateMatrix, 0);
		final float dx = (0.5f - terrain.N + j)*terrain.SIZE;
		final float dz = (0.5f - terrain.N + i)*terrain.SIZE;
		Matrix.translateM(mTranslateMatrix, 0, (terrain.x + dx), Const.HORIZONT_Y, (terrain.z + dz));
		Matrix.setIdentityM(mScaleMatrix, 0);
		Matrix.scaleM(mScaleMatrix, 0, terrain.SIZE/*-2.0f*/, TerrainMountains.HEIGHT, terrain.SIZE/*-2.0f*/);
	
		Matrix.multiplyMM(mFinalMatrix, 0, mTranslateMatrix, 0, mScaleMatrix, 0);
		Matrix.multiplyMM(mFinalMatrix, 0, mRotCameraYZMatrix, 0, mFinalMatrix, 0);
	
		terrain.draw(mFinalMatrix, i, j);
	}
	
	private void startPlaneBirdCollision(float[] explosionPosition, int type){
		plane.setExplosion(explosionPosition);
		
		particalSystem.setExplosion(explosionPosition, type);
		
		if(type == Const.PROPELLER_TYPE){
			propeller.hit();
		}else if(type == Const.WINGS_TYPE){
			deviate(explosionPosition[0]);
			if(soundControl != null){
				soundControl.playColision();
			}
			meet.setExplosion(explosionPosition, angleY);
		}
	}
	
	private void startPlaneLandCollision(){
		if(soundControl != null){
			soundControl.playExplosion();
		}
		explosion.startExplosion();
		Plane.speed = 0.0f;
		Plane.turn_speed = 0.0f;
		scoreCounter.setTimer(0, 3);
		isCollisionPlaneLand = true;
	}
	
	private void deviate(float dx){
		if(isVibrationOn){
			Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(50);
		}
		
		angleY += dx * 0.2f;
	}
	
	private boolean detectBirdCollision(float birdX, float birdY, float birdZ){
		
		pos[0] = birdX;
		pos[1] = -0.25f;
		pos[2] = birdZ;
		pos[3] = 1.0f;
		
	    Matrix.multiplyMV(pos, 0, mRotYZMatrix, 0, pos, 0);
	    	
	    // detect collision with propeller
	    if(pos[2] > Propeller.Z0 && pos[2] < 11.5f){											// Z axis
	    	if(pos[0] >= -(Propeller.RADIUS + 0.15f) && pos[0] <= (Propeller.RADIUS + 0.15f)){	// X axis, 0.15f for bird size
	    		startPlaneBirdCollision(pos, Const.PROPELLER_TYPE);
	    		return true;
	    	}
	    }
	    
	    // detect collision with wings
	    if(pos[2] < 3.75f && pos[2] > 0.01f){		// z, how far
	    	// middle wing
	    	if(pos[0] >= -6.4f && pos[0] <= 6.4f){		// 0.15f for bird size
	    		if(pos[1] > -0.7f && pos[1] < 0.2f){	// 0.2f for bird size
	    			startPlaneBirdCollision(pos, Const.WINGS_TYPE);
	    			return true;
	    		}
	    	}
	    	
	    	// left wing
	    	if(pos[0] > -11.5f && pos[0] < -6.25f){			// 0.15f for bird size
	    		float ymax = -(pos[0] + 6.25f) / 3.75f + 0.2f;	// + 0.2f for bird size
	    		float ymin = -(pos[0] + 6.25f) / 3.0f - 0.7f;	// - 0.2f for bird size
	    		if(pos[1] < ymax && pos[1] > ymin){
	    			startPlaneBirdCollision(pos, Const.WINGS_TYPE);
	    			return true;
	    		}
	    	}
	    	
	    	// right wing
	    	if(pos[0] > 6.25f && pos[0] < 11.5f){			// 0.15f for bird size
	    		float ymax = (pos[0] - 6.25f) / 3.75f + 0.2f;	// + 0.2f for bird size
	    		float ymin = (pos[0] - 6.25f) / 3.0f - 0.7f;	// - 0.2f for bird size
	    		if(pos[1] < ymax && pos[1] > ymin){
	    			startPlaneBirdCollision(pos, Const.WINGS_TYPE);
	    			return true;
	    		}
	    		
	    	}
	    }
	    return false;
	}
	
	private boolean detectLandCollision(){
		
		// detect collisions between land and front part of the plane
		pos[0] = Plane.FRONT_X;
		pos[1] = Plane.FRONT_Y;
		pos[2] = Plane.FRONT_Z;
		pos[3] = 1.0f;
		
		Matrix.multiplyMV(pos, 0, mRotYZMatrix, 0, pos, 0);
		
		float heightCenter = terrain.getHeight(pos[0], pos[2]);
		
		if(heightCenter+Const.HORIZONT_Y > pos[1]){
			startPlaneLandCollision();
		}
		
		
		// detect collision between land and left wing
		pos[0] = -Plane.WING_X;
		pos[1] = Plane.WING_Y;
		pos[2] = Plane.WING_Z;
		pos[3] = 1.0f;
		
		Matrix.multiplyMV(pos, 0, mRotYZMatrix, 0, pos, 0);
		
		float heightLeft = terrain.getHeight(pos[0], pos[2]);
		
		if(heightLeft+Const.HORIZONT_Y > pos[1]){
			startPlaneLandCollision();
		}
		
		// detect collision between land and right wing
		pos[0] = Plane.WING_X;
		pos[1] = Plane.WING_Y;
		pos[2] = Plane.WING_Z;
		pos[3] = 1.0f;
				
		Matrix.multiplyMV(pos, 0, mRotYZMatrix, 0, pos, 0);
			
		float heightRight = terrain.getHeight(pos[0], pos[2]);
		
		if(heightRight+Const.HORIZONT_Y > pos[1]){
			startPlaneLandCollision();
		}
		
		return false;
	}
	
	private void loadOpenGLData(){
		
		if(terrain == null){
			if(terrainType == Terrain.HILLS){
				terrain = new TerrainHills(context);
			}else{
				terrain = new TerrainMountains(context);
			}
		}else	terrain.load(context);
			
		if(skydome == null)		skydome = new SkyDome(context);
		else					skydome.load(context);
			
		if(plane == null)		plane = new Plane(context);
		else					plane.load(context);
		
		if(explosion==null)		explosion = new Explosion(context);
		else					explosion.load(context);
			
		if(propeller == null)	propeller = new Propeller(context);
		else					propeller.load(context);
		
		if(flock == null)			flock = new Flock(context);
		else						flock.load(context);
		
		if(particalSystem == null)	particalSystem = new ParticalSystem(context, displayDensity);
		else						particalSystem.load(context);
		
		if(meet == null)			meet = new Meet(context);
		else						meet.load(context);
		
		if(textTimer == null)		textTimer = new TextTimer(context);
		else						textTimer.load(context);
		
		if(textDigits == null)		textDigits = new TextDigits(context);
		else						textDigits.load(context);
		
		if(textSpeedUp == null)		textSpeedUp = new TextSpeedUp(context);
		else						textSpeedUp.load(context);
		
		if(scoreCounter == null){
			scoreCounter = new ScoreCounter();
			if(terrainType == Terrain.MOUNTAINS){
				scoreCounter.setTimer(Const.TIMER_INITIAL_MINUTES_MOUNTAINS, Const.TIMER_INITIAL_SECUNDES_MOUNTAINS);
			}else{
				scoreCounter.setTimer(Const.TIMER_INITIAL_MINUTES_HILLS, Const.TIMER_INITIAL_SECUNDES_HILLS);
			}
		}
		
		if(radar == null)			radar = new Radar(context);
		else						radar.load(context);
		
		if(targetsSystem == null)	targetsSystem = new TargetsSystem(context, displayDensity, flock.boids.length);
		else						targetsSystem.load(context);
		
		
		if(bundle != null){
			angleY = bundle.getFloat("Render.angleY", 0);
			terrainType = bundle.getInt("terrainType");
			isGamePaused = true;
			
			plane.onRestore(bundle);
			terrain.onRestore(bundle);
			flock.onRestore(bundle);
			
			scoreCounter.onRestore(bundle);
			
			bundle = null;
		}
	}
	
	/**
	 * called when play button is pressed
	 */
	public void resume(){
		isGamePaused = false;
		startCounter.start();
	}
	
	public void pause(){
		if(soundControl != null)	soundControl.pause();
		
		isGamePaused = true;
		isGamePlaying = false;
		
		if(!isFinished){
			handler.sendEmptyMessage(MainActivity.MESSAGE_START_PAUSING);
		}
	}
	
	public void setAngleZ(float y){
		if(isGamePlaying){
			newAngleZ = y*5.0f;
		}
	}
	
	@Override
	public void onSave(Bundle bundle){
		if(plane != null)			plane.onSave(bundle);
		if(terrain != null)			terrain.onSave(bundle);
		if(flock != null)			flock.onSave(bundle);

		if(scoreCounter != null)	scoreCounter.onSave(bundle);
		
		bundle.putFloat("Render.angleY", angleY);
		bundle.putInt("terrainType", terrainType);
	}

	@Override
	public void onRestore(Bundle bundle) {
		this.bundle = bundle;
	}
}
