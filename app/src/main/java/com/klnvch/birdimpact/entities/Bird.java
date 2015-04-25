package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.Const;
import com.klnvch.birdimpact.utils.ObjLoader;

import android.content.Context;
import android.opengl.GLES20;
import android.os.Bundle;

public class Bird extends Entity implements Restorable{
	
	private static final int MAX_BIRDS = 24;
	
	private static final float WINGS_SPEED = 30.0f;
	private static final int[] files = new int[]{R.raw.bird1, R.raw.bird2, R.raw.bird3, R.raw.bird4, R.raw.bird5, R.raw.bird6, R.raw.bird7, R.raw.bird8, R.raw.bird9, R.raw.bird10, R.raw.bird11, R.raw.bird12};
	private static final int NUMBER_OF_FRAMES = 12;
	
	public static float speed = Const.SPEED_BIRD;
	
	private int aliveBirdNumber = 0;
	private int currPos = 0;										// for looking not alive birds
	public float[] x = new float[MAX_BIRDS];
	public float[] z = new float[MAX_BIRDS];
	public float[] angle = new float[MAX_BIRDS];
	public float[] distance = new float[MAX_BIRDS];
	private float[] sin1 = new float[MAX_BIRDS];
	private float[] cos1 = new float[MAX_BIRDS];
	private boolean[] alive = new boolean[MAX_BIRDS];
	private float frame[] = new float[MAX_BIRDS];
    private boolean frameDirUp[] = new boolean[MAX_BIRDS];
	
	private FloatBuffer[] mVertexBuffer = new FloatBuffer[NUMBER_OF_FRAMES];
    private ShortBuffer mIndexBuffer;
    private float vertices[];
    private short indices[];
    private int[] buffers = new int[1 + NUMBER_OF_FRAMES];

    private Random r = new Random();
	        
	public Bird(Context context) {
		
		for(int i=0; i!=NUMBER_OF_FRAMES; ++i){
			loadFromFile(context, files[i]);
			mVertexBuffer[i] = createFloatBuffer(vertices);
		}
		mIndexBuffer = createShortBuffer(indices);
		
		load(context);
	}
	
	public void load(Context context){
		loadProgram(context, R.raw.vshader_bird, R.raw.fshader_bird);

		///
		GLES20.glGenBuffers(1 + NUMBER_OF_FRAMES, buffers, 0);
		for(int i=0; i!=NUMBER_OF_FRAMES; ++i){
			//buffers[i] = bufferArray(mVertexBuffer[i]);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[i]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices.length*4), mVertexBuffer[i], GLES20.GL_STATIC_DRAW);
		}
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[NUMBER_OF_FRAMES]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, (indices.length*2), mIndexBuffer, GLES20.GL_STATIC_DRAW);
		//buffers[NUMBER_OF_FRAMES] = bufferElementArray(mIndexBuffer);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);	
	}
	
	private void loadFromFile(Context context, int file){
		ObjLoader.loadObj2(context, file);
		vertices = ObjLoader.vertices;
		indices = ObjLoader.indices;
	}
	
	
	private int getFreePosition(){
		for(int i=currPos; i!=MAX_BIRDS; ++i){
			if(!isAlive(i)){
				currPos = i+1;
				if(currPos == MAX_BIRDS)	currPos = 0;
				return i;
			}
		}
		for(int i=0; i!=currPos; ++i){
			if(!isAlive(i)){
				currPos = i+1;
				if(currPos == MAX_BIRDS)	currPos = 0;
				return i;
			}
		}
		return 0;
	}
	
	private void createBirdGroup(int n){
		
		// get random angle from 0 to 360
		float angleGroup = (float)(r.nextInt(360));
			
		float cos1Group = (float)Math.cos((2.0*Math.PI*angleGroup)/360.0);
		float sin1Group = (float)Math.sin((2.0*Math.PI*angleGroup)/360.0);
			
		for(int j=0; j!=n; ++j){
			
			int i = getFreePosition();
			
			angle[i] = angleGroup;
			cos1[i] = cos1Group;
			sin1[i] = sin1Group;
			alive[i] = true;
			frame[i] = r.nextInt(NUMBER_OF_FRAMES);
			frameDirUp[i] = r.nextBoolean();
			
			initPosition(i, j, n);
			
			float newX = x[i]*cos1Group - z[i]*sin1Group;
			float newZ = x[i]*sin1Group + z[i]*cos1Group;
			
			x[i] = newX;
			z[i] = newZ;
		}
		
		aliveBirdNumber += n;
	}
	
	private void initPosition(int i, int j, int n){
		final float DX = 5.0f;
		final float DZ = 5.0f;
		
		switch (n) {
		case 1:
			x[i] = Const.START_X;
			z[i] = Const.START_Z;
			break;
		case 3:
			x[i] = Const.START_X + (j-1)*DX;
			z[i] = Const.START_Z;
			break;
		case 5:
			x[i] = Const.START_X + (j-2)*DX;
			z[i] = Const.START_Z - Math.abs(j-2)*DZ;
			break;
		case 7:
			if(j < 3){
				x[i] = Const.START_X + (j-1)*DX;
				z[i] = Const.START_Z;
			}else{
				x[i] = Const.START_X + (j-4.5f)*DX;
				z[i] = Const.START_Z - DZ;
			}
			break;
		case 9:
			x[i] = Const.START_X + (j-4)*DX;
			z[i] = Const.START_Z - Math.abs(j-4)*DZ;
			break;
		case 11:
			if(j < 5){
				x[i] = Const.START_X + (j-2)*DX;
				z[i] = Const.START_Z;
			}else{
				x[i] = Const.START_X + (j-7.5f)*DX;
				z[i] = Const.START_Z - DZ;
			}
			break;
		case 13:
			x[i] = Const.START_X + (j-6)*DX;
			z[i] = Const.START_Z - Math.abs(j-6)*DZ;
			break;
		case 15:
			if(j < 7){
				x[i] = Const.START_X + (j-3)*DX;
				z[i] = Const.START_Z - Math.abs(j-3)*DZ;
			}else if(j < 14){
				x[i] = Const.START_X + (j-10)*DX;
				z[i] = Const.START_Z - Math.abs(j-9)*DZ;
			}else{
				x[i] = Const.START_X;
				z[i] = Const.START_Z - 2.0f*DZ;
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 
	 * @param angleSky
	 * 		for calculating dismiss for plane moving
	 * @param timeInterval
	 */
	public float update(float angleSky, float timeInterval, int timeLeft){
		
		// plane displacement
		final float dx = (float)(- Plane.speed * timeInterval * Math.sin((2.0*Math.PI*angleSky)/360.0));
	    final float dz = (float)(  Plane.speed * timeInterval * Math.cos((2.0*Math.PI*angleSky)/360.0));
	    
	    // minimal distance
	    float minDistance = Float.MAX_VALUE;
	    
		for(int i=0; i!=MAX_BIRDS; ++i){
			
			if(!isAlive(i))	continue;
			
			// check if bird is too far
			distance[i] = x[i] * x[i] + z[i] * z[i];
			if(distance[i] < minDistance){
				minDistance = distance[i];
			}
			if(distance[i] > Const.FAR_DISTANCE){
				killBird(i);
			}
			
			// update position
			x[i] = x1(i, timeInterval) + dx;
			z[i] = z1(i, timeInterval) + dz;
			
			// update wings position
			if(frameDirUp[i]){
				frame[i] += WINGS_SPEED * timeInterval;
				if(frame[i] >= NUMBER_OF_FRAMES - 1){
					frame[i] = NUMBER_OF_FRAMES - 1;
					frameDirUp[i] = false;
				}
			}else{
				frame[i] -= WINGS_SPEED * timeInterval;
				if(frame[i] <= 0){
					frame[i] = 0;
					frameDirUp[i] = true;
				}
			}
		}
		
		// launch new birds
		if(timeLeft < 10){
			if(MAX_BIRDS-aliveBirdNumber > 15){
				createBirdGroup(15);
			}
		}else if(timeLeft < 9){
			if(MAX_BIRDS-aliveBirdNumber > 13){
				createBirdGroup(13);
			}
		}else if(timeLeft < 11){
			if(MAX_BIRDS-aliveBirdNumber > 11){
				createBirdGroup(11);
			}
		}else if(timeLeft < 13){
			if(MAX_BIRDS-aliveBirdNumber > 9){
				createBirdGroup(9);
			}
		}else if(timeLeft < 15){
			if(MAX_BIRDS-aliveBirdNumber > 7){
				createBirdGroup(7);
			}
		}else if(timeLeft < 17){
			if(MAX_BIRDS-aliveBirdNumber > 5){
				createBirdGroup(5);
			}
		}else if(timeLeft < 19){
			if(MAX_BIRDS-aliveBirdNumber > 3){
				createBirdGroup(3);
			}
		}else{
			if(MAX_BIRDS-aliveBirdNumber > 1){
				createBirdGroup(1);
			}
		}
		
		// set geese volume
		return minDistance;
	}
	
	public void killBird(int i){
		alive[i] = false;
		--aliveBirdNumber;
	}
	
	public boolean isAlive(int i) {
		return alive[i];
	}

	private float x1(int i, float t){
		return x[i] - Bird.speed * t * sin1[i];
	}
	
	private float z1(int i, float t){
		return z[i] + Bird.speed * t  *cos1[i];
	}
	
	public void draw(float[] matrix, int i) {
		GLES20.glUseProgram(program);
		
		GLES20.glFrontFace(GLES20.GL_CW);
		
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);
			
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer[(int)frame[i]]);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[NUMBER_OF_FRAMES]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, 0);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        
	}

	@Override
	public void onSave(Bundle bundle) {
		for(int i=0; i!=MAX_BIRDS; ++i){
			if(alive[i]){
				bundle.putBoolean("Bird.alive" + i, true);
				bundle.putFloat("Bird.x" + i, x[i]);
				bundle.putFloat("Bird.z" + i, z[i]);
				bundle.putFloat("Bird.angle" + i, angle[i]);
			}
		}
	}

	@Override
	public void onRestore(Bundle bundle) {

		aliveBirdNumber = 0;
		currPos = 0;
		
		for(int i=0; i!=MAX_BIRDS; ++i){
			alive[i] = bundle.getBoolean("Bird.alive" + i, false);
			if(alive[i]){
				x[i] = bundle.getFloat("Bird.x" + i, 0);
				z[i] = bundle.getFloat("Bird.z" + i, 0);
				angle[i] = bundle.getFloat("Bird.angle" + i, 0);
				
				distance[i] = x[i]*x[i] + z[i]*z[i];
				sin1[i] = (float)Math.sin((2.0*Math.PI*angle[i])/360.0);
				cos1[i] = (float)Math.cos((2.0*Math.PI*angle[i])/360.0);
				frame[i] = r.nextInt(NUMBER_OF_FRAMES);
				frameDirUp[i] = r.nextBoolean();
				
				++aliveBirdNumber;
			}
		}
	}
}
