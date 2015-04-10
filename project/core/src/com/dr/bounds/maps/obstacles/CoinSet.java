package com.dr.bounds.maps.obstacles;

import java.util.ArrayList;
import java.util.List;

import com.DR.dLib.dObject;
import com.DR.dLib.dTweener;
import com.DR.dLib.ui.dImage;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;
import com.dr.bounds.maps.MapGenerator;
import com.dr.bounds.screens.GameScreen;

/**
 * A collection of coins that can be picked up 
 */
public class CoinSet extends dObject
{

	// types of layouts for the coins
	private final static int LINEAR = 0, SQUARE = 1, STRAIGHT = 2, HORIZONTAL_WAVE = 3, VERTICAL_WAVE = 4;
	// current type of layout,
	private int currentLayout;
	// collection of coins
	private List<Coin> coins;
	// player instance
	private Player player;
	// whether we can regenerate
	private boolean canRegenerate = false;
	
	public CoinSet(float x, float y, Texture texture, Player p)
	{
		super(x,y,texture);
		player = p;
		currentLayout = MapGenerator.rng.nextInt(5);
		coins = new ArrayList<Coin>();
		for(int i = 0; i < 9; i++)
		{
			coins.add(new Coin(0,0,texture));
		}
		setCoinPos(x,y);
	}

	@Override
	public void render(SpriteBatch batch)
	{
		for(int x = 0; x < coins.size(); x++)
		{
			coins.get(x).render(batch);
		}
	}

	@Override
	public void update(float delta)
	{
		for(int x = 0; x < coins.size(); x++)
		{
			coins.get(x).update(delta);
			if(player.getBoundingRectangle().overlaps(coins.get(x).getBoundingRectangle()) && coins.get(x).getWidth() != 0)
			{
				// player hit a coin
				MainGame.playCoinSound();
				coins.get(x).hide();
				player.setCoins(player.getCoins() + 1);
				GameScreen.incrementPlayerCoins();
			}	
			if(coins.get(0).getY() > MainGame.camera.position.y + MainGame.VIRTUAL_WIDTH / 2f && canRegenerate == false)
			{
				canRegenerate = true;
			}
		}
	}
	
	private void setCoinPos(float x, float y)
	{
		if(currentLayout == LINEAR)
		{
			for(int i = 0; i < 9; i++)
			{
					coins.get(i).setPos(x + (8 + 64)*i,y + (8 + 64)*i);
			}
		}
		else if(currentLayout == SQUARE)
		{
			int row = 0, column = 0;
			for(int i = 0; i < 9; i++)
			{
				if(i % 3 == 0 && i != 0)
				{
					row++;
					column = 0;
				}
				coins.get(i).setPos(x + (16 + 64)*column,y + (16 + 64)*row);
				column++;
			}
		}	
		else if(currentLayout == STRAIGHT)
		{
			for(int i = 0; i < 9; i++)
			{
				coins.get(i).setPos(x + (16 + 64)*i, y);
			}
		}
		else if(currentLayout == HORIZONTAL_WAVE)
		{
			for(int i = 0; i < 9; i++)
			{
				coins.get(i).setPos(x + (16 + 64)*i, (float) (y + 64*Math.sin((45*i))));
			}
		}
		else if(currentLayout == VERTICAL_WAVE)
		{
			for(int i = 0; i < 9; i++)
			{
				coins.get(i).setPos((float) (x + 64*Math.cos(45*i)), y + (16 + 64)*i);
			}
		}
	}
	
	@Override
	public void setPos(float x, float y)
	{
		super.setPos(x, y);
		setCoinPos(x,y);
	}
	
	@Override
	public void setX(float x)
	{
		super.setX(x);
		setCoinPos(x,getY());
	}
	
	@Override
	public void setY(float y)
	{
		super.setY(y);
		setCoinPos(getX(),y);
	}
	
	public void reset()
	{
		canRegenerate = true;
	}
	
	public void generate()
	{
		// reset coins
		for(int x = 0; x < coins.size(); x++)
		{
			coins.get(x).setDimensions(64, 64);
			coins.get(x).show();
		}
		currentLayout = MapGenerator.rng.nextInt(5);
		canRegenerate = false;
	}
	
	public int getNumberOfCoins()
	{
		return coins.size();
	}

	public boolean canRegenerate()
	{
		return canRegenerate;
	}
}

class Coin extends dImage {

	private float time = 0, duration = 0.5f;
	private boolean active = false;
	public Coin(float x, float y, Texture texture) {
		super(x, y, texture);
		this.setUpdatable(true);
	}
	

	@Override
	public void update(float delta)
	{
		super.update(delta);
		if(time <= duration && active)
		{
			time += delta;
			this.setAlpha(dTweener.LinearEase(time, 1f, -1f, duration));
			this.setOriginCenter();
			this.setDimensions(dTweener.LinearEase(time, 64f, -64f, duration), dTweener.LinearEase(time, 64f, -64f, duration));
		}
	}
	
	/**
	 * Starts the hide animation
	 */
	public void hide()
	{
		active = true;
	}
	
	public void show()
	{
		active = false;
		this.setOrigin(0, 0);
		time = 0;
	}
	
}