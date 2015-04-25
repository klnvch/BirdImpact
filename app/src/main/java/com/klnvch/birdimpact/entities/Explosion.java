package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;

import com.klnvch.birdimpact.R;

import android.content.Context;
import android.opengl.GLES20;

public class Explosion extends Entity{
	
	private static final float SIZE = 16.0f;
	
	private static final int WIDTH = 8;
	private static final int HEIGHT = 6;
	
	private float frame = WIDTH*HEIGHT;
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;
        
    private float vertices[] = new float[3*4];
    private float textures[] = new float[2*4];
    
    private static int textureId;
    
    public Explosion(Context context) {

        init();
    	
		mVertexBuffer = createFloatBuffer(vertices);
		mTextureBuffer = createFloatBuffer(textures);
    	
    	load(context);
	}
    
    public void load(Context context){
    	textureId = loadTexture(context, R.drawable.explosions);
    	loadProgram(context, R.raw.vshader, R.raw.fshader);	
    }
    
    private void init(){
		// first vertex
		vertices[0] = -SIZE;	// x
		vertices[1] = -SIZE;	// y
		vertices[2] = 0;			// z
			
		// second vertex
		vertices[3] = SIZE;// x
		vertices[4] = -SIZE;	// y
		vertices[5] = 0;			// z
			
		// forth vertex
		vertices[6] = -SIZE;	// x
		vertices[7] = SIZE;			// y
		vertices[8] = 0;			// z
			
		// third vertex
		vertices[9] = SIZE;// x
		vertices[10] = SIZE;			// y
		vertices[11] = 0;			// z
    }
    
    public void startExplosion(){
    	if(frame >= WIDTH*HEIGHT)	frame = 0;
    }
	
	private void build(){
		
		final int frameId = (int)frame;
		
		int i = frameId/WIDTH;
		int j = frameId%WIDTH;
		
		// first vertex
		textures[0]     = j/8.0f;
		textures[1] = (i+1)/6.0f;
		// second vertex
		textures[2] = (j+1)/8.0f;
		textures[3] = (i+1)/6.0f;
		// forth vertex
		textures[4] = j/8.0f;
		textures[5] = i/6.0f;
		// third vertex
		textures[6] = (j+1)/8.0f;
		textures[7] = i/6.0f;

		mTextureBuffer.put(textures);
		mTextureBuffer.position(0);
	}
	
	public void update(final float timeInterval){
		// duration 3 seconds
		frame += 24.0f * timeInterval;
	}
	
	public void draw(float[] matrix) {
		
		if(frame >= WIDTH*HEIGHT)	return;
		
		build();
	        
	    ///////////////////////////////////////////////////////////////////////////
		GLES20.glUseProgram(program);
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);
			
		GLES20.glFrontFace(GLES20.GL_CCW);
			
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
				
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glUniform1i(GLES20.glGetUniformLocation(program, TEXTURE), 0);
			
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, TEXTURE_COORD), 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, TEXTURE_COORD));

	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	}
}
