package com.dr.bounds;

import com.DR.dLib.dObject;
import com.DR.dLib.dTweener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dr.bounds.screens.GameScreen;

public class Player extends dObject {
	
	private int skinID = MainGame.PLACEHOLDER_SKIN_ID;
	private boolean controllable = true;
	private boolean moveCenter = false;
	private boolean changeVelocity = false;
	private Vector2 targetVelocity = new Vector2(0,0);
	private Vector2 playerVelocity = new Vector2(0,0);
	private float squeezeTime = 0;
	private boolean squeezed = true;
	// y position when the user taps the screen
	private float startY = 0;
	
	private RequestHandler requestHandler;
	
	public Player(float x, float y, RequestHandler rq) {
		super(x, y, new Sprite(SkinLoader.getTextureForSkinID(MainGame.PLACEHOLDER_SKIN_ID)));
		requestHandler = rq;
	}
	
	public Player(float x,float y, int id, RequestHandler rq)
	{
		super(x,y,new Sprite(SkinLoader.getTextureForSkinID(MainGame.PLACEHOLDER_SKIN_ID)));
		setSkinID(id);
		requestHandler = rq;
	}
	
	public Player(float x, float y, int id, boolean controllable, RequestHandler rq)
	{
		super(x,y,new Sprite(SkinLoader.getTextureForSkinID(MainGame.PLACEHOLDER_SKIN_ID)));
		skinID = id;
		this.controllable = controllable;
		requestHandler = rq;
	}
	
	public Player(float x, float y, boolean controllable, RequestHandler rq)
	{
		super(x,y,new Sprite(SkinLoader.getTextureForSkinID(MainGame.PLACEHOLDER_SKIN_ID)));
		this.controllable = controllable;
		requestHandler = rq;
	}
	
	public Player(float x, float y, Texture t, RequestHandler rq)
	{
		super(x,y,t);
		requestHandler = rq;
	}
	
	public Player(float x, float y, TextureRegion t, RequestHandler rq)
	{
		super(x,y, new Sprite(t));
		requestHandler = rq;
	}
	

	@Override
	public void render(SpriteBatch batch) {
		getSprite().draw(batch);
	}

	@Override
	public void update(float delta) {
		// add velocity
		setPosition(getX() + playerVelocity.x, getY() + playerVelocity.y);
		
		if(controllable)
		{
			if(Gdx.input.isTouched() && moveCenter == false)
			{
				if(touchedLeftSide())// user touched left half of screen
				{
					targetVelocity.set(-12f*32f,0);
					changeVelocity = true;
				}
				else // user touched right half of screen
				{
					targetVelocity.set(12f*32f,0);
					changeVelocity = true;
				}
				startY = getY();
				requestHandler.sendUnreliableMessage(getMovementMessage());
			}
		}
		
		if(changeVelocity)
		{
			changeVelocity(delta);
		}
		if(moveCenter)
		{
			moveCenter(delta);
		}
		
		if(squeezed)
		{
			if(squeezeTime <= 3f)
			{
				squeezeTime+=delta;
				setScale(dTweener.ElasticOut(squeezeTime, 24f, 40f, 3f,6f) / getWidth(), dTweener.ElasticOut(squeezeTime, 76f, -12f, 3f,6f) / getHeight());
			}
			else
			{
				squeezed = false;
			}
		}
	}
	
	private void changeVelocity(float delta)
	{
		playerVelocity.set(dTweener.MoveToAndSlow(playerVelocity.x, targetVelocity.x, delta/8f), dTweener.MoveToAndSlow(playerVelocity.y, targetVelocity.y, delta/32f));
		setY(getY() - GameScreen.CAMERA_SPEED * delta/1.1f);
		// check if passed bounds and need to move back to center
		checkBounds();
	}
	
	private void checkBounds()
	{
		if(getX() <= -15 || getX() >= MainGame.VIRTUAL_WIDTH - getWidth() + 15)// changes with +- 15 to account for ball squeezing
		{
			moveCenter = true;
			changeVelocity = false;
			setScale(16f / getWidth(),76f / getHeight());
			playerVelocity.set(0,0);
			targetVelocity.set(0,0);
			squeezeTime = 0;
			squeezed = true;
		}
	}
	
	/*
	private void moveLeft(float delta)
	{
		if(getX() > 4f)
		{
			playerVelocity.x = dTweener.MoveToAndSlow(playerVelocity.x, -12f*32f, 12f*delta);
		}
		else
		{
			moveCenter = true;
			moveLeft = false;
			moveRight = false;
			playerVelocity.x = 0;
		}
	}
	
	private void moveRight(float delta)
	{
		if(getX() < MainGame.VIRTUAL_WIDTH - getWidth() - 4f)
		{
			playerVelocity.x = dTweener.MoveToAndSlow(playerVelocity.x, 12f*32f, 12f*delta);
		}
		else
		{
			moveCenter = true;
			moveRight = false;
			moveLeft = false;
			playerVelocity.x = 0;
		}
	}
	*/
	private void moveCenter(float delta)
	{
		if(getX() < MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f - 7f || getX() > MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f + 7f)
		{
		//	setPosition(dTweener.MoveToAndSlow(getX(), MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f, 4f*delta),getY());
			setPosition(dTweener.MoveToAndSlow(getX(), MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f, 4f*delta), dTweener.MoveToAndSlow(getY(), startY - 450f, 4f*delta));
		}
		else
		{
			moveCenter = false;
		}
	}
	
	private boolean touchedLeftSide()
	{
		if(MainGame.getVirtualMouseX() <= MainGame.VIRTUAL_WIDTH/2f)
		{
			return true;
		}
		return false;
	}
	
	public void setMovementMessage(byte[] message)//when player is NOT controllable, it sends opponents touches and acts accordingly
	{
		if(moveCenter == false)
		{
			if(message[1] == 'L')//left
			{
				targetVelocity.set(-12f*32f,0);
			}
			else if(message[1] == 'R')//right
			{
				targetVelocity.set(12f*32f,0);
			}
			changeVelocity = true;
			startY = getY();
		}
	}

	public void setControllable(boolean c)
	{
		controllable = c;
	}
	
	public void setSkinID(int id)
	{
		skinID = id;
		getSprite().setRegion(SkinLoader.getTextureForSkinID(skinID));
	}
	
	public int getSkinID()
	{
		return skinID;
	}
	
	public byte[] getMovementMessage()
	{
		if(touchedLeftSide())
		{
			return new byte[]{'M','L'};
		}
		return new byte[]{'M','R'};
	}
}
