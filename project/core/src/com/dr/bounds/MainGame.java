package com.dr.bounds;

import com.DR.dLib.dTweener;
import com.DR.dLib.dValues;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.screens.DebugScreen;
import com.dr.bounds.screens.GameScreen;
import com.dr.bounds.screens.WaitingRoomScreen;

public class MainGame extends ApplicationAdapter {

	public static OrthographicCamera camera;
	private SpriteBatch batch;
	public static final float VIRTUAL_WIDTH = 720f, VIRTUAL_HEIGHT = 1280f, ASPECT_RATIO = VIRTUAL_WIDTH / VIRTUAL_HEIGHT;
	public static final int PLACEHOLDER_SKIN_ID = 0;
	private RequestHandler requestHandler;
	private Texture card, button, icon, obstacle;
	public static boolean isPlaying = false;
	private float cameraTime = 0;
	private GameScreen gameScreen;
	private WaitingRoomScreen waitingRoomScreen;
	private DebugScreen debugCard;
	
	// the time difference between frames
	private final float DELTA = 1f / 60f;
	// the time each update call takes ?
	private float accumulator = 0f;
	
	public MainGame(RequestHandler h)
	{
		requestHandler = h;
	}
	
	@Override
	public void create () {
		Gdx.input.setCatchBackKey(true);
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		camera.setToOrtho(true,VIRTUAL_WIDTH,VIRTUAL_HEIGHT);
		dValues.camera = camera;
		dValues.VH = VIRTUAL_HEIGHT;
		dValues.VW = VIRTUAL_WIDTH;
		
		button = new Texture("button.png");
		button.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		card = new Texture("card.png");
		icon = new Texture("playerIcon.png");
		icon.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		obstacle = new Texture("obstacle.png");
		obstacle.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		debugCard = new DebugScreen(0,0,card,button,requestHandler);
		//debugCard.hide();
		waitingRoomScreen = new WaitingRoomScreen(0,-VIRTUAL_HEIGHT,card,icon,requestHandler);
		//waitingRoomScreen.hide();
		gameScreen = new GameScreen(0,0,card, obstacle, requestHandler);
		gameScreen.pause();
		
		batch = new SpriteBatch();
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0,0, (int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1,1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
		// UPDATE
		accumulator += Gdx.graphics.getDeltaTime();
		while(accumulator >= DELTA)
		{
			update(DELTA);
			waitingRoomScreen.update(DELTA);
			debugCard.update(DELTA);
			gameScreen.update(DELTA);
			camera.update();
			if(Gdx.input.isKeyJustPressed(Keys.BACK))
			{
				gameScreen.pause();
				camera.position.set(VIRTUAL_WIDTH/2f,VIRTUAL_HEIGHT/2f, camera.position.z);
			}
			accumulator -= DELTA;
		}
		
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		gameScreen.render(batch);
		debugCard.render(batch);
		waitingRoomScreen.render(batch);
		batch.end();
		
		/**
		 * BUG: when inviting the nexus 5 from the memopad, if you swipe both away in the recents, and then invite from memo pad again, the nexus doesnt show info
		 */
	}
	
	public void update(float delta)
	{
		if(requestHandler.getRecievedMessage() != null)
		{
			debugCard.addText("\nMESSAGE RECIEVED: " + requestHandler.getRecievedMessage()[0]);
			if(requestHandler.getRecievedMessage()[0] == 'M')// movement received
			{
				gameScreen.getOpponent().setMovementMessage(requestHandler.getRecievedMessage());
			}
			else if(requestHandler.getRecievedMessage()[0] == 'S')// skin received
			{
				gameScreen.getOpponent().setSkinID((int)requestHandler.getRecievedMessage()[1]);
				waitingRoomScreen.showOpponentElements(gameScreen.getOpponentSkinID(), requestHandler.getOpponentName());
			}
			else if(requestHandler.getRecievedMessage()[0] == 'R')// ready in waiting room
			{
				waitingRoomScreen.setOpponentReady();
				//once opponent is ready, set the screen in the back to be the game screen
				gameScreen.setPos(0,0);
			}
			else if(requestHandler.getRecievedMessage()[0] == 'Z')// seed received
			{
				gameScreen.constructSeed(requestHandler.getRecievedMessage());
			}
			requestHandler.clearRecievedMessage();
		}
		if(requestHandler.hasNewInvite())
		{
			//debugCard.addText("\nNew Invite from: " + requestHandler.getInviterName());
		}
		
		if(Gdx.input.justTouched())
		{
		//	requestHandler.sendUnreliableMessage(gameScreen.getPlayer().getMovementMessage());
		}
	
		if(requestHandler.justJoined())
		{
			requestHandler.sendReliableMessage(new byte[]{'S',(byte)gameScreen.getPlayerSkinID()});
			requestHandler.setJoined(false);
		}
		
		if(requestHandler.shouldShowWaitingRoom())
		{
			waitingRoomScreen.show();
			showWaitingRoom(2.5f,Gdx.graphics.getDeltaTime());
			if(Gdx.input.isButtonPressed(Keys.BACK))
			{
				//camera.position.set(VIRTUAL_WIDTH/2f,  VIRTUAL_HEIGHT/2f, camera.position.z);
			}
		}
		
		if(waitingRoomScreen.getHideTime() >= 2f)
		{
			gameScreen.resume();
			//debugCard.hide();
			// move to gameScreen.resume() 
			if(requestHandler.isHost())
			{
				gameScreen.decodeAndSendSeed(gameScreen.getSeed());
			}
		}
	}
	
	public static int getVirtualMouseX()
	{
		return (int)(camera.position.x - VIRTUAL_WIDTH / 2f + (Gdx.input.getX() / (Gdx.graphics.getWidth() / VIRTUAL_WIDTH)));
	}
	
	public static int getVirtualMouseY()
	{
		return (int) (camera.position.y - VIRTUAL_HEIGHT / 2f + Gdx.input.getY() / (Gdx.graphics.getHeight() / VIRTUAL_HEIGHT));
	}
	
	public static void setCameraPos(float x, float y)
	{
		camera.position.set(x,y, camera.position.z);
	}
	
	private void showWaitingRoom(float duration, float delta)
	{
		if(cameraTime < duration)
		{
			cameraTime+=delta;
			//camera.position.set(dTweener.ElasticOut(cameraTime, VIRTUAL_WIDTH/2f, VIRTUAL_WIDTH, duration,4f),dTweener.ElasticOut(cameraTime, VIRTUAL_HEIGHT/2f, 0, duration,6f), camera.position.z);
			//camera.position.set(camera.position.x,dTweener.ElasticOut(cameraTime, VIRTUAL_HEIGHT/2f, VIRTUAL_HEIGHT, duration,6f), camera.position.z);
			waitingRoomScreen.setY(dTweener.ElasticOut(cameraTime, -VIRTUAL_HEIGHT, VIRTUAL_HEIGHT, duration, 6f));
		//	currentTime = 0;
			if(cameraTime >= duration/4f && cameraTime <= duration/3.5f)
			{
				waitingRoomScreen.showPlayerElements(gameScreen.getPlayerSkinID()); // show info regarding player
			}
		}
		else
		{
			requestHandler.setShowWaitingRoom(false);
			cameraTime = 0;
			duration = 0;
		}
	}
}
