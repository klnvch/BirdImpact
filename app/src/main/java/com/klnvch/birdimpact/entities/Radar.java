package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;

import com.klnvch.birdimpact.R;

import android.content.Context;
import android.opengl.GLES20;


public class Radar extends Entity{
	 
	private static final float SIZE = 0.25f;
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;
        
    private float vertices[] = new float[3*4];
    private float textures[] = new float[2*4];
    
    private static int textureId;
    private int[] buffers = new int[2];
    
    public float angle;
	        
	public Radar(Context context) {
		init();
		
		mVertexBuffer = createFloatBuffer(vertices);
		mTextureBuffer = createFloatBuffer(textures);
		
		load(context);
	}
	
	public void load(Context context){
		textureId = loadTexture(context, R.drawable.radar3);
		loadProgram(context, R.raw.vshader, R.raw.fshader);
		
		GLES20.glGenBuffers(2, buffers, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices.length*4), mVertexBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (textures.length*4), mTextureBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
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
		
		// first vertex
		textures[0] = 0.0f;
		textures[1] = 1.0f;
		// second vertex
		textures[2] = 1.0f;
		textures[3] = 1.0f;
		// forth vertex
		textures[4] = 0.0f;
		textures[5] = 0.0f;
		// third vertex
		textures[6] = 1.0f;
		textures[7] = 0.0f;
		
	}
	
	public void update(float interval){
		angle -= 450.0f * interval;
	}
	
	public void draw(float[] matrix) {
		GLES20.glUseProgram(program);
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);
			
		GLES20.glFrontFace(GLES20.GL_CCW);
			
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
		//GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
		//GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
				
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glUniform1i(GLES20.glGetUniformLocation(program, TEXTURE), 0);
			
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, TEXTURE_COORD), 2, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, TEXTURE_COORD));
		//GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, TEXTURE_COORD), 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
		//GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, TEXTURE_COORD));

	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
}
