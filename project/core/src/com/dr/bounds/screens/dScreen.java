package com.dr.bounds.screens;

import com.DR.dLib.dUICard;
import com.badlogic.gdx.graphics.Texture;
import com.dr.bounds.MainGame;

public abstract class dScreen extends dUICard {

	private boolean isPaused = false;
	
	public dScreen(float x, float y, Texture texture) {
		super(x, y, texture);
		this.setDimensions(MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT);
		setHasShadow(false);
	}
	
	public void pause()
	{
		isPaused = true;
	}
	
	public void resume()
	{
		isPaused = false;
	}
	
	public boolean isPaused()
	{
		return isPaused;
	}
	
	/*
	 * WARNING: WHEN OVERRIDING UPDATE AND RENDER METHODS MAKE SURE TO CALL super(...) TO UPDATE AND RENDER THE REGULAR DUICARD STUFF
	 */

}
