package com.dr.bounds.ui;

import com.DR.dLib.ui.dImage;
import com.DR.dLib.ui.dText;
import com.DR.dLib.dObject;
import com.DR.dLib.dTweener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoadingIcon extends dObject {

	private dImage circleOne, circleTwo;
	// the circle that is currently expanding;
	private dImage currentCircle;
	private boolean isLoading = false;
	private float expandTime = 0;
	private final float EXPAND_DURATION = .8f;
	// colors, move to AssetManager class
	/*
	private Color[] flatColors = new Color[]{new Color(46f/256f, 204f/256f, 113f/256f,1f), // green
											new Color(52f/256f, 152f/256f, 219f/256f,1f), // blue
											new Color(231f/256f, 76f/256f, 60f/256f,1f), // red
											new Color(241f/256f, 196f/256f, 15f/256f,1f), //yellow
											new Color(243f/256f, 156f/256f, 18f/256f,1f)}; // orange*/
	
	private Color[] flatColors = new Color[]{new Color(85f/256f, 98f/256f, 112f/256f,1f),
			new Color(78f/256f, 205f/256f, 196f/256f,1f),
			new Color(199f/256f, 244f/256f, 100f/256f,1f),
			new Color(255f/256f, 107f/256f, 107f/256f,1f), 
			new Color(196f/256f, 77f/256f, 88f/256f,1f)}; 
	// index of color that will be assigned
	private int currentIndex = 0;
	// text
	private dText loadingText;
	
	public LoadingIcon(float x, float y, Texture texture)
	{
		super(x,y);
		circleOne = new dImage(x,y,texture);
		circleOne.setOriginCenter();
		circleOne.setDimensions(0,0);
		circleOne.setPos(x,y);
		circleOne.setColor(flatColors[0]);
		circleTwo = new dImage(x,y,texture);
		circleTwo.setOriginCenter();
		circleTwo.setDimensions(0, 0);
		circleTwo.setColor(flatColors[1]);
		
		currentCircle = circleOne;
		this.objColor = circleOne.getColor();
		
		loadingText = new dText(0,0,"LOADING");
		loadingText.setColor(Color.BLACK);
		loadingText.setSize(32f);
		loadingText.setPos(getX() + circleOne.getWidth() / 2f - loadingText.getWidth() / 2f, getY() + circleOne.getHeight() + 4f);
	}

	@Override
	public void render(SpriteBatch batch) {
		if(isLoading)
		{
			circleOne.render(batch);
			currentCircle.render(batch);
			loadingText.render(batch);
		}
	}

	@Override
	public void update(float delta) {
		if(isLoading)
		{
			if(expandTime <= EXPAND_DURATION)
			{
				expandTime+=delta;
				currentCircle.setDimensions(dTweener.ExponentialEaseOut(expandTime, 0, 92f, EXPAND_DURATION), dTweener.ExponentialEaseOut(expandTime, 0, 92f, EXPAND_DURATION));
				currentCircle.setOriginCenter();
			}
			else if(expandTime >= EXPAND_DURATION)
			{
				circleOne.setColor(currentCircle.getColor());
				if(currentCircle == circleOne)
				{
					currentCircle = circleTwo;
				}
				currentCircle.setColor(getNewColor());
				objColor = currentCircle.getColor();
				currentCircle.setDimensions(0, 0);
				expandTime = 0f;
			}
			loadingText.setPos(circleOne.getX() + circleOne.getWidth() - loadingText.getWidth()/4f + 8f, getY() + 92f*2f + 4f);
		}
	}
	
	private Color getNewColor()
	{
		if(currentIndex < flatColors.length-1)
		{
			currentIndex++;
		}
		else
		{
			currentIndex = 0;
		}
		return flatColors[currentIndex];
	}
	
	public void start()
	{
		isLoading = true;
	}
	
	public void stop()
	{
		isLoading = false;
	}
}

