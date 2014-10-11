package com.dr.bounds.screens;

import com.DR.dLib.dButton;
import com.DR.dLib.dText;
import com.DR.dLib.dUICard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;
import com.dr.bounds.RequestHandler;
import com.dr.bounds.SkinLoader;
import com.dr.bounds.maps.MapGenerator;

public class GameScreen extends dScreen {
	
	// current clients player
	private Player player;
	// opponents object
	private Player opponent;
	// camera's speed in pixels per second (camera moves upward)
	//public static final float CAMERA_SPEED = 300f; 
	public static final float CAMERA_SPEED = 100f;
	// used to interact with android device
	private RequestHandler requestHandler;
	// Generate the map
	private MapGenerator mapGen;
	// screen that will show when game ends
	private GameOverScreen gameOverScreen;
	// keep track of players score
	private int playerScore = 0;
	// whether or not opponent wants rematch
	private boolean opponentReplay = false;
	// whether opponent lost or player lost
	private boolean opponentLost = false;
	
	// debug
	private dText debug = new dText(0,0,64f,"debug:");
	
	// test
	private DialogBoxScreen dialog;

	public GameScreen(float x, float y, Texture texture, Texture obstacle) {
		super(x, y, texture);
		
		requestHandler = MainGame.requestHandler;
		
		player = new Player(MainGame.VIRTUAL_WIDTH/2f-32f,MainGame.VIRTUAL_HEIGHT/2f-32f, 4, requestHandler);
		
		opponent = new Player(MainGame.VIRTUAL_WIDTH/2f-32f,MainGame.VIRTUAL_HEIGHT/2f-32f,2, requestHandler);
		opponent.setControllable(false);	

		mapGen = new MapGenerator(MapGenerator.TYPE_DEFAULT,obstacle, player);
		
		if(requestHandler.isHost())
		{
			decodeAndSendSeed(mapGen.getSeed());
		}
		
		gameOverScreen = new GameOverScreen(getX(), getY(), texture, player.getSkinID());
		gameOverScreen.hide();
		
		dialog = new DialogBoxScreen(0,0,texture);
		dButton dialogButton = new dButton(0,0, new Sprite(texture),"yes");
		dialogButton.setColor(Color.LIGHT_GRAY);
		dialogButton.setDimensions(dialog.getDialogBox().getWidth() / 2f - 1, 64f);
		dialogButton.setTextSize(64f);
		dButton dialogButton2 = new dButton(0,0, new Sprite(texture),"no");
		dialogButton2.setColor(Color.LIGHT_GRAY);
		dialogButton2.setDimensions(dialog.getDialogBox().getWidth() / 2f - 1, 64f);
		dialogButton2.setTextSize(64f);
		dText prompt = new dText(0,0,48f,"GAME OVER\n////////////////\n  continue?");
		prompt.setMultiline(true);
		prompt.setColor(0,0,0,0.5f);
		dialog.getDialogBox().addObject(dialogButton, dUICard.RIGHT_NO_PADDING, dUICard.BOTTOM_NO_PADDING);
		dialog.getDialogBox().addObject(dialogButton2, dUICard.LEFT_NO_PADDING, dUICard.BOTTOM_NO_PADDING);
		dialog.getDialogBox().addObject(prompt, dUICard.CENTER, dUICard.TOP);
		
		debug.setMultiline(true);
		
	}
	
	@Override
	public void update(float delta)
	{
		debug.setPos(MainGame.camera.position.x - MainGame.VIRTUAL_WIDTH / 2f  + 2f, MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT /2f + 2f);
		debug.setText("DEBUG:\nseed: " + getSeed() + "\nhadCollision: " + mapGen.hadCollision() + "\nopponentReplay: " + opponentReplay + "\nopponentLost: "
				+ opponentLost + "\nwantsReplay: "+ gameOverScreen.wantsReplay());
		if(isPaused() == false)
		{
			super.update(delta);
			mapGen.update(delta);
			gameOverScreen.update(delta);
			dialog.update(delta);
			
			if((mapGen.hadCollision() || opponentLost) && gameOverScreen.isVisible() == false)
			{
				// game ended, set the winner skin for the game over screen
				if(opponentLost)
				{
					gameOverScreen.setWinnerSkinID(player.getSkinID());
					gameOverScreen.setTitleMessage("You win!");
				}
				else
				{
					gameOverScreen.setWinnerSkinID(opponent.getSkinID());
					gameOverScreen.setTitleMessage(requestHandler.getOpponentName() + " Wins!");
				}
				
				gameOverScreen.show();
				//dialog.show();
			}
			else if(mapGen.hadCollision() || opponentLost)
			{
				// player wants a replay and opponent wants a rematch
				if(gameOverScreen.wantsReplay() && opponentReplay)
				{
					mapGen.setHadCollision(false);
					player.reset();
					opponent.reset();
					// send seed to opponent
					if(requestHandler.isHost())
					{
						decodeAndSendSeed(getSeed());
					}
					opponentReplay = false;
					opponentLost = false;
					gameOverScreen.reset();
				}
			}
			else if(!mapGen.hadCollision() || opponentLost == false)
			{
				// only update player and opponent if game is running
				player.update(delta);
				opponent.update(delta);
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
		gameOverScreen.render(batch);
		dialog.render(batch);
		debug.render(batch);
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
	}
	
	public long getSeed()
	{
		return mapGen.getSeed();
	}
	
	// opponent wants to play again
	public void setWantsRematch(boolean rematch)
	{
		opponentReplay = rematch;
		gameOverScreen.setOpponentReplay(rematch);
	}
	
	public void setOpponentLost(boolean lost)
	{
		opponentLost = lost;
	}
	
	public void setPlayerSkin(int id)
	{
		player.setSkinID(id);
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

}
