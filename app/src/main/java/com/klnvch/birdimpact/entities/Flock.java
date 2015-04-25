package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import android.content.Context;
import android.opengl.GLES20;
import android.os.Bundle;

import com.klnvch.birdimpact.Const;
import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.utils.ObjLoader;

public class Flock extends Entity implements Restorable{
	private static final int[] frames = new int[]{R.raw.bird1, R.raw.bird2, R.raw.bird3, R.raw.bird4, R.raw.bird5, R.raw.bird6, R.raw.bird7, R.raw.bird8, R.raw.bird9, R.raw.bird10, R.raw.bird11, R.raw.bird12};
	
	public static final int MAX_BOIDS = 54;
	private static final int FLOCK_SIZE = 36;
	private static final float WINGS_SPEED = 30.0f;
	
	public Boid[] boids = new Boid[MAX_BOIDS];
	private int numAliveBoids = 0;
	private int currPos = 0;	
	
	private FloatBuffer[] mVertexBuffer = new FloatBuffer[frames.length];
    private ShortBuffer mIndexBuffer;
    private float vertices[];
    private short indices[];
    private int[] buffers = new int[1 + frames.length];
    
    private Random r = new Random();
	
	public Flock(Context context) {
		for(int i=0; i!=MAX_BOIDS; ++i){
			boids[i] = new Boid();
		}
		
		for(int i=0; i!=frames.length; ++i){
			loadFromFile(context, frames[i]);
			mVertexBuffer[i] = createFloatBuffer(vertices);
		}
		mIndexBuffer = createShortBuffer(indices);
		
		load(context);
	}
	
	private void loadFromFile(Context context, int file){
		ObjLoader.loadObj2(context, file);
		vertices = ObjLoader.vertices;
		indices = ObjLoader.indices;
	}
	
	public void load(Context context){
		loadProgram(context, R.raw.vshader_bird, R.raw.fshader_bird);

		///
		GLES20.glGenBuffers(1 + frames.length, buffers, 0);
		for(int i=0; i!=frames.length; ++i){
			//buffers[i] = bufferArray(mVertexBuffer[i]);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[i]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices.length*4), mVertexBuffer[i], GLES20.GL_STATIC_DRAW);
		}
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[frames.length]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, (indices.length*2), mIndexBuffer, GLES20.GL_STATIC_DRAW);
		//buffers[NUMBER_OF_FRAMES] = bufferElementArray(mIndexBuffer);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);	
	}
	
	public float update(float angleSky, float timeInterval, float planeSpeed){
		
		final float dx = (float)(- planeSpeed * timeInterval * Math.sin((2.0*Math.PI*angleSky)/360.0));
	    final float dz = (float)(  planeSpeed * timeInterval * Math.cos((2.0*Math.PI*angleSky)/360.0));
	    
	    float minSquaredDistance = Float.MAX_VALUE;
		
		// create boids 
		if(MAX_BOIDS - numAliveBoids > FLOCK_SIZE){
			createFlock(angleSky);
			numAliveBoids += FLOCK_SIZE;
		}
		
		for(int i=0; i!=boids.length; ++i){
			
			// update boid position
			if(boids[i].isAlive){
				
				// update position
				boids[i].update(boids);
				boids[i].pX += (boids[i].vX + dx) * timeInterval;
				boids[i].pZ += (boids[i].vZ + dz) * timeInterval;
				
				// move by plane displacement
				
				// kill birds out of range
				final float squaredDistance = boids[i].getSquareDistanceFromCenter();
				if(squaredDistance < minSquaredDistance){
					minSquaredDistance = squaredDistance;
				}
				if(squaredDistance > Const.FAR_DISTANCE){
					killBird(i);
				}
				
				// update wings positions
				if(boids[i].frameDirUp){
					boids[i].frame += WINGS_SPEED * timeInterval;
					if(boids[i].frame >= frames.length - 1){
						boids[i].frame = frames.length - 1;
						boids[i].frameDirUp = false;
					}
				}else{
					boids[i].frame -= WINGS_SPEED * timeInterval;
					if(boids[i].frame <= 0){
						boids[i].frame = 0;
						boids[i].frameDirUp = true;
					}
				}
			}
		}
		
		return minSquaredDistance;
	}
	
	private void createFlock(float angleSky){
		
		for(int i=0; i!=FLOCK_SIZE; ++i){
			int k = getFreePosition();
			
			boids[k].isAlive = true;
			boids[k].frame = r.nextInt(frames.length);
			boids[k].frameDirUp = r.nextBoolean();
			boids[k].pX = 0.0f + (r.nextFloat() - 0.5f);
			boids[k].pZ = Const.START_Z + (r.nextFloat() - 0.5f);;
			boids[k].vX = 0.0f;
			boids[k].vZ = Const.SPEED_BIRD;
			boids[k].targetX = 0.0f;
			boids[k].targetZ = 500.0f;
		}
	}
	
	private int getFreePosition(){
		for(int i=currPos; i!=MAX_BOIDS; ++i){
			if(!boids[i].isAlive){
				currPos = i+1;
				if(currPos == MAX_BOIDS)	currPos = 0;
				return i;
			}
		}
		for(int i=0; i!=currPos; ++i){
			if(!boids[i].isAlive){
				currPos = i+1;
				if(currPos == MAX_BOIDS)	currPos = 0;
				return i;
			}
		}
		return 0;
	}
	
	public void killBird(int i){
		boids[i].isAlive = false;
		--numAliveBoids;
	}
	
	public void draw(float[] matrix, int i) {
		GLES20.glUseProgram(program);
		
		GLES20.glFrontFace(GLES20.GL_CW);
		
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);
			
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer[(int)boids[i].frame]);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[frames.length]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, 0);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        
	}

	@Override
	public void onSave(Bundle bundle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestore(Bundle bundle) {
		// TODO Auto-generated method stub
		
	}
}
