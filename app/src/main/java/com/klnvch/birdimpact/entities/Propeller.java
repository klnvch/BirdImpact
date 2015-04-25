package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.klnvch.birdimpact.R;

import android.content.Context;
import android.opengl.GLES20;

public class Propeller extends Entity{
	
	public static final float Z0 = -2.5f;
	public static final float RADIUS = 2.5f;
	private static final int SLICES = 20;
	private static final float[] whiteColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	private static final float[] grayColor = new float[]{0.75f, 0.75f, 0.75f, 0.5f};
	private static final float[] redColor = new float[]{1.0f, 0.0f, 0.0f, 0.75f};
	
	private int prev0;
	private int prev1;
	private float hit = 60;
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mColorBuffer;
    private ShortBuffer mIndexBuffer;
        
    private float vertices[];
    private float colors[];
    private short indices[];
    
    private int[] buffers = new int[2];
           
	public Propeller(Context context) {
		init();
		
		mVertexBuffer = createFloatBuffer(vertices);
		mColorBuffer = createFloatBuffer(colors);
		mIndexBuffer = createShortBuffer(indices);
		
		load(context);
	}
	
	public void load(Context context){
		loadProgram(context, R.raw.vshader_propeller, R.raw.fshader_propeller);
		
		///
		GLES20.glGenBuffers(2, buffers, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices.length*4), mVertexBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, (indices.length*2), mIndexBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);		
	}
	
	private void init(){
		
		vertices = new float[3 * (SLICES + 1)];
		colors = new float[4 * (SLICES + 1)];
		indices = new short[3 * SLICES];
		
		double a = (2.0f * Math.PI)/SLICES;
		
		// build vertices and colors coordinates
		vertices[0] = vertices[1] = 0.0f;
		vertices[2] = Z0;
		colors[0] = 0.75f;	//red
		colors[1] = 0.75f;	//blue
		colors[2] = 0.75f;	//green
		colors[3] = 0.75f;	//alpha
		for(int i=1; i!=SLICES+1; ++i){
				
				vertices[3 * i]     = (float)(RADIUS * Math.cos(a * i));	// x
				vertices[3 * i + 1] = (float)(RADIUS * Math.sin(a * i));	// y
				vertices[3 * i + 2] = Z0;									// z
				
				colors[4 * i]     = 0.75f;	//red
				colors[4 * i + 1] = 0.75f;	//blue
				colors[4 * i + 2] = 0.75f;	//green
				colors[4 * i + 3] = 0.5f;	//alpha
		}
		
		// build indices
		for(int i=0; i!=SLICES-1; ++i){
			indices[3 * i]     = 0;
			indices[3 * i + 1] = (short)(i + 1);
			indices[3 * i + 2] = (short)(i + 2);
		}
		indices[3 * (SLICES-1)]     = 0;
		indices[3 * (SLICES-1) + 1] = (short)SLICES;
		indices[3 * (SLICES-1) + 2] = 1;
		
	}
	
	private void changeColor(int i, float[] color){
		mColorBuffer.position(4*i);
		mColorBuffer.put(color);
		mColorBuffer.position(0);
	}
	
	public void hit(){
		hit = 2.0f;
	}
	
	public void update(float interval){
		// gray - white color
		changeColor(prev0, grayColor);		// restore to gray
		changeColor(prev1, grayColor);
		++prev0;
		++prev1;
		if(prev0 == SLICES)	prev0 = 1;
		if(prev1 == SLICES)	prev1 = 1;
		changeColor(prev0, whiteColor);		// set white
		changeColor(prev1, whiteColor);
		
		// blood on the propeller
		if(hit < 60){
			hit += 	20.0f*interval;
			for(int i=1; i!=SLICES; ++i){
				if(i % ((int)hit) == 0)	changeColor(i, redColor);
			}
		}
	}
	
	// Draw the shape
	public void draw(float[] matrix) {
		GLES20.glUseProgram(program);
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);		
        
        GLES20.glFrontFace(GLES20.GL_CCW);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        //GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
		//GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
			
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, COLOR), 4, GLES20.GL_FLOAT, false, 0, mColorBuffer);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, COLOR));

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, 0);
		//GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
