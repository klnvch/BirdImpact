package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;

import com.klnvch.birdimpact.Param;
import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.Const;
import com.klnvch.birdimpact.counters.ScoreCounter;

import android.content.Context;
import android.opengl.GLES20;

public class TextDigits extends Entity{

	private static final int N = 6;	// number of digits
	
	private FloatBuffer mVertexBuffer[] = new FloatBuffer[3];
	private FloatBuffer mTextureBuffer;
        
    private float vertices[][] = new float[3][3*4*N];
    private float textures[] = new float[2*4*N];
    
    private static int textureId;
    private int[] buffers = new int[3];
    
    public TextDigits(Context context) {

        setPosition(0, Const.SCORE_PLAYING_X0, Const.SCORE_PLAYING_Y0, Const.SCORE_PLAYING_Z0, Const.SCORE_PLAYING_DX, Const.SCORE_PLAYING_DY);
        setPosition(1, Const.RATE_PLAYING_X0, Const.RATE_PLAYING_Y0, Const.RATE_PLAYING_Z0, Const.RATE_PLAYING_DX, Const.RATE_PLAYING_DY);
        setPosition(2, Const.FPS_PLAYING_X0, Const.FPS_PLAYING_Y0, Const.FPS_PLAYING_Z0, Const.FPS_PLAYING_DX, Const.FPS_PLAYING_DY);
    	
        for(int i=0; i!=3; ++i){
			mVertexBuffer[i] = createFloatBuffer(vertices[i]);
        }
		mTextureBuffer = createFloatBuffer(textures);
    	
    	load(context);
	}
    
    public void load(Context context){
    	textureId = loadTexture(context, R.drawable.digits2);
    	loadProgram(context, R.raw.vshader_text, R.raw.fshader_text);
    	
    	
    	GLES20.glGenBuffers(3, buffers, 0);
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices[0].length*4), mVertexBuffer[0], GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices[1].length*4), mVertexBuffer[1], GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices[2].length*4), mVertexBuffer[2], GLES20.GL_STATIC_DRAW);
		
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);   	
    }
    
    private void setPosition(int k, float x0, float y0, float z0, float dx, float dy){
    	for(int i=0; i!=N; ++i){
			// first vertex
			vertices[k][12 * i]     = i*dx + x0;	// x
			vertices[k][12 * i + 1] = -dy + y0;	// y
			vertices[k][12 * i + 2] = z0;			// z
			
			// second vertex
			vertices[k][12 * i + 3] = (i+1)*dx + x0;// x
			vertices[k][12 * i + 4] = -dy + y0;	// y
			vertices[k][12 * i + 5] = z0;			// z
			
			// forth vertex
			vertices[k][12 * i + 6] = i*dx + x0;	// x
			vertices[k][12 * i + 7] = y0;			// y
			vertices[k][12 * i + 8] = z0;			// z
			
			// third vertex
			vertices[k][12 * i + 9] = (i+1)*dx + x0;// x
			vertices[k][12 * i + 10] = y0;			// y
			vertices[k][12 * i + 11] = z0;			// z
    	}
    }
	
	private void build(int[] score){
		
		for(int i=0; i!=N; ++i){
			float j = score[i];
			
			// first vertex
			textures[8 * i]     = j/10.0f;
			textures[8 * i + 1] = 1.0f;
			// second vertex
			textures[8 * i + 2] = (j+1)/10.0f;
			textures[8 * i + 3] = 1.0f;
			// forth vertex
			textures[8 * i + 4] = j/10.0f;
			textures[8 * i + 5] = 0.0f;
			// third vertex
			textures[8 * i + 6] = (j+1)/10.0f;
			textures[8 * i + 7] = 0.0f;
		}

		mTextureBuffer.put(textures);
		mTextureBuffer.position(0);
	}
	
	private void draw(int k, int[] score) {
		
		if(score != null){
			build(score);
		}
	        
	    ///////////////////////////////////////////////////////////////////////////
		GLES20.glUseProgram(program);
		GLES20.glUniform1f(GLES20.glGetUniformLocation(program, ALPHA), 1.0f);
			
		GLES20.glFrontFace(GLES20.GL_CCW);
	        
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[k]);
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
	
	public void draw(ScoreCounter scoreCounter){
		draw(0, scoreCounter.getScoreI());
		draw(1, scoreCounter.getRateI());
		if(Param.showFPS){
			draw(2, scoreCounter.getFPSI());
		}
	}
}
