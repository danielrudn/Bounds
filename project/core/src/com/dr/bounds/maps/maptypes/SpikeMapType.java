package com.dr.bounds.maps.maptypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dr.bounds.BoundsAssetManager;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;
import com.dr.bounds.maps.MapGenerator;
import com.dr.bounds.maps.MapType;
import com.dr.bounds.maps.obstacles.SpikeObstacle;

public class SpikeMapType extends MapType{

	public SpikeMapType(int type, Player player, MapGenerator generator) {
		super(type, player, generator);
		MIN_DISTANCE = MAX_DISTANCE / 2;
		MAX_DISTANCE *= 1.5f;
		MIN_WIDTH = 92;
		MAX_WIDTH = 256 - MIN_WIDTH;
		this.typeName = "SPIKES";
		bgColor = new Color(51f/256f, 110f/256f, 123f/256f,1f);
		super.setScoreIncrementAmount(1);
		for(int x = 0; x < 6; x++)
		{
			this.getObstacles().add(new SpikeObstacle(0,0,BoundsAssetManager.getTexture("spike2.png"), player));
		}
	}

	private boolean hadCirclularCollision(Vector2 f, Vector2 i, int index)
	{
		float radiusPlanet = obstacles.get(index).getWidth() / 2f;
		float radiusPlayer = player.getWidth() / 2f;
		return Math.pow((f.x + radiusPlanet) - (i.x + radiusPlayer), 2) + Math.pow((f.y + radiusPlanet) - (i.y + radiusPlayer), 2) <= Math.pow(radiusPlanet + radiusPlayer, 2); 
	}
	
	@Override
	protected void generateObstacle(int index)
	{
		int side = MapGenerator.rng.nextInt(11); // 0,1,5,6,7 is LEFT, 2,3,8,9,10 is RIGHT, 4 is center
		if(side == 0 || side == 1 || side == 5 || side == 6 || side == 7)// left
		{
			obstacles.get(index).setWidth(MIN_WIDTH + MapGenerator.rng.nextInt(MAX_WIDTH));
			obstacles.get(index).setHeight(obstacles.get(index).getWidth());
			obstacles.get(index).setX(32f);
		}
		else if(side == 2 || side == 3 || side == 8 || side == 9 || side == 10)// right
		{
			obstacles.get(index).setWidth(MIN_WIDTH + MapGenerator.rng.nextInt(MAX_WIDTH));
			obstacles.get(index).setHeight(obstacles.get(index).getWidth());
			obstacles.get(index).setX(MainGame.VIRTUAL_WIDTH - obstacles.get(index).getWidth() - 32f);
		}
		else if(side == 4)// center		
		{
			obstacles.get(index).setWidth(MIN_WIDTH + MapGenerator.rng.nextInt(MAX_WIDTH) - 32f);
			obstacles.get(index).setHeight(obstacles.get(index).getWidth());
			obstacles.get(index).setX(MainGame.VIRTUAL_WIDTH / 2f - obstacles.get(index).getWidth() / 2f + (-50 + MapGenerator.rng.nextInt(100)));
		}
		obstacles.get(index).setY(obstacles.get(getPreviousIndex(index)).getY() - MIN_DISTANCE - MapGenerator.rng.nextInt(MAX_DISTANCE));
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		BoundsAssetManager.dispose("spike2.png");
	}

}
