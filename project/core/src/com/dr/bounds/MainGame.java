package com.dr.bounds;

import java.util.ArrayList;

import com.DR.dLib.dScreen;
import com.DR.dLib.dText;
import com.DR.dLib.dUICard;
import com.DR.dLib.dValues;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dr.bounds.screens.DebugScreen;
import com.dr.bounds.screens.GameScreen;
import com.dr.bounds.screens.InboxScreen;
import com.dr.bounds.screens.InviteScreen;
import com.dr.bounds.screens.WaitingRoomScreen;
import com.dr.bounds.ui.InviteCard;
import com.dr.bounds.ui.PlayerCard;

public class MainGame extends ApplicationAdapter implements MultiplayerListener {
	
	/*==================================================
	 *					VARIABLES					   |
	 *=================================================*/
	
	// RequestHandler, Batch, Camera
	public static OrthographicCamera camera;
	private SpriteBatch batch;
	public static RequestHandler requestHandler;
	
	// CONSTANTS
	public static final float VIRTUAL_WIDTH = 720f, VIRTUAL_HEIGHT = 1280f, ASPECT_RATIO = VIRTUAL_WIDTH / VIRTUAL_HEIGHT;
	public static final int PLACEHOLDER_SKIN_ID = 0;
	
	// Textures, will be moved to an AssetManager class
	private Texture card, button, icon, obstacle, circle;
	
	// SCREENS
	public static dScreen currentScreen;
	public static dScreen previousScreen = null;
	public static InviteScreen inviteScreen;
	public static GameScreen gameScreen;
	public static WaitingRoomScreen waitingRoomScreen;
	public static DebugScreen debugCard;
	public static InboxScreen inboxScreen;
	
	public static boolean isPlaying = false;
	
	// the time difference between frames
	private final float DELTA = 1f/60f;
	// the time each update call takes ?
	private float accumulator = 0f;
	
	// test, remove
	private ArrayList<dUICard> recentlyPlayedList;
	
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
		//obstacle = new Texture("obstacle.png");
		obstacle = new Texture("girder.png");
		obstacle.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		circle = new Texture("circle.png");
		circle.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		debugCard = new DebugScreen(0,0,card,button);
		//debugCard.hide();
		waitingRoomScreen = new WaitingRoomScreen(0,-VIRTUAL_HEIGHT,card,icon,circle);
		//waitingRoomScreen.hide();
		gameScreen = new GameScreen(0,0,card, obstacle);
	//	gameScreen.pause();
		
		recentlyPlayedList = new ArrayList<dUICard>();
		inviteScreen = new InviteScreen(0,0,card,recentlyPlayedList);
		
		inboxScreen = new InboxScreen(0,0,card,recentlyPlayedList);
		
		batch = new SpriteBatch();
		
		currentScreen = debugCard;
		currentScreen = gameScreen;
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0,0, (int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(189f/256f, 195f/256f, 199f/256f,.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
		// UPDATE
		accumulator += Gdx.graphics.getDeltaTime();
		while(accumulator >= DELTA)
		{
			//inviteCard.update(DELTA);
			update(DELTA);
			accumulator -= DELTA;
		}
	
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		/*
		gameScreen.render(batch);
		debugCard.render(batch);
		inviteScreen.render(batch);
		waitingRoomScreen.render(batch);
		inviteCard.render(batch);
		*/
		currentScreen.render(batch);
		batch.end();
		
		/**
		 * TODO: BUG: when inviting the nexus 5 from the memopad, if you swipe both away in the recents, and then invite from memo pad again, the nexus doesnt show info
		 */
	}
	
	public void update(float delta)
	{
		// update screens
		/*
		waitingRoomScreen.update(DELTA);
		debugCard.update(DELTA);
		gameScreen.update(DELTA);
		inviteScreen.update(DELTA);
		*/
		currentScreen.update(delta);
		//update camera
		camera.update();
		// waiting room has moved away, start playing.
		if(waitingRoomScreen.getHideTime() >= 2f)
		{
			gameScreen.resume();
			//debugCard.hide();
			currentScreen.switchScreen(gameScreen);
		}
		if(Gdx.input.isKeyJustPressed(Keys.BACK) || Gdx.input.isKeyJustPressed(Keys.SPACE))
		{
			currentScreen.goBack();
		}
	}
	
	public static int getVirtualMouseX()
	{
		return (int) (camera.position.x - VIRTUAL_WIDTH / 2f + (Gdx.input.getX() / (Gdx.graphics.getWidth() / VIRTUAL_WIDTH)));
	}
	
	public static int getVirtualMouseY()
	{
		return (int) (camera.position.y - VIRTUAL_HEIGHT / 2f + Gdx.input.getY() / (Gdx.graphics.getHeight() / VIRTUAL_HEIGHT));
	}
	
	public static void setCameraPos(float x, float y)
	{
		camera.position.set(x,y, camera.position.z);
	}
	

	public static int getPlayerSkinID()
	{
		return gameScreen.getPlayer().getSkinID();
	}
	
	/*
	 * MULTIPLAYER LISTENER METHODS
	 */
	
	@Override
	public void onConnected()
	{
		
	}

	@Override
	public void onJoinedRoom() {
		//waitingRoomScreen.show();
		if(currentScreen != waitingRoomScreen)
		{
			currentScreen.switchScreen(waitingRoomScreen);
		}
	}

	@Override
	public void onLeftRoom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomCreated() {
		//waitingRoomScreen.show();
	}

	@Override
	public void onRealTimeMessageRecieved(byte[] msg) {
		debugCard.addText("\nMESSAGE RECIEVED: " + msg[0]);
		if(msg[0] == 'M')// movement received
		{
			gameScreen.getOpponent().setMovementMessage(msg);
		}
		else if(msg[0] == 'S')// skin received
		{
			gameScreen.getOpponent().setSkinID((int)msg[1]);
			waitingRoomScreen.showOpponentElements(gameScreen.getOpponentSkinID(), requestHandler.getOpponentName());
		}
		else if(msg[0] == 'R')// ready in waiting room
		{
			waitingRoomScreen.setOpponentReady();
			//once opponent is ready, set the screen in the back to be the game screen
			gameScreen.setPos(0,0);
		}
		else if(msg[0] == 'Z')// seed received
		{
			if(requestHandler.isHost() == false)
			{
				gameScreen.constructSeed(msg);
			}
		}
		else if(msg[0] == 'L')// opponent lost
		{
			gameScreen.setOpponentLost(true);
		}
		else if(msg[0] == 'P')// opponent wants a rematch
		{
			gameScreen.setOpponentWantsRematch(true);
		}
	}

	@Override
	public void onPeerLeft() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeersConnected() {
		//opponent just connected
		requestHandler.sendReliableMessage(new byte[]{'S',(byte)gameScreen.getPlayerSkinID()});
	}

	@Override
	public void onPeersDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInvitationReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInvitationRemoved() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecentPlayersLoaded(int numLoaded) {
		// recents card
		dUICard recentsCard = new dUICard(0,0,card);
		recentsCard.setDimensions(VIRTUAL_WIDTH - 128f, 128f);
		// change this line V
		recentsCard.setColor(new Color(46f/256f, 204f/256f, 113f/256f,1f));
		recentsCard.setHasShadow(false);
		dText recentsText = new dText(0,0,92f,"RECENTS (" + numLoaded + ")");
		recentsText.setColor(Color.WHITE);
		dUICard divider = new dUICard(0,0,card);
		divider.setHasShadow(false);
		divider.setDimensions(recentsCard.getWidth() - 8f, 4f);
		recentsCard.addObject(recentsText, dUICard.LEFT, dUICard.CENTER);
		recentsCard.addObjectUnder(divider, 0);
		inviteScreen.addCardAsObject(recentsCard);
		for(int x = 0; x < numLoaded; x++)
		{
			//construct array list of recently played cards
			PlayerCard currentCard = new PlayerCard(0,0,card, 1+MathUtils.random(9), requestHandler.getRecentPlayerName(x));
			currentCard.setPlayerID(requestHandler.getRecentPlayerID(x));
			inviteScreen.addCardAsObject(currentCard);
		}
		requestHandler.loadInvitablePlayers();
	}
	
	@Override
	public void onInvitablePlayersLoaded(int numLoaded)
	{
		// invitable card
		dUICard invitableCard = new dUICard(0,0,card);
		invitableCard.setDimensions(VIRTUAL_WIDTH - 128f, 128f);
		// change this line V
		invitableCard.setColor(new Color(46f/256f, 204f/256f, 113f/256f,1f));
		invitableCard.setHasShadow(false);
		dText invitableText = new dText(0,0,92f,"FRIENDS (" + numLoaded + ")");
		invitableText.setColor(Color.WHITE);
		dUICard divider = new dUICard(0,0,card);
		divider.setHasShadow(false);
		divider.setDimensions(invitableCard.getWidth() - 8f, 4f);
		invitableCard.addObject(invitableText, dUICard.LEFT, dUICard.CENTER);
		invitableCard.addObjectUnder(divider, 0);
		inviteScreen.addCardAsObject(invitableCard);
		for(int x = 0; x < numLoaded; x++)
		{
			PlayerCard currentCard = new PlayerCard(0,0,card,1+MathUtils.random(9),requestHandler.getInvitablePlayerName(x));
			currentCard.setPlayerID(requestHandler.getInvitablePlayerID(x));
			inviteScreen.addCardAsObject(currentCard);
		}
		inviteScreen.showCards();
	}
	
	@Override
	public void onInvitationsLoaded(int numLoaded)
	{
		for(int x = 0; x < numLoaded; x++)
		{
			InviteCard currentCard = new InviteCard(0,0,card,requestHandler.getInviterName(x), requestHandler.getInvitationID(x));
			inboxScreen.addCardAsObject(currentCard);
		}
		inboxScreen.showCards();
	}
}
