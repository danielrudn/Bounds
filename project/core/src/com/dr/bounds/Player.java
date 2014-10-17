package com.dr.bounds;

import com.DR.dLib.dObject;
import com.DR.dLib.dTweener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dr.bounds.screens.GameScreen;

public class Player extends dObject {
	
	private final int SKIN_DIMENSIONS = 64;
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
	// bounding rectangle used for collisions
	private Rectangle boundingRect = new Rectangle(SKIN_DIMENSIONS, SKIN_DIMENSIONS,SKIN_DIMENSIONS,SKIN_DIMENSIONS);
	
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
		if(controllable)
		{
			if(Gdx.input.justTouched() && moveCenter == false)
			{
				// temp
			//	playerVelocity.x = 0;
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
		
		// add velocity
		setPosition(getX() + playerVelocity.x, getY() + playerVelocity.y);
		boundingRect.set(getX() + 8f, getY() + 8f, getWidth()-16f, getHeight()-16f);
	}
	
	private void changeVelocity(float delta)
	{
		playerVelocity.set(dTweener.MoveToAndSlow(playerVelocity.x, targetVelocity.x, delta/12f), dTweener.MoveToAndSlow(playerVelocity.y, targetVelocity.y, delta/32f));
		setY(getY() - GameScreen.CAMERA_SPEED * delta * 1.25f);
		// check if passed bounds and need to move back to center
		checkBounds();
	}
	
	private void checkBounds()
	{
		if(getX() <= -20 || getX() >= MainGame.VIRTUAL_WIDTH - getWidth() + 20)// changes with +- 20 to account for ball squeezing
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
	
	
	private void moveLeft(float delta)
	{
			playerVelocity.x = dTweener.MoveToAndSlow(playerVelocity.x, -12f*32f, delta/12f);
			checkBounds();
	}
	
	private void moveRight(float delta)
	{
			playerVelocity.x = dTweener.MoveToAndSlow(playerVelocity.x, 12f*32f, delta/12f);
			checkBounds();
	}
	
	private void moveCenter(float delta)
	{
		if(getX() < MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f - 14f || getX() > MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f + 14f)
		{
			//setPosition(dTweener.MoveToAndSlow(getX(), MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f, 4f*delta),getY());
			setPosition(dTweener.MoveToAndSlow(getX(), MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f, 4f*delta), dTweener.MoveToAndSlow(getY(), startY - 500f, 4f*delta));
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
	
	public void reset()
	{
		setPos(MainGame.VIRTUAL_WIDTH / 2f - getWidth()/2f, MainGame.VIRTUAL_HEIGHT / 2f - getHeight() / 2f);
		moveCenter = false;
		startY = getY();
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
		}
		startY = getY();
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
	
	@Override
	public Rectangle getBoundingRectangle()
	{
		return boundingRect;
	}
}
