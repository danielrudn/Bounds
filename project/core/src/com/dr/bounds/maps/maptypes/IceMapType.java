package com.dr.bounds.maps.maptypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.dr.bounds.BoundsAssetManager;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;
import com.dr.bounds.maps.MapGenerator;
import com.dr.bounds.maps.MapType;
import com.dr.bounds.maps.obstacles.IceObstacle;

public class IceMapType extends MapType {

	public IceMapType(int type, Player player, MapGenerator generator) {
		super(type, player, generator);
		typeName = "Falling Ice";
		bgColor = new Color(255f/256f, 255f/256f, 220f/256f,1f);
		for(int x = 0; x < 10; x++)
		{
			obstacles.add(new IceObstacle(0,0, BoundsAssetManager.getTexture("ice.png"), player));
			obstacles.get(x).setRegenerate(false);
		}
	}
	
	@Override
	protected void generateObstacle(int index)
	{
		obstacles.get(index).setX(16 + MapGenerator.rng.nextInt((int) (MainGame.VIRTUAL_WIDTH - obstacles.get(index).getWidth())));
		obstacles.get(index).setY(obstacles.get(getPreviousIndex(index)).getY() - MIN_DISTANCE - MapGenerator.rng.nextInt(MAX_DISTANCE));
	}

	@Override
	public void dispose()
	{
		super.dispose();
		BoundsAssetManager.dispose("ice.png");
	}
}
