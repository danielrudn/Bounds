package com.dr.bounds.maps.maptypes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.dr.bounds.Player;
import com.dr.bounds.maps.obstacles.dObstacle;

public class DefaultMapType extends MapType {
	
	public DefaultMapType(int type, Player player, MapGenerator generator) {
		super(type, player, generator, new Texture("DEFAULT_BG.png"));
		typeName = "Void/Default";
		gen.setScoreIncrementAmount(1);
		Texture obstacleTexture = new Texture("obstacle.png");
		obstacleTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		// add 12 obstacles to start with
		for(int x = 0; x < 12; x++)
		{
			obstacles.add(new dObstacle(0,0, obstacleTexture, player));
			obstacles.get(x).setRegenerate(false);
			obstacles.get(x).setColor(65f/256f,177f/256f,202f/256f,1f);
		}
	}

}
