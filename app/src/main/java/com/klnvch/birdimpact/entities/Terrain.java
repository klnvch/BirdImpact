package com.klnvch.birdimpact.entities;

import android.content.Context;
import android.os.Bundle;

public abstract class Terrain extends Entity implements Restorable{
	
	public static final int MOUNTAINS = 1;
	public static final int HILLS = 2;
	
	public final int N;	// NxN size of a grid
	public final float SIZE; // distance between two centers
	
	public float x = 0.0f;
	public float z = 0.0f;
	
	public Terrain(Context context, int n, float size) {
		this.N = n;
		this.SIZE = size;
	}
	
	public abstract void checkVisibility(final float angleY);
	public abstract void preDraw();
	public abstract void postDraw();
	public abstract void update(float angleSky, float timeInterval);
	public abstract boolean isVisible(int i, int j);
	public abstract void draw(final float[] matrix, final int i, final int j);
	public abstract float getHeight(final float x, final float z);
	public abstract void load(Context context);
	
	@Override
	public void onSave(Bundle bundle) {
		bundle.putFloat("Terrain.x", x);
		bundle.putFloat("Terrain.z", z);
	}
	
	@Override
	public void onRestore(Bundle bundle) {
		x = bundle.getFloat("Terrain.x", 0);
		z = bundle.getFloat("Terrain.z", 0);
	}
}
