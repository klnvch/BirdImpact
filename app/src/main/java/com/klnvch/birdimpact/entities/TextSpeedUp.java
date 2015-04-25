package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;

import com.klnvch.birdimpact.R;

import android.content.Context;
import android.opengl.GLES20;

public class TextSpeedUp extends Entity {
	
	private int n = 0;
	private float flashing = 0.0f;
	
	private int textureId;
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;
        
    private float[] vertices;
    private float[] textures;
    
    private int[] buffers = new int[2];
	
	public TextSpeedUp(Context context) {
		init(context);
		load(context);
	}
	
	public void load(Context context){
    	textureId = loadTexture(context, R.drawable.letters_red);
    	loadProgram(context, R.raw.vshader_text, R.raw.fshader_text);
    	
    	GLES20.glGenBuffers(2, buffers, 0);
    	
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices.length*4), mVertexBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (textures.length*4), mTextureBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
	
	private void init(Context context){
		final float width = 32.0f;
    	final float height = 7.0f;
    	
    	final float dx = 0.1f;
    	final float dy = 0.2f;
    	
    	final float y0 = -0.8f;
		
    	String line = context.getString(R.string.main_speed_up);
    	n = line.length();
    	
		vertices = new float[3*4*n];
		textures = new float[3*4*n];
		
		for(int i=0; i!=n; ++i){
			
			// first vertex
			vertices[i*12 + 0] = -(dx*n/2.0f) + i*dx;		// x
			vertices[i*12 + 1] = y0 - dy/2.0f;			// y
			vertices[i*12 + 2] = 0.0f;			// z
			// second vertex
			vertices[i*12 + 3] = -(dx*n/2.0f) + (i+1)*dx;	// x
			vertices[i*12 + 4] = y0 - dy/2.0f;			// y
			vertices[i*12 + 5] = 0.0f;			// z
			// forth vertex
			vertices[i*12 + 6] = -(dx*n/2.0f) + i*dx;		// x
			vertices[i*12 + 7] = y0 + dy/2.0f;		// y
			vertices[i*12 + 8] = 0.0f;			// z
			// third vertex
			vertices[i*12 + 9] = -(dx*n/2.0f) + (i+1)*dx;	// x
			vertices[i*12 + 10] = y0 + dy/2.0f;		// y
			vertices[i*12 + 11] = 0.0f;			// z
			
			float letter = line.charAt(i);
			if(letter >= '0' && letter <= '9'){
				letter -= '0';
				
				// first vertex
				textures[i*8 + 0] = letter/width;
				textures[i*8 + 1] = 1.0f/height;
				// second vertex
				textures[i*8 + 2] = (letter+1)/width;
				textures[i*8 + 3] = 1.0f/height;
				// forth vertex
				textures[i*8 + 4] = letter/width;
				textures[i*8 + 5] = 0.0f/height;
				// third vertex
				textures[i*8 + 6] = (letter+1)/width;
				textures[i*8 + 7] = 0.0f/height;
			}else if(letter >= 'A' && letter <= 'Z'){
				letter -= 'A';
				
				// first vertex
				textures[i*8 + 0] = letter/width;
				textures[i*8 + 1] = 2.0f/height;
				// second vertex
				textures[i*8 + 2] = (letter+1)/width;
				textures[i*8 + 3] = 2.0f/height;
				// forth vertex
				textures[i*8 + 4] = letter/width;
				textures[i*8 + 5] = 1.0f/height;
				// third vertex
				textures[i*8 + 6] = (letter+1)/width;
				textures[i*8 + 7] = 1.0f/height;
			}else if(letter >= 'a' && letter <= 'z'){
				letter -= 'a';
				
				// first vertex
				textures[i*8 + 0] = letter/width;
				textures[i*8 + 1] = 3.0f/height;
				// second vertex
				textures[i*8 + 2] = (letter+1)/width;
				textures[i*8 + 3] = 3.0f/height;
				// forth vertex
				textures[i*8 + 4] = letter/width;
				textures[i*8 + 5] = 2.0f/height;
				// third vertex
				textures[i*8 + 6] = (letter+1)/width;
				textures[i*8 + 7] = 2.0f/height;
			}else if(letter >= 'А' && letter <= 'Я'){
				letter -= 'А';
				
				// first vertex
				textures[i*8 + 0] = letter/width;
				textures[i*8 + 1] = 6.0f/height;
				// second vertex
				textures[i*8 + 2] = (letter+1)/width;
				textures[i*8 + 3] = 6.0f/height;
				// forth vertex
				textures[i*8 + 4] = letter/width;
				textures[i*8 + 5] = 5.0f/height;
				// third vertex
				textures[i*8 + 6] = (letter+1)/width;
				textures[i*8 + 7] = 5.0f/height;
			}else if(letter >= 'а' && letter <= 'я'){
				letter -= 'а';
				
				// first vertex
				textures[i*8 + 0] = letter/width;
				textures[i*8 + 1] = 7.0f/height;
				// second vertex
				textures[i*8 + 2] = (letter+1)/width;
				textures[i*8 + 3] = 7.0f/height;
				// forth vertex
				textures[i*8 + 4] = letter/width;
				textures[i*8 + 5] = 6.0f/height;
				// third vertex
				textures[i*8 + 6] = (letter+1)/width;
				textures[i*8 + 7] = 6.0f/height;
			}else if(letter == '.'){
				// first vertex
				textures[i*8 + 0] = 29.0f/width;
				textures[i*8 + 1] = 2.0f/height;
				// second vertex
				textures[i*8 + 2] = 30.0f/width;
				textures[i*8 + 3] = 2.0f/height;
				// forth vertex
				textures[i*8 + 4] = 29.0f/width;
				textures[i*8 + 5] = 1.0f/height;
				// third vertex
				textures[i*8 + 6] = 30.0f/width;
				textures[i*8 + 7] = 1.0f/height;
			}else if(letter == ' '){
			}else if(letter == ','){
				// first vertex
				textures[i*8 + 0] = 30.0f/width;
				textures[i*8 + 1] = 2.0f/height;
				// second vertex
				textures[i*8 + 2] = 31.0f/width;
				textures[i*8 + 3] = 2.0f/height;
				// forth vertex
				textures[i*8 + 4] = 30.0f/width;
				textures[i*8 + 5] = 1.0f/height;
				// third vertex
				textures[i*8 + 6] = 31.0f/width;
				textures[i*8 + 7] = 1.0f/height;
			}else if(letter == '-'){
				// first vertex
				textures[i*8 + 0] = 3.0f/width;
				textures[i*8 + 1] = 2.0f/height;
				// second vertex
				textures[i*8 + 2] = 4.0f/width;
				textures[i*8 + 3] = 2.0f/height;
				// forth vertex
				textures[i*8 + 4] = 3.0f/width;
				textures[i*8 + 5] = 1.0f/height;
				// third vertex
				textures[i*8 + 6] = 4.0f/width;
				textures[i*8 + 7] = 1.0f/height;
			}else if(letter == ':'){
				// first vertex
				textures[i*8 + 0] = 31.0f/width;
				textures[i*8 + 1] = 2.0f/height;
				// second vertex
				textures[i*8 + 2] = 32.0f/width;
				textures[i*8 + 3] = 2.0f/height;
				// forth vertex
				textures[i*8 + 4] = 31.0f/width;
				textures[i*8 + 5] = 1.0f/height;
				// third vertex
				textures[i*8 + 6] = 32.0f/width;
				textures[i*8 + 7] = 1.0f/height;
			}else if(letter == '!'){
				// first vertex
				textures[i*8 + 0] = 28.0f/width;
				textures[i*8 + 1] = 2.0f/height;
				// second vertex
				textures[i*8 + 2] = 29.0f/width;
				textures[i*8 + 3] = 2.0f/height;
				// forth vertex
				textures[i*8 + 4] = 28.0f/width;
				textures[i*8 + 5] = 1.0f/height;
				// third vertex
				textures[i*8 + 6] = 29.0f/width;
				textures[i*8 + 7] = 1.0f/height;
			}else if(letter == '%'){
				// first vertex
				textures[i*8 + 0] = 31.0f/width;
				textures[i*8 + 1] = 1.0f/height;
				// second vertex
				textures[i*8 + 2] = 32.0f/width;
				textures[i*8 + 3] = 1.0f/height;
				// forth vertex
				textures[i*8 + 4] = 31.0f/width;
				textures[i*8 + 5] = 0.0f/height;
				// third vertex
				textures[i*8 + 6] = 32.0f/width;
				textures[i*8 + 7] = 0.0f/height;
			}else if("ĄĆĘŁŃÓŚŹŻ".indexOf(line.charAt(i)) != -1){
				float index = "ĄĆĘŁŃÓŚŹŻ".indexOf(line.charAt(i));
				// first vertex
				textures[i*8 + 0] = index/width;
				textures[i*8 + 1] = 4.0f/height;
				// second vertex
				textures[i*8 + 2] = (index+1.0f)/width;
				textures[i*8 + 3] = 4.0f/height;
				// forth vertex
				textures[i*8 + 4] = index/width;
				textures[i*8 + 5] = 3.0f/height;
				// third vertex
				textures[i*8 + 6] = (index+1.0f)/width;
				textures[i*8 + 7] = 3.0f/height;
			}else if("ąćęłńóśźż".indexOf(line.charAt(i)) != -1){
				float index = "ąćęłńóśźż".indexOf(line.charAt(i));
				// first vertex
				textures[i*8 + 0] = index/width;
				textures[i*8 + 1] = 5.0f/height;
				// second vertex
				textures[i*8 + 2] = (index+1.0f)/width;
				textures[i*8 + 3] = 5.0f/height;
				// forth vertex
				textures[i*8 + 4] = index/width;
				textures[i*8 + 5] = 4.0f/height;
				// third vertex
				textures[i*8 + 6] = (index+1.0f)/width;
				textures[i*8 + 7] = 4.0f/height;
			}else if(letter == 'Ё'){
				// first vertex
				textures[i*8 + 0] = 31.0f/width;
				textures[i*8 + 1] = 4.0f/height;
				// second vertex
				textures[i*8 + 2] = 32.0f/width;
				textures[i*8 + 3] = 4.0f/height;
				// forth vertex
				textures[i*8 + 4] = 31.0f/width;
				textures[i*8 + 5] = 3.0f/height;
				// third vertex
				textures[i*8 + 6] = 32.0f/width;
				textures[i*8 + 7] = 3.0f/height;
			}else if(letter == 'ё'){
				// first vertex
				textures[i*8 + 0] = 31.0f/width;
				textures[i*8 + 1] = 5.0f/height;
				// second vertex
				textures[i*8 + 2] = 32.0f/width;
				textures[i*8 + 3] = 5.0f/height;
				// forth vertex
				textures[i*8 + 4] = 31.0f/width;
				textures[i*8 + 5] = 4.0f/height;
				// third vertex
				textures[i*8 + 6] = 32.0f/width;
				textures[i*8 + 7] = 4.0f/height;
			}
			
		}
		
		mVertexBuffer = createFloatBuffer(vertices);
		mTextureBuffer = createFloatBuffer(textures);
	}
	
	public void start(){
		flashing = 1.0f;
	}
	
	public void update(float timeInterval){
		flashing -= 0.5f*timeInterval;
	}
	
	public void draw() {
		
		if(flashing <= 0.0f)	return;
		
		GLES20.glUseProgram(program);
		
		GLES20.glUniform1f(GLES20.glGetUniformLocation(program, ALPHA), flashing);
		
		GLES20.glFrontFace(GLES20.GL_CCW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
			
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glUniform1i(GLES20.glGetUniformLocation(program, TEXTURE), 0);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, TEXTURE_COORD), 2, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, TEXTURE_COORD));

		for(int i=0; i!=n; ++i){
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i*4, 4);
		}
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
}
