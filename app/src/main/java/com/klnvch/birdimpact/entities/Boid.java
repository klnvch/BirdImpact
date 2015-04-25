package com.klnvch.birdimpact.entities;

import android.util.Log;

public class Boid {
	
	private static float SEPARATION = 25.0f;
	private static float COHESION = 400.0f;
	private static float LIMITATION = 14.0f;
	
	public float pX;	// position
	public float pZ;	// position
	public float vX;	// velocity
	public float vZ;	// velocity
	public float targetX;	// target
	public float targetZ;	// target
	public float angle;
	public float frame;
    public boolean frameDirUp;
	public float distance;
	public boolean isAlive = false;
	
	public void update(Boid[] boids){
		cohere(boids);
		separate(boids);
		align(boids);
		tendToPlace();
		avoidPlane();
		limit();
	}
	
	// rule 1
	private void cohere(Boid[] boids){
		float cx = 0;
		float cz = 0;
		int count = 0;
		
		for(int i=0; i!=boids.length; ++i){
			if(boids[i].isAlive && this != boids[i]){
				final float dist = getSquaredDistanceFromBoid(boids[i]);
				if(dist < COHESION){
					cx += boids[i].pX;
					cz += boids[i].pZ;
					++count;
				}
			}
		}
		
		if(count != 0){
			cx /= count;
			cz /= count;
			
			cx -= this.pX;
			cz -= this.pZ;
			
			cx /= 100.0f;
			cz /= 100.0f;
			
			// do it
			this.vX += cx;
			this.vZ += cz;
			
			if(boids[0] == this){
				Log.d("boid", "cohesion: " + cz);
			}
		}
	}
	
	// rule 2
	private void separate(Boid[] boids){
		float cx = 0;
		float cz = 0;
		int count = 0;
		
		for(int i=0; i!=boids.length; ++i){
			if(boids[i].isAlive && this != boids[i]){
				final float dist = getSquaredDistanceFromBoid(boids[i]);
				if(dist < SEPARATION){
					cx -= boids[i].pX - this.pX;
					cz -= boids[i].pZ - this.pZ;
					++count;
				}
			}
		}
		
		if(count != 0){
			cx /= count;
			cz /= count;
			
			// do it
			this.vX += cx;
			this.vZ += cz;
			
			if(boids[0] == this){
				Log.d("boid", "separation: " + cz);
			}
		}
	}
	
	// rule 3
	private void align(Boid[] boids){
		float cx = 0;
		float cz = 0;
		int count = 0;
		
		for(int i=0; i!=boids.length; ++i){
			if(boids[i].isAlive && this != boids[i]){
				cx += boids[i].vX;
				cz += boids[i].vZ;
				++count;
			}
		}
		
		if(count != 0){
			cx /= count;
			cz /= count;
			
			cx -= this.vX;
			cz -= this.vZ;
			
			cx /= 8.0f;
			cz /= 8.0f;
			
			// do it
			this.vX += cx;
			this.vZ += cz;
			
			if(boids[0] == this){
				Log.d("boid", "alignment: " + cz);
			}
		}
	}
	
	// rule 4
	private void tendToPlace(){
		vX += (targetX - this.pX) / 2000.0f;
		vZ += (targetZ - this.pZ) / 2000.0f;
	}
	
	// rule 5
	private void avoidPlane(){
		final float dist = getSquareDistanceFromCenter();
		if(dist < 400){
			vX += dist / 10.0f;
			vZ += dist / 10.0f;
		}
	}
	
	// limit velocity
	private void limit(){
		final float dist = (float)Math.sqrt(vX * vX + vZ * vZ);
		if(dist > LIMITATION){
			vX = (vX / dist) * LIMITATION;
			vZ = (vZ / dist) * LIMITATION;
		}
	}
	
	public float getSquareDistanceFromCenter(){
		distance = pX * pX + pZ * pZ;
		return distance;
	}
	
	public float getSquaredDistanceFromBoid(Boid b){
		return (this.pX - b.pX) * (this.pX - b.pX) + (this.pZ - b.pZ) * (this.pZ - b.pZ);
	}
	
	@Override
	public String toString() {
		return "(" + pX + ", " + pZ + ", "+ vX + ", "+ vZ + ", "+ angle + ", "+ frame + ", " + distance + ", "+ isAlive + ")";
	}
}
