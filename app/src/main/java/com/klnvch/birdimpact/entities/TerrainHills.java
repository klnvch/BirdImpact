package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.utils.ObjLoader;

import android.content.Context;
import android.opengl.GLES20;


//	+-------------------+---------------+---------------+
//	|					|				|				|
//	|	(-400, 400)		|	(0, 400)	|	(400, 400)	|
//	|					|				|				|
//	+-------------------+---------------+---------------+
//	|					|				|				|
//	|	(-400,   0)		|	(0,   0)	|	(400, 0)	|
//	|					|				|				|
//	+-------------------+---------------+---------------+
//	|					|				|				|
//	|	(-400, -400)	|	(0, -400)	|	(400, -400)	|
//	|					|				|				|
//	+-------------------+---------------+---------------+
public class TerrainHills extends Terrain{
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;
    private ShortBuffer mIndexBuffer;
    private float vertices[];
    private float textures[];
    private short indices[];
    
    private int textureId;
    private int[] buffers = new int[3];
	        
	public TerrainHills(Context context) {
		
		super(context, 3, 700.0f);
		
		loadFromFile(context, R.raw.terrain);
		
		mVertexBuffer = createFloatBuffer(vertices);
		mTextureBuffer = createFloatBuffer(textures);
		mIndexBuffer = createShortBuffer(indices);
		
		load(context);
	}
	
	public void load(Context context) {
		textureId = loadTexture(context, R.drawable.terrain);
		//textureId = TextureHelper.loadCompressedTexture(context, R.raw.terrain_compressed);
		loadProgram(context, R.raw.vshader, R.raw.fshader);
		
		
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
	
	private void move(float dx, float dz){
		x += dx;
		z += dz;
	}
	
	public void update(float angleSky, float timeInterval){
		
		float dx = (float)(- Plane.speed * timeInterval * Math.sin((2.0*Math.PI*angleSky)/360.0));
	    float dz = (float)(  Plane.speed * timeInterval * Math.cos((2.0*Math.PI*angleSky)/360.0));
		
	    x += dx;
		z += dz;
		
		// replace by top
		if(z > SIZE){
			move(0, -SIZE);
		}
		// replace by bottom
		if(z < -SIZE){
			move(0, SIZE);
		}
		// replace by left
		if(x > SIZE){
			move(-SIZE, 0);
		}
		// replace by right
		if(x < -SIZE){
			move(SIZE, 0);
		}
	}
	
	public float getHeight(float x, float z){
		return 0.0f;
	}
	
	public void draw(float[] matrix, int i, int j) {
		
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);
		
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, 0);
	}
	
	@Override
	public void checkVisibility(float angleY) {
	}

	@Override
	public void preDraw() {
		GLES20.glUseProgram(program);
		
		GLES20.glFrontFace(GLES20.GL_CCW);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glUniform1i(GLES20.glGetUniformLocation(program, TEXTURE), 0);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
				
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, TEXTURE_COORD), 2, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, TEXTURE_COORD));

		// Draw with indices
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[2]);
	}

	@Override
	public void postDraw() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	@Override
	public boolean isVisible(int i, int j) {
		return true;
	}
}
