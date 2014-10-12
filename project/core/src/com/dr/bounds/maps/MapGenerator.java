package com.dr.bounds.maps;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;

public class MapGenerator {

	// map generation type
	public static final int TYPE_DEFAULT = 1515, TYPE_SPACE = 3030;
	// the current map generation type
	private int currentType = TYPE_DEFAULT; 
	// ArrayList of obstacles in map, in an arraylist for easy adding/removing and modifying
	private ArrayList<dObstacle> obstacles = new ArrayList<dObstacle>();
	// Random number generator for positioning objects
	private Random rng = new Random();
	// seed for random number generator
	private long seed = 123456789;
	// Minimum  and maximum vertical(y) distance between obstacles
	private final int MIN_DISTANCE = 256, MAX_DISTANCE = (int) MainGame.VIRTUAL_HEIGHT - 256;
	// minimum / maximum width of blocks
	private final int MIN_WIDTH = 192, MAX_WIDTH = (int)MainGame.VIRTUAL_WIDTH - 256;
	// player object to determine collisions
	private Player player;
	// useless REMOVE PLEASE
	Rectangle useless = new Rectangle();
	// whether or not the player had a collision
	private boolean hadCollision = false;
	
	/**
	 * Creates a new generator and sets the level type
	 * @param mapType Type of map to generate, use static attributes from this class as parameters
	 */
	public MapGenerator(int mapType, Texture obstacleTexture, Player player)
	{
		setMapType(mapType);
		//generateSeed();
		this.player = player;
		// add 7 obstacles to start with
		for(int x = 0; x < 7; x++)
		{
			obstacles.add(new dObstacle(0,MainGame.camera.position.y + MainGame.VIRTUAL_HEIGHT, obstacleTexture));
			obstacles.get(x).setColor(Color.RED);
		}
		generateFirstSet();
	}
	
	public void update(float delta)
	{
		// loop through all obstacles
		for(int x = 0; x < obstacles.size(); x++)
		{
			obstacles.get(x).update(delta);
			if(currentType == TYPE_DEFAULT)
			{
				if(obstacles.get(x).shouldRegenerate())
				{
					generateDefault(x);
					obstacles.get(x).setRegenerate(false);
				}
				// check if player had collision
				if(Intersector.intersectRectangles(player.getBoundingRectangle(), obstacles.get(x).getBoundingRectangle(), useless)) // FIX
				{
					obstacles.get(x).setColor(Color.BLUE);
					hadCollision = true;
					// send message to let opponent know that i have lost
					MainGame.requestHandler.sendReliableMessage(new byte[]{'L'});
					// test might have to remove
					break;
				}
			}
			else if(currentType == TYPE_SPACE)
			{
				
			}
		}
	}
	
	private void generateDefault(int index)
	{
			obstacles.get(index).setColor(Color.RED);
			int side = rng.nextInt(2); // 0 is LEFT, 1 is RIGHT
			if(side == 0)// left
			{
				obstacles.get(index).setWidth(MIN_WIDTH + rng.nextInt(MAX_WIDTH));
				obstacles.get(index).setX(0);
			}
			else if(side == 1)// right
			{
				obstacles.get(index).setWidth(MIN_WIDTH + rng.nextInt(MAX_WIDTH));
				obstacles.get(index).setX(MainGame.VIRTUAL_WIDTH - obstacles.get(index).getWidth());
			}
			System.out.println("index: " + index + " next: " + getNextIndex(index) + " prev: " + getPreviousIndex(index));
			obstacles.get(index).setY(obstacles.get(getNextIndex(index)).getY() - MIN_DISTANCE - rng.nextInt(MAX_DISTANCE));
		/*	if(obstacles.get(getNextIndex(index)).getY() < MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT / 4f)
			{
				obstacles.get(index).setY(obstacles.get(getNextIndex(index)).getY() - MIN_DISTANCE - rng.nextInt(MAX_DISTANCE));
			}
			else
			{
				obstacles.get(index).setY(MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT/2f - MIN_DISTANCE - rng.nextInt(MAX_DISTANCE));
			}
	
			//make sure it's not vertically close to any other objects
			for(int y = 0; y < obstacles.size(); y++)
			{
				if(getVerticalDistance(obstacles.get(index), obstacles.get(y)) < MIN_DISTANCE)
				{
					obstacles.get(index).setY(obstacles.get(index).getY() - MIN_DISTANCE);
					System.out.println("near");
				}
			}*/
	}
	
	private void generateFirstSet()
	{
		for(int x = 0; x < obstacles.size(); x++)
		{
		//	generateDefault(x);
			obstacles.get(x).setColor(Color.RED);
			int side = rng.nextInt(2); // 0 is LEFT, 1 is RIGHT
			if(side == 0)// left
			{
				obstacles.get(x).setWidth(MIN_WIDTH + rng.nextInt(MAX_WIDTH));
				obstacles.get(x).setX(0);
			}
			else if(side == 1)// right
			{
				obstacles.get(x).setWidth(MIN_WIDTH + rng.nextInt(MAX_WIDTH));
				obstacles.get(x).setX(MainGame.VIRTUAL_WIDTH - obstacles.get(x).getWidth());
			}
			obstacles.get(x).setY(MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT/2f - MIN_DISTANCE - rng.nextInt(MAX_DISTANCE));
			
		}
	}
	
	private float getVerticalDistance(dObstacle o1, dObstacle o2)
	{
		return Math.abs(o1.getY() - o2.getY());
	}
	
	private int getNextIndex(int i)
	{
		if(i == obstacles.size()-1)
		{
			return 0;
		}
		return i+1;
	}
	
	private int getPreviousIndex(int i)
	{
		if(i == 0)
		{
			return obstacles.size() - 1;
		}
		return i-1;
	}
	
	public void render(SpriteBatch batch)
	{
		for(int x = 0; x < obstacles.size(); x++)
		{
			obstacles.get(x).render(batch);
		}
	}
	
	public void setSeed(long s)
	{
		seed = s;
		rng.setSeed(seed);
		generateFirstSet();
	}
	
	public void generateSeed()
	{
		setSeed(Math.abs(rng.nextLong()));
	}
	
	public void setMapType(int mapType)
	{
		if(mapType != TYPE_DEFAULT || mapType != TYPE_SPACE)
		{
			currentType = TYPE_DEFAULT;
		}
		else
		{
			currentType = mapType;
		}
	}
	
	public void setHadCollision(boolean c)
	{
		hadCollision = c;
		//MainGame.setCameraPos(MainGame.VIRTUAL_WIDTH/2f, MainGame.VIRTUAL_HEIGHT / 2f);
		// reset obstacles
		for(int x = 0; x < obstacles.size(); x++)
		{
			obstacles.get(x).setY(MainGame.camera.position.y + MainGame.VIRTUAL_HEIGHT);
			obstacles.get(x).setColor(Color.RED);
		}
		generateSeed();
	}
	
	public int getMapType()
	{
		return currentType;
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
