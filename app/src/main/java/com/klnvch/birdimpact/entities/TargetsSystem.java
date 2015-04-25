package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;

import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.Const;

import android.content.Context;
import android.opengl.GLES20;

public class TargetsSystem extends Entity{
	
	private final float density;
	private final int maxBoids;
	
    private float[] positions;
    
    private FloatBuffer floatBuffer;
    
    public TargetsSystem(Context context, float density, int maxBoids){
    	this.density = density;
    	this.maxBoids = maxBoids;
    	this.positions = new float[3 * maxBoids];

        floatBuffer = createFloatBuffer(positions);
        
        load(context);
    }
    
    public void load(Context context){
    	loadProgram(context, R.raw.vshader_targets, R.raw.fshader_targets);
    }
    
    public void set(int i, float x, float y){
    	
    	float _x = x * Const.RADAR_SCALE;
    	float _y = y * Const.RADAR_SCALE;
    	
    	// check if the target is inside radar
    	if(_x*_x + _y*_y <= 0.0625f){	// good
    		floatBuffer.position(i*3);
        	floatBuffer.put(_x);
        	floatBuffer.position(i*3 + 1);
        	floatBuffer.put(_y);
        	floatBuffer.position(0);
    	}else{							// bad
    		floatBuffer.position(i*3);
        	floatBuffer.put(-100);
        	floatBuffer.position(i*3 + 1);
        	floatBuffer.put(-100);
        	floatBuffer.position(0);
    	}
    }

    public void draw(float[] matrix) {
		GLES20.glUseProgram(program);
		
		GLES20.glUniform1f(GLES20.glGetUniformLocation(program, DOT_SIZE), density);

		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);
               
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, floatBuffer);
        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
        
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, maxBoids);
    }
}
