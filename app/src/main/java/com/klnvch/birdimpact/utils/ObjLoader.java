package com.klnvch.birdimpact.utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class ObjLoader {
	
	public static float vertices[];
	public static float textures[];
	public static short indices[];

	public static void loadObj2(Context context, int file){
		
		InputStream inputStream = context.getResources().openRawResource(file);
		DataInputStream dis = new DataInputStream(inputStream);
		
		int length = 0;
		
		try {
			// read number of objects
			/*int numObjects = */dis.readInt();
			
			// read material
			/*String material = */dis.readUTF();
			
			// read vertices
			length = dis.readInt();
			vertices = new float[length * 3];
			for(int i=0; i!=vertices.length; ++i){
				vertices[i] = dis.readFloat();
			}
			
			// read textures
			length = dis.readInt();
			textures = new float[length * 2];
			for(int i=0; i!=textures.length; ++i){
				textures[i] = dis.readFloat();
			}
			
			// read indexes
			length = dis.readInt();
			indices = new short[length];
			for(int i=0; i!=indices.length; ++i){
				indices[i] = dis.readShort();
			}
			
			dis.close();
			
		} catch (IOException e) {
			
			vertices = null;
			textures = null;
			indices = null;
			
			e.printStackTrace();
		}
	}
}
