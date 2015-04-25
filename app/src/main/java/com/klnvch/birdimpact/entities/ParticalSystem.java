package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;
import java.util.Random;

import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.Const;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;


public class ParticalSystem extends Entity{
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;    
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;
    private static final int TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT + VECTOR_COMPONENT_COUNT + PARTICLE_START_TIME_COMPONENT_COUNT;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * 4;
    private static final int color = Color.rgb(255, 25, 25);
    private static final float[] direction0 = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
    private static final float ANGLE_VARIANCE_1 = 20.0f;	// propeller
    private static final float SPEED_VARIANCE_1 = 15.0f;
    private static final float ANGLE_VARIANCE_2 = 40.0f;	// wings
    private static final float SPEED_VARIANCE_2 = 30.0f;
  
    //
    private int nextParticle;											// position to overwrite old particles
    private float[] particles = new float[Const.MAX_PARTICLE_COUNT * TOTAL_COMPONENT_COUNT];
    private float[] counts = new float[4];								// number of particles created starts from 100 and finishes with 0
    private float[] explTime = new float[4];
    private int[] explType = new int[4];
    private float[] explPos = new float[4*4];
    
    // init
    private FloatBuffer floatBuffer;
    private float density;
    private Random random = new Random();
    
    // temporary buffers
    private float[] rotationMatrix = new float[16];
    private float[] resultVector = new float[4];
    private float[] explosionsNewPosition = new float[4*4];
    private float[] direction1 = new float[4];
    
    public ParticalSystem(Context context, float density){
    	this.density = density;
    	
        floatBuffer = createFloatBuffer(particles);
        
        load(context);
    }
    
    public void load(Context context) {
    	loadProgram(context, R.raw.vshader_partical, R.raw.fshader_partical);
	}
    
    /*
     * direction - direction vector of size 4
     * 
     * newpos - array of size 16, contains 4 points
     */
    private void addParticles(float currentTime, float angle){
    	Matrix.setRotateM(rotationMatrix, 0, -angle, 0.0f, 1.0f, 0.0f);
		Matrix.multiplyMV(direction1, 0, rotationMatrix, 0, direction0, 0);
		for(int i=0; i!=4; ++i){
			Matrix.multiplyMV(explosionsNewPosition, i*4, rotationMatrix, 0, explPos, i*4);
		}
    	
    	for(int i=0; i!=4; ++i){
    		
    		if(counts[i] > 5){
    			counts[i] -= 5;
    		}else{
    			continue;
    		}
    		
    		float angleVariance = 0;
    		float speedVariance = 0;
    		if(explType[i] == Const.PROPELLER_TYPE){
    			angleVariance = ANGLE_VARIANCE_1;
    			speedVariance = SPEED_VARIANCE_1;
    		}
    		if(explType[i] == Const.WINGS_TYPE){
    			angleVariance = ANGLE_VARIANCE_2;
    			speedVariance = SPEED_VARIANCE_2;
    		}
    		
        	for (int j = 0; j < counts[i]; j++) {
        		float a1 = (random.nextFloat() - 0.5f) * angleVariance;
        		float a2 = (random.nextFloat() - 0.5f) * angleVariance;
        		float a3 = (random.nextFloat() - 0.5f) * angleVariance;
        		Matrix.setRotateEulerM(rotationMatrix, 0, a1, a2, a3);
        		Matrix.multiplyMV(resultVector, 0, rotationMatrix, 0, direction1, 0);
        		float speedAdjustment = 10.0f + random.nextFloat() * speedVariance;
            
        		float dx1 = resultVector[0] * speedAdjustment;
        		float dy1 = resultVector[1] * speedAdjustment;
        		float dz1 = resultVector[2] * speedAdjustment;       
        		addParticle(explosionsNewPosition[i*4+0], explosionsNewPosition[i*4+1], explosionsNewPosition[i*4+2], color, dx1, dy1, dz1, currentTime);
           
        	}
    	}
    }
    
    private void addParticle(float x, float y, float z, int color, float dx, float dy, float dz, float particleStartTime){
    	
        int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;
		
        int currentOffset = particleOffset;        
        nextParticle++;
        
        if (nextParticle == Const.MAX_PARTICLE_COUNT) {
            nextParticle = 0;
        }
        
        particles[currentOffset++] = x;
        particles[currentOffset++] = y;
        particles[currentOffset++] = z;
        
        particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;
        
        particles[currentOffset++] = dx;
        particles[currentOffset++] = dy;
        particles[currentOffset++] = dz;             
        
        particles[currentOffset++] = particleStartTime;

        floatBuffer.position(particleOffset);
        floatBuffer.put(particles, particleOffset, TOTAL_COMPONENT_COUNT);
        floatBuffer.position(0);
    }
    
    public void setExplosion(float[] position, int type){
    	
    	int k = 0;
		for(int i=1; i!=4; ++i){
			if(explTime[k] < explTime[i]){
				k = i;
			}
		}
		explTime[k] = 0.0f;
		
		explPos[k*4 + 0] = position[0];
		explPos[k*4 + 1] = position[1];
		explPos[k*4 + 2] = position[2];
    	
    	
    	counts[k] = 100f;
    	explType[k] = type;
    }
    
    public void update(float timeInterval, float currentTime, float angle){
		explTime[0] += timeInterval;
		explTime[1] += timeInterval;
		explTime[2] += timeInterval;
		explTime[3] += timeInterval;
		
		addParticles(currentTime, angle);
	}

    public void draw(float[] matrix, float currentTime) {
        
        int dataOffset = 0;
        
		GLES20.glUseProgram(program);
		
		GLES20.glUniform1f(GLES20.glGetUniformLocation(program, DOT_SIZE), density);
		
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);
		GLES20.glUniform1f(GLES20.glGetUniformLocation(program, TIME), currentTime);  
        
        floatBuffer.position(dataOffset);        
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, floatBuffer);
        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
        floatBuffer.position(0);
        dataOffset += POSITION_COMPONENT_COUNT;
        
        floatBuffer.position(dataOffset);        
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, COLOR), COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, floatBuffer);
        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, COLOR));
        floatBuffer.position(0);        
        dataOffset += COLOR_COMPONENT_COUNT;
        
        floatBuffer.position(dataOffset);        
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, DIRECTION), VECTOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, floatBuffer);
        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, DIRECTION));
        floatBuffer.position(0);
        dataOffset += VECTOR_COMPONENT_COUNT;       
        
        floatBuffer.position(dataOffset);        
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, START_TIME), PARTICLE_START_TIME_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, floatBuffer);
        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, START_TIME));
        floatBuffer.position(0);
        
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, Const.MAX_PARTICLE_COUNT);
    }
}
