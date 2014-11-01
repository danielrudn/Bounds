package com.dr.bounds.maps;

import com.badlogic.gdx.graphics.Texture;
import com.dr.bounds.Player;

public class dMovingObstacle extends dObstacle {
	
	private float moveTime = 0;
	private final float moveDuration = (float) (Math.PI * 2f);
	
	public dMovingObstacle(float x, float y, Texture texture, Player p) {
		super(x, y, texture, p);

	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		if(moveTime < moveDuration)
		{
			moveTime+=delta;
			setX((float) (getX() - 2f * Math.sin(moveTime)));
		}
		else
		{
			moveTime = 0;
		}
	}

}
