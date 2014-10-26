package com.dr.bounds.maps;

import com.DR.dLib.dImage;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.dr.bounds.Player;

public class dPlanetObstacle extends dObstacle {

	// whether this planet has a moon orbiting it
	private boolean hasMoon = false;
	// the moon object
	private dImage moonImage;
	// the x value for the moon rotation
	private float rotationTime = 0;
	private float rotationDuration = 2f;
	
	public dPlanetObstacle(float x, float y, Texture texture, Player p) {
		super(x, y, texture, p);
		int moonProbability = MathUtils.random(3);
		moonProbability = 2;
		if(moonProbability == 0 || moonProbability == 1)
		{
			hasMoon = false;
		}
		else if(moonProbability == 2)
		{
			hasMoon = true;
			moonImage = new dImage(0,0,texture);
			moonImage.setDimensions(64f, 64f);
			moonImage.setColor(Color.BLACK);
			moonImage.setPos(getX() - 15 - moonImage.getWidth(), getY() - 15 - moonImage.getHeight());
		}
	}
	
	@Override
	public void render(SpriteBatch batch)
	{
		super.render(batch);
		if(hasMoon)
		{
			moonImage.render(batch);
		}
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		if(hasMoon)
		{
			if(rotationTime < 2 * Math.PI)
			{
				moonImage.setPos((float) (getX() + getWidth()/2f + Math.cos(rotationTime) * (moonImage.getWidth() + getWidth()/2f)),
						(float)(getY() + getHeight()/2f + Math.sin(rotationTime) * (moonImage.getWidth() + getHeight()/2f + 32)));
				rotationTime+=delta;
			}
			else if(rotationTime >= 2 * Math.PI)
			{
				rotationTime = 0;
			}
		}
	}
	
	@Override
	public void setPos(float x, float y)
	{
		super.setPos(x, y);
		if(hasMoon)
		{
			moonImage.setPos(getX() - 15 - moonImage.getWidth(), getY() - 15 - moonImage.getHeight());
		}
	}
	
	@Override
	public void setX(float x)
	{
		super.setX(x);
		if(hasMoon)
		{
			moonImage.setPos(getX() - 15 - moonImage.getWidth(), getY() - 15 - moonImage.getHeight());
		}
	}
	
	@Override
	public void setY(float y)
	{
		super.setY(y);
		if(hasMoon)
		{
			moonImage.setPos(getX() - 15 - moonImage.getWidth(), getY() - 15 - moonImage.getHeight());
		}
	}
	
	public Rectangle getMoonBoundingRectangle()
	{
		return moonImage.getBoundingRectangle();
	}
	
	public boolean hasMoon()
	{
		return hasMoon;
	}

}