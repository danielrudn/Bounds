package com.dr.bounds.maps;

import java.util.ArrayList;

import com.DR.dLib.ui.dImage;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;

public abstract class MapType {

	// the values used when generating obstacles on the map
	// Minimum  and maximum vertical(y) distance between obstacles
	protected int MIN_DISTANCE = 192, MAX_DISTANCE = (int) MainGame.VIRTUAL_HEIGHT / 2 - 128;
	// minimum / maximum width of blocks
	protected int MIN_WIDTH = 192, MAX_WIDTH = (int)MainGame.VIRTUAL_WIDTH - 475;
	// the id of the map type
	private int type;
	// name of the map
	protected String typeName;
	// list of obstacles
	protected ArrayList<dObstacle> obstacles;
	// the MapGenerator object that this MapType is being used in
	protected MapGenerator gen;
	// Player object to determine collisions and score
	protected Player player;
	// the background images for this maptype
	protected dImage firstBG, secondBG;
	// the next map type
	protected MapType nextType = null;
	// whether the currentType should be switched with the nextType
	protected boolean shouldSwitch = false;
	// whether the maptype should keep regenerating its obstacles
	protected boolean isTransitioning = false;
	// whether the map type should start showing the new bg types
	protected boolean switchBG = false;
	// whether to show the transition image between map types
	private boolean showTransitionImage = false;
	// whether to change position for the transition image
	private boolean moveTransitionImage = true;
	// whether the obstacles for the new type have been generated
	private boolean newTypeGenerated = false;
	
	public MapType(int type, Player player, MapGenerator generator, Texture bgTexture)
	{
		this.type = type;
		obstacles = new ArrayList<dObstacle>();
		gen = generator;
		this.player = player;
		
		firstBG = new dImage(0,0, bgTexture);
		secondBG = new dImage(0,-MainGame.VIRTUAL_HEIGHT, bgTexture);
	}
	
	public void render(SpriteBatch batch)
	{
		firstBG.render(batch);
		secondBG.render(batch);
		renderObstacles(batch);
		if(nextType != null && showTransitionImage)
		{
			nextType.renderObstacles(batch);
		}
	}
	
	protected void renderObstacles(SpriteBatch batch)
	{
		for(int x = 0; x < obstacles.size(); x++)
		{
			obstacles.get(x).render(batch);
		}
	}
	
	public void update(float delta)
	{
		// update backgrounds
		if(firstBG.getY() >= MainGame.camera.position.y + MainGame.VIRTUAL_HEIGHT / 2f)
		{
			if(nextType != null && switchBG && secondBG == nextType.secondBG)
			{
				firstBG = nextType.firstBG;
				shouldSwitch = true;
			}
			else if(nextType != null && switchBG)
			{
				firstBG = nextType.firstBG;
				showTransitionImage = true;
			}
			firstBG.setY(secondBG.getY() - firstBG.getHeight());
			if(showTransitionImage && moveTransitionImage)
			{
				MapGenerator.transitionImage.setPos(firstBG.getX(), firstBG.getY() + firstBG.getHeight() - MapGenerator.transitionImage.getHeight() / 2f);
				moveTransitionImage = false;
				nextType.generateNewType();
			}
		}
		if(secondBG.getY() >= MainGame.camera.position.y + MainGame.VIRTUAL_HEIGHT / 2f)
		{
			if(nextType != null && switchBG && firstBG == nextType.firstBG)
			{
				secondBG = nextType.secondBG;
				shouldSwitch = true;
			}
			else if(nextType != null && switchBG)
			{
				secondBG = nextType.secondBG;
				showTransitionImage = true;
			}
			secondBG.setY(firstBG.getY() - secondBG.getHeight());
			if(showTransitionImage && moveTransitionImage)
			{
				MapGenerator.transitionImage.setPos(secondBG.getX(), secondBG.getY() + secondBG.getHeight() - MapGenerator.transitionImage.getHeight() / 2f);
				moveTransitionImage = false;
				nextType.generateNewType();
			}
		}
	}
	
	protected void generateFirstSet()
	{
	//	obstacles.get(0).setY(MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT/2f - MIN_DISTANCE - MapGenerator.rng.nextInt(MAX_DISTANCE));
	//	obstacles.get(0).setRegenerate(false);
	//	obstacles.get(0).setPassed(false);
		for(int x = 0; x < obstacles.size(); x++)
		{
			generate(x);
			obstacles.get(x).setRegenerate(false);
		}
	}
	
	/**
	 * DO NOT OVERRIDE
	 * @param index
	 */
	protected void generate(int index)
	{
		if(isTransitioning == false)
		{
			//reset passed for this obstacles
			obstacles.get(index).setPassed(false);
			generateBlock(index);
		}
		else
		{
			boolean canSwitch = true;
			for(int x = 0; x < obstacles.size(); x++)
			{
				if(obstacles.get(x).shouldRegenerate() == false)
				{
					canSwitch = false;
				}
			}
			if(canSwitch)
			{
				switchBG = true;
			}
		}
	}
	
	protected void generateBlock(int index)
	{
		int side = MapGenerator.rng.nextInt(11); // 0,1,5,6,7 is LEFT, 2,3,8,9,10 is RIGHT, 4 is center
		if(side == 0 || side == 1 || side == 5 || side == 6 || side == 7)// left
		{
			obstacles.get(index).setWidth(MIN_WIDTH + MapGenerator.rng.nextInt(MAX_WIDTH));
			obstacles.get(index).setX(0);
		}
		else if(side == 2 || side == 3 || side == 8 || side == 9 || side == 10)// right
		{
			obstacles.get(index).setWidth(MIN_WIDTH + MapGenerator.rng.nextInt(MAX_WIDTH));
			obstacles.get(index).setX(MainGame.VIRTUAL_WIDTH - obstacles.get(index).getWidth());	
		}
		else if(side == 4)// center
		{
			obstacles.get(index).setWidth(MIN_WIDTH + MapGenerator.rng.nextInt(MAX_WIDTH) - 32f);
			obstacles.get(index).setX(MainGame.VIRTUAL_WIDTH / 2f - obstacles.get(index).getWidth() / 2f + (-50 + MapGenerator.rng.nextInt(100)));
		}
		obstacles.get(index).setY(obstacles.get(getPreviousIndex(index)).getY() - MIN_DISTANCE - MapGenerator.rng.nextInt(MAX_DISTANCE));
	}
	
	/**
	 * Generates the obstacles from scratch when transitioning to a new map type
	 */
	protected void generateNewType()
	{
		if(newTypeGenerated == false)
		{
			obstacles.get(0).setY(MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT/2f - MIN_DISTANCE - MapGenerator.rng.nextInt(MAX_DISTANCE));
			obstacles.get(0).setRegenerate(false);
			obstacles.get(0).setPassed(false);
			for(int x = 1; x < obstacles.size(); x++)
			{
				generate(x);
				obstacles.get(x).setRegenerate(false);
			}
			newTypeGenerated = true;
		}
	}
	
	public void reset()
	{
		// reset backgrounds
		firstBG.setPos(0,0);
		secondBG.setPos(0,-MainGame.VIRTUAL_HEIGHT);
	}
	
	/**
	 * If this method has been called, the current map type will begin transitioning to the one provided
	 * @param next the next map type
	 */
	public void setNextMapType(MapType next)
	{
		nextType = next;
		if(next != null)
		{
			isTransitioning = true;
		}
	}
	
	public boolean shouldSwitch()
	{
		return shouldSwitch;
	}
	
	public int getMapType()
	{
		return type;
	}
	
	public String getTypeName()
	{
		return typeName;
	}
	
	public ArrayList<dObstacle> getObstacles()
	{
		return obstacles;
	}

	protected int getPreviousIndex(int i)
	{
		if(i == 0)
		{
			return obstacles.size() - 1;
		}
		return i-1;
	}	
}