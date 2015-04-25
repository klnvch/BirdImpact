package com.klnvch.birdimpact.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public abstract class Entity {
	
	protected static final String MATRIX = "uMVPMatrix";
	protected static final String POSITION = "aPosition";
	protected static final String TEXTURE = "texture1";
	protected static final String TEXTURE_COORD = "textureCoord";
	protected static final String ALPHA = "uAlpha";
	protected static final String DOT_SIZE = "uSize";
	protected static final String COLOR = "aColor";
	protected static final String TIME = "uTime";
	protected static final String DIRECTION = "aDirection";
	protected static final String START_TIME = "aStartTime";
	protected static final String EXPLOSIONS = "uExplosion";
	protected static final String ELAPSED_TIME = "uElapsedTime";
	
	protected int program = 0;
	
	protected FloatBuffer createFloatBuffer(float[] array){
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(array.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuf.asFloatBuffer();
		buffer.put(array);
		buffer.position(0);
		return buffer;
	}
	
	protected ShortBuffer createShortBuffer(short[] array) {
		ByteBuffer byteBuf1 = ByteBuffer.allocateDirect(array.length * 2);
		byteBuf1.order(ByteOrder.nativeOrder());
		ShortBuffer buffer = byteBuf1.asShortBuffer();
		buffer.put(array);
		buffer.position(0);
		return buffer;
	}
	
	protected int bufferArray(FloatBuffer floatBuffer){
		int[] buffer = new int[1];
		
		GLES20.glGenBuffers(1, buffer, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, floatBuffer.capacity() * 4, floatBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		return buffer[0];
	}
	
	protected int bufferElementArray(ShortBuffer floatBuffer){
		int[] buffer = new int[1];
		
		GLES20.glGenBuffers(1, buffer, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer[0]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, floatBuffer.capacity() * 2, floatBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		return buffer[0];
	}
	
    protected int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            Log.w("error", "Could not generate a new OpenGL texture object." + this.getClass().getName());
            return 0;
        }
        
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if(bitmap == null){
            Log.w("error", "Resource ID " + resourceId + " could not be decoded.");
            GLES20.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        } 
        
        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0]);

        // Set filtering: a default must be set, or the texture will be black.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle();

        // Unbind from the texture.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureObjectIds[0];        
    }
    
    protected int loadCompressedTexture(Context context, int resourceId) {
    	final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if(textureHandle[0] != 0){
            InputStream input = context.getResources().openRawResource(resourceId);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            
            //GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,  GLES20.GL_LINEAR_MIPMAP_LINEAR);
            //GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,  GLES20.GL_LINEAR);

            try{
                ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0,GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, input);
            }
            catch(IOException e){
                System.out.println("DEBUG! IOException" + e.getMessage());
            }
            finally{
                try {
                    input.close();
                } catch (IOException e) {
                    // ignore exception thrown from close.
                }
            }
        }else{
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];     
    }
    
    protected void loadProgram(Context context, int vID, int fID){
    	StringBuffer vs = new StringBuffer();
		StringBuffer fs = new StringBuffer();

		// read the files
		try {
			InputStream inputStream = context.getResources().openRawResource(vID);
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			String read = in.readLine();
			while(read != null){
				vs.append(read + "\n");
				read = in.readLine();
			}
			vs.deleteCharAt(vs.length() - 1);

			inputStream = context.getResources().openRawResource(fID);
			in = new BufferedReader(new InputStreamReader(inputStream));

			read = in.readLine();
			while(read != null){
				fs.append(read + "\n");
				read = in.readLine();
			}
			fs.deleteCharAt(fs.length() - 1);
		}catch (Exception e){
			Log.w("error", "Error in reading vertex or fragment shaders" + this.getClass().getName());
			return;
		}

		int _vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		if(_vertexShader != 0){
			GLES20.glShaderSource(_vertexShader, vs.toString());
			GLES20.glCompileShader(_vertexShader);
		}else{
			Log.w("error", "Error in compiling vertex shader" + this.getClass().getName());
			return;
		}
		
		int _pixelShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		if(_pixelShader != 0){
			GLES20.glShaderSource(_pixelShader, fs.toString());
			GLES20.glCompileShader(_pixelShader);
		}else{
			Log.w("error", "Error in compiling fragment shader" + this.getClass().getName());
			return;
		}

		// Create the program
		program = GLES20.glCreateProgram();
		if(program != 0){
			GLES20.glAttachShader(program, _vertexShader);
			GLES20.glAttachShader(program, _pixelShader);
			GLES20.glLinkProgram(program);
		}else{
			Log.w("error", "Error in creating a program" + this.getClass().getName());
		}
    }
}
