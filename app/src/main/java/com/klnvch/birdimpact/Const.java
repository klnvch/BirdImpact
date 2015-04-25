package com.klnvch.birdimpact;

public class Const {
	
	public static final boolean DEBUG = false;
	public static final long MIN_TIME_INTERVAL = 15000;
	
	// for renderer
	public static final float FOVY = 45.0f;
	public static final float ZNEAR = 1.0f;
	public static final float ZFAR = 400.0f;
	
	// plane
	public static final float SPEED_BIRD = 7.0f;		//	Bird.update			10-50 MPH
	public static final float SPEED_PLANE = 25.0f;		//	Bird.update			120 MPH
	public static final float SPEED_TURN = 1.5f;
	
	// birds
	public static final float START_X = 0;
	public static final float START_Z = -390;
	/**
	 * Squared distance from the center, where bird should be killed
	 */
	public static final float FAR_DISTANCE = ZFAR * ZFAR;
	
	// radar, targets
	public static final float RADAR_SIZE = 0.25f;
	public static final float RADAR_SCALE = RADAR_SIZE / ZFAR;
	
	// Explosions
	public static final int PROPELLER_TYPE = 1;
    public static final int WINGS_TYPE = 2;
    
    // Blood
    public static final int MAX_PARTICLE_COUNT = 4000;
	
	// sky radius
	public static final float RADIUS = 350;
	public static final float diametr = 2 * RADIUS;
	public static final float HORIZONT_Y = -25.0f;		// SkyDome(init); Renderer(landscape translate)
	
	// control parameters
	public static final float TURN_ACCELERATION = 80.0f;
	public static final float MAX_PLANE_ANGLE = 40.0f;
	public static final float MIN_PLANE_ANGLE = -40.0f;
	
	// timer initial values
	public static final int TIMER_INITIAL_MINUTES_MOUNTAINS = 10;
	public static final int TIMER_INITIAL_SECUNDES_MOUNTAINS = 15;
	
	public static final int TIMER_INITIAL_MINUTES_HILLS = 3;
	public static final int TIMER_INITIAL_SECUNDES_HILLS = 15;

	// Colors
	public static final float BACKGROUND_COLOR_RED = 0.10196078431372549019607843137255f;	//26
	public static final float BACKGROUND_COLOR_GREEN = 0.11764705882352941176470588235294f;	//30
	public static final float BACKGROUND_COLOR_BLUE = 0.13333333333333333333333333333333f; 	//34
	public static final float BACKGROUND_COLOR_ALPHA = 0.0f;
	
	// score position while playing
	public static final float SCORE_PLAYING_X0 = 0.5f;
	public static final float SCORE_PLAYING_Y0 = 0.9f;
	public static final float SCORE_PLAYING_Z0 = 0.0f;
	
	public static final float SCORE_PLAYING_DX = 0.08f;
	public static final float SCORE_PLAYING_DY = 0.1f;
	
	// rate position while playing
	public static final float RATE_PLAYING_X0 = 0.5f;
	public static final float RATE_PLAYING_Y0 = 0.78f;
	public static final float RATE_PLAYING_Z0 = 0.0f;
		
	public static final float RATE_PLAYING_DX = 0.08f;
	public static final float RATE_PLAYING_DY = 0.1f;
	
	// fps position while playing
	public static final float FPS_PLAYING_X0 = 0.5f;
	public static final float FPS_PLAYING_Y0 = 0.66f;
	public static final float FPS_PLAYING_Z0 = 0.0f;
	
	public static final float FPS_PLAYING_DX = 0.08f;
	public static final float FPS_PLAYING_DY = 0.1f;
	
	// timer position while playing
	public static final float TIMER_PLAYING_X0 = 0.0f;
	public static final float TIMER_PLAYING_Y0 = 0.75f;
	public static final float TIMER_PLAYING_Z0 = 0.0f;
	
	public static final float TIMER_PLAYING_DX = 0.1775f;
	public static final float TIMER_PLAYING_DY = 0.275f;
}
