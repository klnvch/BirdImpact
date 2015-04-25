package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.klnvch.birdimpact.Const;
import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.utils.ObjLoader;

import android.content.Context;
import android.opengl.GLES20;
import android.os.Bundle;

public class Plane extends Entity implements Restorable{
	
	public static final float LENGTH_MIDDLE_WING = 6.25f;
	public static final float Y_TOP_MIDDLE_WING = 0.0f;
	public static final float Y_BOTTOM_MIDDLE_WING = 0.5f;
	public static final float Z_FRONT_MIDDLE_WING = 0.0f;
	public static final float LENGHT_CORNER_WING = 10.0f;
	public static final float Z_FRONT_CORNER_WING = 0.5f;
	
	public static final float FRONT_X = 0.0f;
	public static final float FRONT_Y = -0.25f;
	public static final float FRONT_Z = -2.5f;
	
	public static final float WING_X = 10.0f;
	public static final float WING_Y = 0.75f;
	public static final float WING_Z = 0.5f;
	
	// to store
	public static float speed = Const.SPEED_PLANE;
	public static float turn_speed = Const.SPEED_TURN;
	
	// to forget
    private float[] explTime = new float[4];
    private float[] explPos = new float[4*4];
	
    // to draw
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;
    private ShortBuffer mIndexBuffer;
        
    private float vertices[];
    private float textures[];
    private short indices[];

    private int textureId;
    private int[] buffers = new int[3];
	        
	public Plane(Context context) {
		
		speed = Const.SPEED_PLANE;
		turn_speed = Const.SPEED_TURN;
		
		loadFromFile(context, R.raw.plane1);
		
		mVertexBuffer = createFloatBuffer(vertices);
		mTextureBuffer = createFloatBuffer(textures);
		mIndexBuffer = createShortBuffer(indices);
		
		load(context);
	}
	
	public void load(Context context){
		textureId = loadTexture(context, R.drawable.plane1);
		//textureId = TextureHelper.loadCompressedTexture(context, R.raw.plane_compressed);
		loadProgram(context, R.raw.vshader_plane, R.raw.fshader_plane);
		
		
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
	
	public void setExplosion(float[] position){
		int k = 0;
		for(int i=1; i!=4; ++i){
			if(explTime[k] < explTime[i]){
				k = i;
			}
		}
		explTime[k] = 0.0f;
		
		explPos[k*4 + 0] = position[0];
		explPos[k*4 + 1] = position[1];
		explPos[k*4 + 2] = position[2];
	}
	
	public void update(float timeInterval){
		explTime[0] += timeInterval;
		explTime[1] += timeInterval;
		explTime[2] += timeInterval;
		explTime[3] += timeInterval;
	}
	
	public void draw(float[] matrix) {
		GLES20.glUseProgram(program);
		
		GLES20.glFrontFace(GLES20.GL_CCW);
		
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);
		GLES20.glUniform4fv(GLES20.glGetUniformLocation(program, EXPLOSIONS), 4, explPos, 0);
		GLES20.glUniform1fv(GLES20.glGetUniformLocation(program, ELAPSED_TIME), 4, explTime, 0);
		
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

	@Override
	public void onSave(Bundle bundle) {
		
		bundle.putFloat("Plane.speed", Plane.speed);
		bundle.putFloat("Plane.turn_speed", Plane.turn_speed);
	}

	@Override
	public void onRestore(Bundle bundle) {
		
		Plane.speed = bundle.getFloat("Plane.speed", 0);
		Plane.turn_speed = bundle.getFloat("Plane.turn_speed", 0);
	}
}
