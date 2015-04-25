package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.utils.ObjLoader;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class Meet extends Entity{
	
	public static final int N = 10;		// number of pieces
	private static final float[] direction0 = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
	
	private int nextMeet = 0;
	public float[] x = new float[N];
	public float[] y = new float[N];
	public float[] z = new float[N];
	public float[] a = new float[N];
	public float[] dx = new float[N];
	public float[] dy = new float[N];
	public float[] dz = new float[N];
	
	// temporary buffers
    private float[] rotationMatrix = new float[16];
    private float[] direction1 = new float[4];
    private float[] explosion1 = new float[4];
	
	// Constructor
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;
    private ShortBuffer mIndexBuffer;
        
    private float vertices[];
    private float textures[];
    private short indices[];
    
    // Initialized in load
    private int textureId;
    private int[] buffers = new int[3];
    
	public Meet(Context context) {
		loadFromFile(context, R.raw.meet);
		
		mVertexBuffer = createFloatBuffer(vertices);
		mTextureBuffer = createFloatBuffer(textures);
		mIndexBuffer = createShortBuffer(indices);
		
		load(context);
	}
	
	public void load(Context context){
		textureId = loadTexture(context, R.drawable.meet);
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
	
	private void loadFromFile(Context context, int file){
		ObjLoader.loadObj2(context, file);
		vertices = ObjLoader.vertices;
		textures = ObjLoader.textures;
		indices = ObjLoader.indices;
	}
	
	public void setExplosion(float[] position, float angle){
		Matrix.setRotateM(rotationMatrix, 0, -angle, 0.0f, 1.0f, 0.0f);
	    Matrix.multiplyMV(direction1, 0, rotationMatrix, 0, direction0, 0);
		Matrix.multiplyMV(explosion1, 0, rotationMatrix, 0, position, 0);
		
		addPiecesOfMeet(direction1, explosion1);
	}
	
	private void addPiecesOfMeet(float[] direction, float[] position){
		
		
		// #1
		x[nextMeet] = position[0];
		y[nextMeet] = position[1];
		z[nextMeet] = position[2];
		
		a[nextMeet] = 0.0f;

		Matrix.setRotateEulerM(rotationMatrix, 0, 10, 0, 0);
		Matrix.multiplyMV(direction, 0, rotationMatrix, 0, direction, 0);
		
		dx[nextMeet] = direction[0]/3.0f;
		dy[nextMeet] = direction[1]/3.0f;
		dz[nextMeet] = direction[2]/3.0f;
		
		++nextMeet;
		if(nextMeet == N)	nextMeet = 0;
		
		// #2
		x[nextMeet] = position[0];
		y[nextMeet] = position[1];
		z[nextMeet] = position[2];
		
		a[nextMeet] = 64.0f;

		Matrix.setRotateEulerM(rotationMatrix, 0, -15, 0, 0);
		Matrix.multiplyMV(direction, 0, rotationMatrix, 0, direction, 0);
				
		dx[nextMeet] = direction[0]/2.0f;
		dy[nextMeet] = direction[1]/2.0f;
		dz[nextMeet] = direction[2]/2.0f;
				
		++nextMeet;
		if(nextMeet == N)	nextMeet = 0;
	}
	
	public void update(float interval){
		for(int i=0; i!=N; ++i){
			x[i] += dx[i] * 30.0f * interval;
			y[i] += dy[i] * 30.0f * interval;
			z[i] += dz[i] * 30.0f * interval;
			
			a[i] += 8.0f;
		}
	}
	
	public void draw(int i, float[] matrix) {
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

		// Draw with indices
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[2]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, 0);
		//GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        
	}
}
