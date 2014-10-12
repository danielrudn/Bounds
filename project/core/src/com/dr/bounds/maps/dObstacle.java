package com.dr.bounds.maps;

import com.DR.dLib.dObject;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;

public class dObstacle extends dObject{
	
	// width/height
	private float objWidth = 0, objHeight = 0;
	// whether or not the obstacle is under the screen and needs to be re-generated up top
	private boolean regenerate = false;
	// whether or not the player[s] have passed the obstacle and need to be given a point
	private boolean passed = false;

	public dObstacle(float x, float y, Texture texture) {
		super(x, y, texture);
		objWidth = texture.getWidth();
		objHeight = texture.getHeight();
		// set origin 0,0
		setOrigin(0,0);
	}

	public dObstacle(float x, float y, Sprite sprite) {
		super(x, y, sprite);
		objWidth = sprite.getWidth();
		objHeight = sprite.getHeight();
		setOrigin(0,0);
	}
	
	@Override
	public void update(float delta) {
		// check if under cameras reach
		if(getY() >= MainGame.camera.position.y + MainGame.VIRTUAL_HEIGHT/2f && regenerate == false)
		{
			regenerate = true;
		}
		// check if player passed and needs to be awarded point
		
	}

	public boolean shouldRegenerate()
	{
		return regenerate;
	}
	
	public void setRegenerate(boolean r)
	{
		regenerate = r;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		getSprite().draw(batch);
	}
	
	public void setWidth(float w)
	{
		setScaleX(w / getSprite().getRegionWidth());
		objWidth = w;
	}
	
	public void setHeight(float h)
	{
		setScaleY(h / getSprite().getRegionHeight());
		objHeight = h;
	}
	
	public void setDimensions(float w, float h)
	{
		setWidth(w);
		setHeight(h);
	}
	
	@Override
	public float getWidth()
	{
		return objWidth;
	}
	
	@Override
	public float getHeight()
	{
		return objHeight;
	}
}
