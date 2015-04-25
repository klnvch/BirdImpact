package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.Const;

import android.content.Context;
import android.opengl.GLES20;

public class SkyDome extends Entity{	
	private static final int STACKS = 4;
	private static final int SLICES = 8;
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;
    private ShortBuffer mIndexBuffer;
        
    private float vertices[];
    private float textures[];
    private short indices[];
    
    private int textureId;
    private int[] buffers = new int[3];
	        
	public SkyDome(Context context) {
		
		init();
		
		mVertexBuffer = createFloatBuffer(vertices);
		mTextureBuffer = createFloatBuffer(textures);
		mIndexBuffer = createShortBuffer(indices);
		
		load(context);
	}
	
	public void load(Context context){
		textureId = loadTexture(context, R.drawable.sky6666);
		//textureId = TextureHelper.loadCompressedTexture(context, R.raw.sky_compressed);
		loadProgram(context, R.raw.vshader, R.raw.fshader);
		
		///
		GLES20.glGenBuffers(3, buffers, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices.length*4), mVertexBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (textures.length*4), mTextureBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[2]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, (indices.length*2), mIndexBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	private void init(){
		
		vertices = new float[3 * ((STACKS + 1) * (SLICES + 1))];
		textures = new float[2 * ((STACKS + 1) * (SLICES + 1))];
		indices = new short[3 * ((2 * STACKS - 1) * SLICES)];
		
		double d1 = (2.0f * Math.PI)/SLICES;
		double d2 = (Math.PI/(2.0f*STACKS));
		
		// build vertices and texture coordinates
		for(int i=0; i!=STACKS+1; ++i){
			for(int j=0; j!=SLICES+1; ++j){
				
				vertices[3 * ((SLICES+1)*i + j)]     = (float)(Const.RADIUS * Math.cos(d1 * j) * Math.cos(d2 * i));	// x
				vertices[3 * ((SLICES+1)*i + j) + 1] = (float)(Const.RADIUS * Math.sin(d2 * i)) + Const.HORIZONT_Y;	// y
				vertices[3 * ((SLICES+1)*i + j) + 2] = (float)(Const.RADIUS * Math.sin(d1 * j) * Math.cos(d2 * i));	// z
				
				textures[2 * ((SLICES+1)*i + j)]     = 1.0f - (float)j/SLICES;	// x
				textures[2 * ((SLICES+1)*i + j) + 1] = (float)i/STACKS;			// y
			}
		}
		
		// build indices
		for(int i=0; i!=STACKS-1; ++i){
			for(int j=0; j!=SLICES; ++j){
					
				// first triangle
				indices[6*(SLICES*i+j)]     = (short)((SLICES+1)*i + j);
				indices[6*(SLICES*i+j) + 1] = (short)((SLICES+1)*i + j + SLICES + 1);
				indices[6*(SLICES*i+j) + 2] = (short)((SLICES+1)*i + j + 1);
				// second triangle
				
				indices[6*(SLICES*i+j) + 3] = (short)((SLICES+1)*i + j + 1);
				indices[6*(SLICES*i+j) + 4] = (short)((SLICES+1)*i + j + SLICES + 1);
				indices[6*(SLICES*i+j) + 5] = (short)((SLICES+1)*i + j + SLICES + 2);
			}
		}
		
		// add last eight triangles
		
		int startIndex1 = SLICES * (STACKS-1) * 2;
		int startIndex2 = (SLICES+1) * (STACKS-1);
				
		for(int j=0; j!=SLICES; ++j){
			indices[3*(startIndex1+j)]     = (short)(startIndex2 + j);
			indices[3*(startIndex1+j) + 1] = (short)(startIndex2 + j + SLICES + 1);
			indices[3*(startIndex1+j) + 2] = (short)(startIndex2 + j + 1);
		}
		
	}
	
	// Draw the shape
	public void draw(float[] matrix) {
		GLES20.glUseProgram(program);
		
        GLES20.glFrontFace(GLES20.GL_CW);
        
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);
        
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

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[2]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, 0);
		//GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
