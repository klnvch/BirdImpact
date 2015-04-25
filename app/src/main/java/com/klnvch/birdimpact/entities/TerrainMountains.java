package com.klnvch.birdimpact.entities;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import com.klnvch.birdimpact.R;
import com.klnvch.birdimpact.Const;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;

public class TerrainMountains extends Terrain{
	
	private static final int PLAIN = 1;
	private static final int MOUNTAIN = 2;
	
	public static final float HEIGHT = 40.0f;
	
	private static final int HEIGHTMAP_SIZE = 32;
	
	private int[][] type = new int[N+2][N+2];
	private boolean[][] isVisible = new boolean[2*N][2*N];
	private Random r = new Random();
	
	private FloatBuffer[] mVertexBuffer = new FloatBuffer[13];
	private FloatBuffer[] mTextureBuffer = new FloatBuffer[13];
    private ShortBuffer[] mIndexBuffer = new ShortBuffer[13];
    private float vertices[][] = new float[13][];
    private float textures[][] = new float[13][];
    private short indices[][] = new short[13][];
    
    private int textureId;
    
    private int[] buffers = new int[39];
	        
	public TerrainMountains(Context context) {
		
		super(context, 9, 50.0f);
		
		//init
		for(int i=0; i!=N+2; ++i){
			for(int j=0; j!=N+2; ++j){
				type[i][j] = r.nextInt(2) + 1;
			}
		}
		
		type[N/2+1][N/2+1] = PLAIN;

		// load height maps
		final int size = HEIGHTMAP_SIZE;
		
		loadHeightmapFromFile(context, R.drawable.hm0, 0, 2, 0, 0);
		loadHeightmapFromFile(context, R.drawable.hm10, 1, size, 1, 0);
		loadHeightmapFromFile(context, R.drawable.hm11, 2, size, 1, 1);
		loadHeightmapFromFile(context, R.drawable.hm12, 3, size, 1, 2);
		loadHeightmapFromFile(context, R.drawable.hm13, 4, size, 1, 3);
		loadHeightmapFromFile(context, R.drawable.hm20, 5, size, 2, 0);
		loadHeightmapFromFile(context, R.drawable.hm21, 6, size, 2, 1);
		loadHeightmapFromFile(context, R.drawable.hm22, 7, size, 2, 2);
		loadHeightmapFromFile(context, R.drawable.hm23, 8, size, 2, 3);
		loadHeightmapFromFile(context, R.drawable.hm30, 9, size, 3, 0);
		loadHeightmapFromFile(context, R.drawable.hm31, 10, size, 3, 1);
		loadHeightmapFromFile(context, R.drawable.hm32, 11, size, 3, 2);
		loadHeightmapFromFile(context, R.drawable.hm33, 12, size, 3, 3);
		
		for(int i=0; i!=13; ++i){
			mVertexBuffer[i] = createFloatBuffer(vertices[i]);
			mTextureBuffer[i] = createFloatBuffer(textures[i]);
			mIndexBuffer[i] = createShortBuffer(indices[i]);
		}
		
		load(context);
	}
	
	public void load(Context context) {
		textureId = loadTexture(context, R.drawable.terrain_texture);
		
		loadProgram(context, R.raw.vshader, R.raw.fshader);
		
		GLES20.glGenBuffers(39, buffers, 0);
		
		for(int i=0; i!=13; ++i){
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[i*3]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (mVertexBuffer[i].capacity()*4), mVertexBuffer[i], GLES20.GL_STATIC_DRAW);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[i*3+1]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (mTextureBuffer[i].capacity()*4), mTextureBuffer[i], GLES20.GL_STATIC_DRAW);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[i*3+2]);
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, (mIndexBuffer[i].capacity()*2), mIndexBuffer[i], GLES20.GL_STATIC_DRAW);
		}
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	private void loadHeightmapFromFile(Context context, int file, int i, int size, int ti, int tj){
		
		Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(file)).getBitmap();
		
		int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        int[] pixels = new int[width * height];                
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();
        
        // create vertices
        vertices[i] = new float[size * size * 3];
        // create textures
        textures[i] = new float[size * size * 2];
        
        int offsetV = 0;
        int offsetT = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                // The height map will lie flat on the XZ plane and centered
                // around (0, 0), with the bitmap width mapped to X and the
                // bitmap height mapped to Z, and Y representing the height. We
                // assume the height map is gray scale, and use the value of the
                // red color to determine the height.
                final float xPosition = ((float)col / (float)(size - 1)) - 0.5f;
                
                int i1 = (int)Math.floor((row*(width-1))/(double)(size-1));
                int i2 = (int)Math.ceil((row*(width-1))/(double)(size-1));
                int i3 = (int)Math.floor((col*(height-1))/(double)(size-1));
                int i4 = (int)Math.ceil((col*(height-1))/(double)(size-1));
                
                float h1 = (float)Color.red(pixels[(i1 * size) + i3]) / (float)255;
                float h2 = (float)Color.red(pixels[(i1 * size) + i4]) / (float)255;
                float h3 = (float)Color.red(pixels[(i2 * size) + i3]) / (float)255;
                float h4 = (float)Color.red(pixels[(i2 * size) + i4]) / (float)255;
                		
                final float yPosition = (h1+h2+h3+h4)/4.0f;
                //final float yPosition = (float)Color.red(pixels[(k*row * size) + k*col]) / (float)255;
                final float zPosition = ((float)row / (float)(size - 1)) - 0.5f;                                                
                
                vertices[i][offsetV++] = xPosition;
                vertices[i][offsetV++] = yPosition;
                vertices[i][offsetV++] = zPosition;
                
                textures[i][offsetT++] = col/(float)((size-1)*4.0f) + tj/4.0f;
                textures[i][offsetT++] = row/(float)((size-1)*4.0f) + ti/4.0f;
            }
        }
        
        // create indices
        indices[i] = new short[(size - 1) * (size - 1) * 2 * 3];
        int offset = 0;
            
        for (int row = 0; row < size - 1; row++) {
            for (int col = 0; col < size - 1; col++) {
                // Note: The (short) cast will end up underflowing the number
                // into the negative range if it doesn't fit, which gives us the
                // right unsigned number for OpenGL due to two's complement.
                // This will work so long as the heightmap contains 65536 pixels
                // or less.
                short topLeftIndexNum = (short) (row * size + col);
                short topRightIndexNum = (short) (row * size + col + 1);
                short bottomLeftIndexNum = (short) ((row + 1) * size + col);
                short bottomRightIndexNum = (short) ((row + 1) * size + col + 1);                                
                
                // Write out two triangles.
                indices[i][offset++] = topLeftIndexNum;
                indices[i][offset++] = bottomLeftIndexNum;
                indices[i][offset++] = topRightIndexNum;
                
                indices[i][offset++] = topRightIndexNum;
                indices[i][offset++] = bottomLeftIndexNum;
                indices[i][offset++] = bottomRightIndexNum;
            }
        }
	}
	
	private void move(float dx, float dz){
		x += dx;
		z += dz;
	}
	
	public void update(float angleSky, float timeInterval){
		
		float dx = (float)(- Plane.speed * timeInterval * Math.sin((2.0*Math.PI*angleSky)/360.0));
	    float dz = (float)(  Plane.speed * timeInterval * Math.cos((2.0*Math.PI*angleSky)/360.0));
		
		move(dx, dz);
		
		// replace by top and create new cells at the top
	 	if(z > 2.0f*SIZE){
	 		move(0, -2.0f*SIZE);

	 		for(int i=N+1; i!=0; --i){
	 			for(int j=0; j!=N+2; ++j){
	 				type[i][j] = type[i-1][j];
	 			}
	 		}
	 		for(int i=0; i!=N+2; ++i){
	 			type[0][i] = r.nextInt(2) + 1;
	 		}
	 	}
	 	// replace by bottom
	 	if(z < -2.0f*SIZE){
	 		move(0, 2.0f*SIZE);
	 		
	 		for(int i=0; i!=N+1; ++i){
	 			for(int j=0; j!=N+2; ++j){
	 				type[i][j] = type[i+1][j];
	 			}
	 		}
	 		for(int i=0; i!=N+2; ++i){
	 			type[N+1][i] = r.nextInt(2) + 1;
	 		}
	 	}
	 	// replace by left
	 	if(x > 2.0f*SIZE){
	 		move(-2.0f*SIZE, 0);
	 		
	 		for(int i=0; i!=N+2; ++i){
	 			for(int j=N+1; j!=0; --j){
	 				type[i][j] = type[i][j-1];
	 			}
	 		}
	 		for(int i=0; i!=N+2; ++i){
	 			type[i][0] = r.nextInt(2) + 1;
	 		}
	 	}
	 	// replace by right
	 	if(x < -2.0f*SIZE){
	 		move(2.0f*SIZE, 0);

	 		for(int i=0; i!=N+2; ++i){
	 			for(int j=0; j!=N+1; ++j){
	 				type[i][j] = type[i][j+1];
	 			}
	 		}
	 		for(int i=0; i!=N+2; ++i){
	 			type[i][N+1] = r.nextInt(2) + 1;
	 		}
	 	}
	}
	/**
	 * 
	 * returns height of the terrain in the specified location
	 * 
	 * @param x	x-location after rotating on angle Y
	 * @param z z-location after rotating on angle Y
	 * @return	height of the terrain
	 */
	public float getHeight(final float x, final float z){
		
		float posX = x - this.x;
		float posZ = z - this.z;
		
		// find what cell it is
		final int i = (int)(posZ / (2.0f*SIZE) + N/2.0f);
		final int j = (int)(posX / (2.0f*SIZE) + N/2.0f);
		
		// return 0 if it is plain
		if(i >= N || i < 0 || i >= N || i < 0){
			Log.d("dbg", "(" + i + "," + j + ")-(" + x + "," + z + ")-(" + this.x + "," + this.z + ")");
			return 0;
		}
		if(type[i+1][j+1] == PLAIN)	return 0;
		
		// localize positions to the cell
		posX += (N/2 - j) * 2.0f * SIZE;
		posZ += (N/2 - i) * 2.0f * SIZE;
		
		int k = 0;
		
		if(posZ >= 0){
			if(posX >= 0){
				k = getMountainType(2*i+1, 2*j+1);
				posX -= SIZE / 2.0f;
				posZ -= SIZE / 2.0f;
			}else{
				k = getMountainType(2*i+1, 2*j);
				posX += SIZE / 2.0f;
				posZ -= SIZE / 2.0f;
			}
		}else{
			if(posX >= 0){
				k = getMountainType(2*i, 2*j+1);
				posX -= SIZE / 2.0f;
				posZ += SIZE / 2.0f;
			}else{
				k = getMountainType(2*i, 2*j);
				posX += SIZE / 2.0f;
				posZ += SIZE / 2.0f;
			}
		}
		
		// scale position
		posX /= SIZE;
		posZ /= SIZE;
		
		// find position in the height map array
		final int i_terrain = (int)((posZ + 0.5f)* HEIGHTMAP_SIZE);
		final int j_terrain = (int)((posX + 0.5f)* HEIGHTMAP_SIZE);
		final int p = 3*(i_terrain*HEIGHTMAP_SIZE + j_terrain) + 1;
		final float height = vertices[k][p];
		
		//Log.d("dbg", "height: " + height + " (" + i_terrain + "," + j_terrain + ")");
		
		return height * HEIGHT;
	}
	
	private int getMountainType(int i, int j){
		int k = 0;
		
		int i_global = i/2 + 1;
		int i_local = i%2;
		int j_global = j/2 + 1;
		int j_local = j%2;
		
		if(type[i_global][j_global] == 2){	// mountain
			if(i_local == 0 && j_local == 0){	// left top
				
				if(type[i_global-1][j_global] == 2 && type[i_global][j_global-1] == 2){
					k = 9;
				}else if(type[i_global-1][j_global] == MOUNTAIN && type[i_global][j_global-1] == PLAIN){
					k = 6;
				}else if(type[i_global-1][j_global] == PLAIN && type[i_global][j_global-1] == MOUNTAIN){
					k = 5;
				}else if(type[i_global-1][j_global] == PLAIN && type[i_global][j_global-1] == PLAIN){
					k = 1;
				}
				
			}else if(i_local == 1 && j_local == 0){	// left bottom
				
				if(type[i_global+1][j_global] == MOUNTAIN && type[i_global][j_global-1] == MOUNTAIN){
					k = 10;
				}else if(type[i_global+1][j_global] == PLAIN && type[i_global][j_global-1] == MOUNTAIN){
					k = 7;
				}else if(type[i_global+1][j_global] == MOUNTAIN && type[i_global][j_global-1] == PLAIN){
					k = 6;
				}else if(type[i_global+1][j_global] == PLAIN && type[i_global][j_global-1] == PLAIN){
					k = 2;
				}
				
			}else if(i_local == 1 && j_local == 1){	// bottom right
				if(type[i_global+1][j_global] == MOUNTAIN && type[i_global][j_global+1] == MOUNTAIN){
					k = 11;
				}else if(type[i_global+1][j_global] == PLAIN && type[i_global][j_global+1] == MOUNTAIN){
					k = 7;
				}else if(type[i_global+1][j_global] == 2 && type[i_global][j_global+1] == 1){
					k = 8;
				}else if(type[i_global+1][j_global] == PLAIN && type[i_global][j_global+1] == PLAIN){
					k = 3;
				}
			}else if(i_local == 0 && j_local == 1){	// right top
				
				if(type[i_global-1][j_global] == 2 && type[i_global][j_global+1] == 2){
					k = 12;
				}else if(type[i_global-1][j_global] == 2 && type[i_global][j_global+1] == 1){
					k = 8;
				}else if(type[i_global-1][j_global] == PLAIN && type[i_global][j_global+1] == MOUNTAIN){
					k = 5;
				}else if(type[i_global-1][j_global] == PLAIN && type[i_global][j_global+1] == PLAIN){
					k = 4;
				}
			}
		}
		return k;
	}
	
	public void checkVisibility(final float angleY){
		
		final float sin = (float)Math.sin((Math.PI*angleY)/180.0);
		final float cos = (float)Math.cos((Math.PI*angleY)/180.0);
		
		//int count = 0;
		
		for(int i=0; i!=2*N; ++i){
			for(int j=0; j!=2*N; ++j){
				
				isVisible[i][j] = false;
				
				final float centerX = (0.5f - N + j)*SIZE + x;
				final float centerZ = (0.5f - N + i)*SIZE + z;
				
				// corner 1
				if(isInside(centerX + SIZE/2.0f, centerZ + SIZE/2.0f, sin, cos)){
					isVisible[i][j] = true;
					//++count;
					continue;
				}
				// corner 2
				if(isInside(centerX - SIZE/2.0f, centerZ + SIZE/2.0f, sin, cos)){
					isVisible[i][j] = true;
					//++count;
					continue;
				}
				// corner 3
				if(isInside(centerX + SIZE/2.0f, centerZ - SIZE/2.0f, sin, cos)){
					isVisible[i][j] = true;
					//++count;
					continue;
				}
				// corner 4
				if(isInside(centerX - SIZE/2.0f, centerZ - SIZE/2.0f, sin, cos)){
					isVisible[i][j] = true;
					//++count;
					continue;
				}
				
				
			}
		}
		
		//Log.d("dbg", "visible " + count + " from " + 9*9*4);
	}
	
	private boolean isInside(final float centerX, final float centerZ, final float sin, final float cos){
		
		final float posZ = -centerX * sin + centerZ * cos;

		if(posZ < 30.0f){
			final float posX = centerX * cos + centerZ * sin;
			if((-posZ + 30.0f) > Math.abs(posX)){
				final float distance = posX * posX + posZ * posZ;
				if(distance < Const.RADIUS*Const.RADIUS){
					return true;
				}
			}
		}
		return false;
	}
	
	public void preDraw(){
		GLES20.glUseProgram(program);
		
		GLES20.glFrontFace(GLES20.GL_CCW);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glUniform1i(GLES20.glGetUniformLocation(program, TEXTURE), 0);
	}
	
	public void draw(final float[] matrix, final int i, final int j) {
		
		int k = getMountainType(i, j);
		
		GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, MATRIX), 1, false, matrix, 0);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[3*k]);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, POSITION), 3, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, POSITION));
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[3*k+1]);
		GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, TEXTURE_COORD), 2, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, TEXTURE_COORD));

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[3*k+2]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndexBuffer[k].capacity(), GLES20.GL_UNSIGNED_SHORT, 0);
	}
	
	public void postDraw(){
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	@Override
	public boolean isVisible(int i, int j) {
		return isVisible[i][j];
	}
	
	@Override
	public void onSave(Bundle bundle) {
		for(int i=0; i!=N+2; ++i){
			for(int j=0; j!=N+2; ++j){
				bundle.putInt("terrainType[" + i + "][" + j + "]", type[i][j]);
			}
		}
		super.onSave(bundle);
	}
	
	@Override
	public void onRestore(Bundle bundle) {
		for(int i=0; i!=N+2; ++i){
			for(int j=0; j!=N+2; ++j){
				type[i][j] = bundle.getInt("terrainType[" + i + "][" + j + "]", PLAIN);
			}
		}
		super.onRestore(bundle);
	}
}
