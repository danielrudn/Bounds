package com.dr.bounds.maps;

import java.util.Random;

import com.DR.dLib.dImage;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.dr.bounds.Player;

public class dPlanetObstacle extends dObstacle {

	// whether this planet has a moon orbiting it
	private boolean hasMoon = false;
	// the moon object
	private dImage moonImage;
	// the x value for the moon rotation
	private float rotationTime = 0;
	// the offset from start position of the angle for the moon
	private float angleOffset = 0;
	// rotation speed of the moon
	private float rotationSpeed = 1f;
	// minimum rotation speed
	private final float MIN_ROTATION_SPEED = 0.5f;
	// the distance between the planet and the moon
	private int moonDistance = 32;
	// colors for planet and moon
	private Color[] planetColors = new Color[]{new Color(46f/256f, 204f/256f, 113f/256f,1f), // green
			new Color(52f/256f, 152f/256f, 219f/256f,1f), // blue
			new Color(231f/256f, 76f/256f, 60f/256f,1f), // red
			new Color(241f/256f, 196f/256f, 15f/256f,1f), //yellow
			new Color(243f/256f, 156f/256f, 18f/256f,1f)}; // orange
	// rng to determine values
	private Random rng;
	
	public dPlanetObstacle(float x, float y, Texture texture, Player p, Random rng) {
		super(x, y, texture, p);
		this.rng = rng;
		generate();
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
				moonImage.setPos((float) (getX() + getWidth()/2f + Math.cos(rotationTime) * (moonImage.getWidth() + getWidth()/2f + moonDistance)),
						(float)(getY() + getHeight()/2f + Math.sin(rotationTime) * (moonImage.getWidth() + getHeight()/2f + moonDistance)));
				rotationTime+=delta*rotationSpeed;
			}
			else if(rotationTime >= 2 * Math.PI)
			{
				rotationTime = 0;
			}
		}
	}
	
	public void generate()
	{
		int colorIndex = rng.nextInt(planetColors.length);
		setColor(planetColors[colorIndex]);
		int moonProbability = rng.nextInt(2);
		if(moonProbability == 0)
		{
			hasMoon = false;
		}
		else if(moonProbability == 1)
		{
			if(hasMoon == false)
			{
				// TODO: FIX THIS LINE V
				moonImage = new dImage(0,0,new Texture("circle.png"));
			}
			hasMoon = true;
			moonImage.setDimensions(64f, 64f);
			try{
				moonImage.setColor(planetColors[colorIndex+1]);
			}
			catch(Exception e)
			{
				moonImage.setColor(planetColors[0]);
			}
			moonImage.setPos(getX() - 15 - moonImage.getWidth(), getY() - 15 - moonImage.getHeight());
			angleOffset = rng.nextInt(2);
			rotationTime = angleOffset;
			rotationSpeed = MIN_ROTATION_SPEED + rng.nextFloat();
			moonDistance = rng.nextInt(64);
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
