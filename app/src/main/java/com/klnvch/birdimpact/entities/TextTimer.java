package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;

import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.Const;

import android.content.Context;
import android.opengl.GLES20;

public class TextTimer extends Entity{
	
	private static final int N = 5;
	
	private float flashing = 1.0f;
    private boolean flashingUp = false;
	
	private FloatBuffer mVertexBuffer;;
	private FloatBuffer mTextureBuffer;
        
    private float vertices[] = new float[3*4*N];
    private float textures[] = new float[2*4*N];
    
    private int textureId;
    private int[] buffers = new int[1];
    
    public TextTimer(Context context) {
    	
    	init(Const.TIMER_PLAYING_X0, Const.TIMER_PLAYING_Y0, Const.TIMER_PLAYING_Z0, Const.TIMER_PLAYING_DX, Const.TIMER_PLAYING_DY);
		
		mVertexBuffer = createFloatBuffer(vertices);
		mTextureBuffer = createFloatBuffer(textures);
		
		load(context);
	}
    
    public void load(Context context) {
    	textureId = loadTexture(context, R.drawable.digits);
    	loadProgram(context, R.raw.vshader_text, R.raw.fshader_text);
    	
		///
		GLES20.glGenBuffers(1, buffers, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices.length*4), mVertexBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
    
    private void init(float x0, float y0, float z0, float dx, float dy){
    	float x1 = -2.0f*dx - dx/6.0f + x0;
    	float x2 = -1.0f*dx - dx/6.0f + x0;
    	float x3 = -0.0f*dx - dx/6.0f + x0;
    	float x4 = +0.0f*dx + dx/6.0f + x0;
    	float x5 = +1.0f*dx + dx/6.0f + x0;
    	float x6 = +2.0f*dx + dx/6.0f + x0;
    	
    	float[] xs = new float[]{x1, x2, x3, x4, x5, x6};
    	
    	float y1 = +dy/2.0f + y0;
    	float y2 = -dy/2.0f + y0;
    	
		for(int i=0; i!=N; ++i){
			// first vertex
    		vertices[12 * i]     = xs[i];		// x
			vertices[12 * i + 1] = y2;			// y
			vertices[12 * i + 2] = z0;			// z
			// second vertex
    		vertices[12 * i + 3] = xs[i+1];		// x
			vertices[12 * i + 4] = y2;			// y
			vertices[12 * i + 5] = z0;			// z
			// forth vertex
    		vertices[12 * i + 6] = xs[i];		// x
			vertices[12 * i + 7] = y1;			// y
			vertices[12 * i + 8] = z0;			// z
			// third vertex
    		vertices[12 * i + 9] = xs[i+1];		// x
			vertices[12 * i + 10] = y1;			// y
			vertices[12 * i + 11] = z0;			// z
		}
    }

	private void buildTimer(int[] time){

		for(int i=0; i!=N; ++i){
			
			if(time[i] >=0 && time[i] <= 9){
				
				// first vertex
				textures[8 * i]     = (71.0f*time[i])/731.0f;
				textures[8 * i + 1] = 1.0f;
				// second vertex
				textures[8 * i + 2] = (71.0f*(time[i]+1))/731.0f;
				textures[8 * i + 3] = 1.0f;
				// forth vertex
				textures[8 * i + 4] = (71.0f*time[i])/731.0f;
				textures[8 * i + 5] = 0.0f;
				// third vertex
				textures[8 * i + 6] = (71.0f*(time[i]+1))/731.0f;
				textures[8 * i + 7] = 0.0f;
				
			}else{
				textures[8 * i]     = 710.0f/731.0f;
				textures[8 * i + 1] = 1.0f;
				// second vertex
				textures[8 * i + 2] = 1.0f;
				textures[8 * i + 3] = 1.0f;
				// forth vertex
				textures[8 * i + 4] = 710.0f/731.0f;
				textures[8 * i + 5] = 0.0f;
				// third vertex
				textures[8 * i + 6] = 1.0f;
				textures[8 * i + 7] = 0.0f;
			}
		}
       
		mTextureBuffer.put(textures);
		mTextureBuffer.position(0);
	}
	
	public void draw(int[] time) {
		
		buildTimer(time);
		

		if(time[4] <= 9 && time[3] == 0 && time[1] == 0 && time[0] == 0){
			if(flashingUp){
				flashing += 0.04f;
				if(flashing >= 1.0f){
					flashingUp = false;
				}
			}else{
				flashing -= 0.04f;
				if(flashing <= 0.5f){
					flashingUp = true;
				}
			}
		}else{
			flashing = 1.0f;
			flashingUp = false;
		}
			
	    ///////////////////////////////////////////////////////////////////////////
		GLES20.glUseProgram(program);
		
		GLES20.glUniform1f(GLES20.glGetUniformLocation(program, ALPHA), flashing);
			
		GLES20.glFrontFace(GLES20.GL_CCW);
	    
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glUniform1i(GLES20.glGetUniformLocation(program, TEXTURE), 0);
			
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, TEXTURE_COORD), 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, TEXTURE_COORD));

		for (int face = 0; face < N; face++) {
	         GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, face*4, 4);
	    }
	}
}
