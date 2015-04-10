package com.dr.bounds;

import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetManager {

	// map of textures with the key being the name, and the value is the texture
	private static final Map<String, Texture> textures = new TreeMap<String, Texture>();
	// a texture for the current MapType background
	private static Texture background = null;
	
	private AssetManager(){}
	
	private static void loadTexture(String path, TextureFilter filter) throws Exception
	{
		if(filter == null)
		{
			textures.put(path.substring(0,path.indexOf(".")), new Texture(path));
		}
		else
		{
			Texture texture = new Texture(path);
			texture.setFilter(filter, filter);
			textures.put(path.substring(0,path.indexOf(".")), texture);
		}
	//	System.out.println("[AssetManager]: loaded texture " + path + " succesfully");
	}
	
	public static void loadAll()
	{
		try
		{
			loadTexture("card.png", TextureFilter.Linear);
			loadTexture("button.png", TextureFilter.Linear);
			loadTexture("playerIcon.png", TextureFilter.Linear);
			loadTexture("circle.png", TextureFilter.Linear);
			loadTexture("girder.png", TextureFilter.Linear);
		}
		catch(Exception e)
		{
			
		}
	}
	
	/**
	 * Get a texture by name, if the texture is not already loaded, then it is loaded in and returned.
	 * @param name Name of Texture to get
	 * @return Returns a texture, null if it does not exist in the assets folder
	 */
	public static Texture getTexture(String name)
	{
		String nameNoExtensions = name; 
		try
		{
			nameNoExtensions = name.substring(0,name.indexOf("."));
		}
		catch(IndexOutOfBoundsException ioobe){}
		
		for(String names : textures.keySet())
		{
			if(name.equalsIgnoreCase(names) || nameNoExtensions.equalsIgnoreCase(names))
			{
				return textures.get(names);
			}
		}
		// didn't find it, that means it didn't exist in the map, so we load the texture in and try again
		try
		{
			loadTexture(name, TextureFilter.Linear);
		}
		catch(Exception e)
		{
			// failed to load the file in, return null
			return null;
		}
		// try again now that its been loaded in
		return getTexture(name);
	}
	
	public static Texture getBackground(String path)
	{
		background = new Texture(path);
		return background;
	}
	

	public static class SkinLoader 
	{
	
		public static final Texture SKINS = new Texture("skins.png");
		private static final int SKINS_PER_LINE = 8;
		private static final int SKIN_DIMENSIONS = 64;
		
		public static TextureRegion getTextureForSkinID(int id)
		{
			SKINS.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			// find what line the skin is on
			int currentLine = 0, tempID = id;
			while(tempID > SKINS_PER_LINE-1)//0 to 7
			{
				tempID-=SKINS_PER_LINE;// 8 per line
				currentLine++;
			}
			int startX = SKIN_DIMENSIONS * tempID;
			int startY = SKIN_DIMENSIONS * currentLine;
			TextureRegion skin =  new TextureRegion(SKINS,startX,startY,SKIN_DIMENSIONS,SKIN_DIMENSIONS);
			skin.flip(false, true);
			return skin;
		}
	}
}

