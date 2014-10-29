package com.dr.bounds.maps;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.dr.bounds.Player;

public class MapGenerator {

	// map generation type
	public static final int TYPE_DEFAULT = 1515, TYPE_SPACE = 3030, TYPE_FACTORY = 6060;
	// the current map generation type
	private MapType currentType; 
	// Random number generator for positioning objects
	public static Random rng = new Random();
	// seed for random number generator
	private long seed = 123456789;
	// useless REMOVE PLEASE
	Rectangle useless = new Rectangle();
	// whether or not the player had a collision
	private boolean hadCollision = false;
	// player score to give to game screen
	private int score = 0;
	// used to let gamescreen know that it should show the score change animation
	private boolean scoreChanged = false;
	
	/**
	 * Creates a new generator and sets the level type
	 * @param mapType Type of map to generate, use static attributes from this class as parameters
	 */
	public MapGenerator(int mapType, Texture obstacleTexture, Player player)
	{
		generateSeed();
		if(mapType == TYPE_FACTORY)
		{
			currentType = new FactoryMapType(TYPE_FACTORY, player, obstacleTexture, this);
		}
		else if(mapType == TYPE_SPACE)
		{
			currentType = new SpaceMapType(TYPE_SPACE, player, this);
		}
	}
	
	public void update(float delta)
	{
		currentType.update(delta);
		// loop through all currentType.getObstacles()
		for(int x = 0; x < currentType.getObstacles().size(); x++)
		{
			currentType.getObstacles().get(x).update(delta);
			// check players score
			if(currentType.getObstacles().get(x).hasPassed() && currentType.getObstacles().get(x).hasIncrementedScore() == false)
			{
				score++;
				currentType.getObstacles().get(x).setIncrementedScore(true);
				scoreChanged = true;
			}
		}
	}

	public void render(SpriteBatch batch)
	{
		currentType.render(batch);
	}
	
	public void setSeed(long s)
	{
		seed = s;
		rng.setSeed(seed);
	}
	
	public void generateSeed()
	{
		setSeed(Math.abs(rng.nextLong()));
	}
	
	public void setHadCollision(boolean c)
	{
		hadCollision = c;
		if(c == false) // reset
		{
			//MainGame.setCameraPos(MainGame.VIRTUAL_WIDTH/2f, MainGame.VIRTUAL_HEIGHT / 2f);
			// reset currentType.getObstacles()
			for(int x = 0; x < currentType.getObstacles().size(); x++)
			{
				currentType.getObstacles().get(x).setY(0);
				currentType.getObstacles().get(x).setRegenerate(true);
				//currentType.getObstacles().get(x).setColor(Color.RED);
				currentType.getObstacles().get(x).setPassed(false);
				currentType.getObstacles().get(x).setIncrementedScore(true);
			}
			generateSeed();
			generateFirstSet();
			currentType.reset();
			score = 0;
		}
	}
	
	public void generateFirstSet()
	{
		currentType.generateFirstSet();
	}
	
	public void setScoreChanged(boolean scoreChanged)
	{
		this.scoreChanged = scoreChanged;
	}
	
	public void incrementScore()
	{
		score++;
	}
	
	public boolean hasScoreChanged()
	{
		return scoreChanged;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public int getMapType()
	{
		return currentType.getMapType();
	}
	
	public long getSeed()
	{
		return seed;
	}
	
	public boolean hadCollision()
	{
		return hadCollision;
	}
	
}
