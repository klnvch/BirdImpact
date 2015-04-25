package com.klnvch.birdimpact.scores;

import java.io.Serializable;

public class HighScore implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7579578677652806287L;
	
	public static final String KEY_NAME = "KEY_NAME";
	public static final String KEY_KILLED_BIRDS = "KEY_KILLED_BIRDS"; 
	public static final String KEY_TIME_SPENT = "KEY_TIME_SPENT"; 
	public static final String KEY_TERRAIN_TYPE = "KEY_TERRAIN_TYPE"; 
	
	public String name;
	public int killedBirds;
	public String timeSpent;
	public int terrainType;
	
	public HighScore(String name, int killedBirds, String timeSpent, int terrainType) {
		this.name = name;
		this.killedBirds = killedBirds;
		this.timeSpent = timeSpent;
		this.terrainType = terrainType;
	}
	
	@Override
	public boolean equals(Object o) {
		HighScore highscore = (HighScore)o;
		return this.name.equals(highscore.name) && this.killedBirds == highscore.killedBirds && this.timeSpent.equals(highscore.timeSpent) && this.terrainType == highscore.terrainType;
	}
}
