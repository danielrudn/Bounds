package com.dr.bounds.screens;

import com.DR.dLib.ui.dScreen;
import com.DR.dLib.ui.dText;
import com.DR.dLib.dTweener;
import com.DR.dLib.ui.dUICard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;
import com.dr.bounds.RequestHandler;
import com.dr.bounds.maps.MapGenerator;

public class GameScreen extends dScreen {
	
	// current clients player
	private Player player;
	// opponents object
	private Player opponent;
	// camera's speed in pixels per second (camera moves upward)
	//public static final float CAMERA_SPEED = 300f; 
	public static final float CAMERA_SPEED = 500f;
	// used to interact with android device
	private RequestHandler requestHandler;
	// Generate the map
	private MapGenerator mapGen;
	// screen that will show when game ends
	private GameOverScreen gameOverScreen;
	// keep track of players score
	private int playerScore = 0;
	// used to display player's score
	private dText scoreText;
	// animate score changing
	private float scoreTime = 0;
	// whether opponent wants rematch
	private boolean opponentRematch = false;
	// whether opponent has lost
	private boolean opponentLost = false;
	
	// debug
	private dText debug = new dText(0,0,64f,"debug:");
	private static String debugString = "";

	public GameScreen(float x, float y, Texture texture, Texture obstacle) {
		super(x, y, texture);
		
		requestHandler = MainGame.requestHandler;
		
		player = new Player(MainGame.VIRTUAL_WIDTH/2f-32f,MainGame.VIRTUAL_HEIGHT/2f-32f, 5, requestHandler);
		
		opponent = new Player(MainGame.VIRTUAL_WIDTH/2f-32f,MainGame.VIRTUAL_HEIGHT/2f-32f, 0, requestHandler);
		opponent.setControllable(false);	

		mapGen = new MapGenerator(MapGenerator.TYPE_MACHINERY,obstacle, player);
		mapGen.generateSeed();
		// TODO: might remove
		mapGen.generateFirstSet();
		
		gameOverScreen = new GameOverScreen(getX(), getY(), texture, player.getSkinID());
		gameOverScreen.hide();
		
		debug.setMultiline(true);
		debug.setColor(Color.WHITE);
		
		scoreText = new dText(0,0,192f,"0");
		scoreText.setColor(0,0,0,0.5f);
		addObject(scoreText,dUICard.CENTER, dUICard.TOP);
		
	}
	
	@Override
	public void update(float delta)
	{
		debug.setPos(MainGame.camera.position.x - MainGame.VIRTUAL_WIDTH / 2f  + 2f, MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT /2f + 2f);
		debug.setText("DEBUG:\nseed: " + getSeed() + "\nhadCollision: " + mapGen.hadCollision() + "\nwantsReplay: "+ gameOverScreen.wantsReplay()
				+ "\nopponentRematch: " + opponentRematch + "\nopponentLost: " + opponentLost + debugString);
		if(isPaused() == false)
		{
			super.update(delta);
			mapGen.update(delta);
			gameOverScreen.update(delta);
			
			if((mapGen.hadCollision() || opponentLost) && gameOverScreen.isVisible() == false)
			{
				gameOverScreen.setScore(playerScore);
				gameOverScreen.show();
				//dialog.show();'
			}
			// single player game over screen
			else if(MainGame.requestHandler.isMultiplayer() == false && mapGen.hadCollision() && gameOverScreen.wantsReplay())
			{
				// player wants a replay
					player.reset();
					opponent.reset();
					mapGen.setHadCollision(false);
					// send seed to opponent
					if(requestHandler.isHost())
					{
						decodeAndSendSeed(getSeed());
					}
					scoreText.setText(Integer.toString(0));
			}
			// multiplayer game over screen
			else if((mapGen.hadCollision() || opponentLost) && MainGame.requestHandler.isMultiplayer() && gameOverScreen.wantsReplay() && opponentRematch)
			{
				player.reset();
				opponent.reset();
				mapGen.setHadCollision(false);
				// send seed to opponent
				if(requestHandler.isHost())
				{
				//	decodeAndSendSeed(getSeed());
				}
				scoreText.setText(Integer.toString(0));
				opponentRematch = false;
				opponentLost = false;
			}
			else if(!mapGen.hadCollision() && gameOverScreen.isVisible() == false && opponentLost == false)
			{
				// only update player and opponent if game is running
				player.update(delta);
				opponent.update(delta);
				// get players score from the map gen
				playerScore = mapGen.getScore();
				scoreText.setY(MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT / 2f + 48f);
				if(mapGen.hasScoreChanged())
				{
					scoreText.setText(Integer.toString(playerScore));
					scoreText.setX(getX() + getWidth()/2f - scoreText.getWidth()/2f - 12f);
					scoreTime = 0;
					mapGen.setScoreChanged(false);
				}
				if(scoreTime <= 2f)
				{
					scoreTime+=delta;
					scoreText.setSize(dTweener.ElasticOut(scoreTime, 256f, 192f - 256f, 2f));
				}
				// move camera upward
				MainGame.setCameraPos(MainGame.camera.position.x, MainGame.camera.position.y - CAMERA_SPEED * delta);
			
			}
		}
	}
	
	@Override
	public void render(SpriteBatch batch)
	{
		super.render(batch);
		mapGen.render(batch);
		opponent.render(batch);
		player.render(batch);
		scoreText.render(batch);
		gameOverScreen.render(batch);
		//debug.render(batch);
	}
	
	@Override
	public void resume()
	{
		super.resume();
		if(requestHandler.isHost())
		{
			decodeAndSendSeed(getSeed());
			mapGen.generateFirstSet();
		}
	}
	
	/**
	 * Convert the seed from a long into an array of bytes in order to be sent over a real-time message
	 * @param seed
	 */
	public void decodeAndSendSeed(long seed)
	{
		String seedString = Long.toString(seed);
		byte[] message = new byte[seedString.length() + 2];// length of seed and 2 reserved for metadata
		message[0] = 'Z'; //indicates we're sending seed
		message[1] = (byte) seedString.length(); // indicates length
		for(int x = 0; x < seedString.length(); x++)
		{
			message[x+2] = Byte.parseByte(seedString.substring(x,x+1));
		}
		requestHandler.sendReliableMessage(message);
	}
	
	/**
	 * Reconstructs the seed sent by the host for map gen use
	 * @param message received message
	 */
	public void constructSeed(byte[] message)
	{
		int length = message[1];
		String seed = "";
		for(int x= 0; x < length; x++)
		{
			seed += message[x+2];
		}
		mapGen.setSeed(Long.parseLong(seed));
		mapGen.generateFirstSet();
	}
	
	public long getSeed()
	{
		return mapGen.getSeed();
	}
	
	public void setOpponentWantsRematch(boolean rematch)
	{
		gameOverScreen.setOpponentWantsRematch(rematch);
		opponentRematch = rematch;
	}
	
	public void setOpponentLost(boolean lost)
	{
		opponentLost = lost;
	}
	
	public void setPlayerSkin(int id)
	{
		player.setSkinID(id);
	}
	
	public void setOpponentScore(int score)
	{
		gameOverScreen.setOpponentScore(score);
	}
	
	public int getPlayerSkinID()
	{
		return player.getSkinID();
	}
	
	public void setOpponentSkin(int id)
	{
		opponent.setSkinID(id);
	}
	
	public int getOpponentSkinID()
	{
		return opponent.getSkinID();
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public Player getOpponent()
	{
		return opponent;
	}
	
	public int getScore()
	{
		return mapGen.getScore();
	}
	
	@Override
	public void goBack() {
		if(MainGame.previousScreen != null)
		{
			switchScreen(MainGame.previousScreen);
		}
	}

	@Override
	public void switchScreen(dScreen newScreen) {
	//	this.hide();
		newScreen.show();
		MainGame.currentScreen = newScreen;
	//	MainGame.previousScreen = this;
	}
	
	// TEMP
	public static void log(String s)
	{
		debugString += "\n" + s;
	}

}
