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
	public static final float CAMERA_SPEED = 300f; 
	//public static final float CAMERA_SPEED = 0f;
	// used to interact with android device
	private RequestHandler requestHandler;
	// Generate the map
	private MapGenerator mapGen;
	// screen that will show when game ends
	private GameOverScreen gameOverScreen;
	// keep track of players score
	private int playerScore = 0;
	
	// test
	private DialogBoxScreen dialog;

	public GameScreen(float x, float y, Texture texture, Texture obstacle, RequestHandler rq) {
		super(x, y, texture);
		
		requestHandler = rq;
		
		player = new Player(MainGame.VIRTUAL_WIDTH/2f-32f,MainGame.VIRTUAL_HEIGHT/2f-32f, 8, rq);
		
		opponent = new Player(MainGame.VIRTUAL_WIDTH/2f-32f,MainGame.VIRTUAL_HEIGHT/2f-32f,2, rq);
		opponent.setControllable(false);	

		mapGen = new MapGenerator(MapGenerator.TYPE_DEFAULT,obstacle, player);
		
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
	}
	
	@Override
	public void update(float delta)
	{
		if(isPaused() == false)
		{
			super.update(delta);
			player.update(delta);
			opponent.update(delta);
			mapGen.update(delta);
			gameOverScreen.update(delta);
			dialog.update(delta);
			
			if(mapGen.hadCollision() && gameOverScreen.isVisible() == false )
			{
				gameOverScreen.show();
				//dialog.show();
			}
			else if(mapGen.hadCollision())
			{
				if(gameOverScreen.wantsReplay())
				{
					mapGen.setHadCollision(false);
					player.reset();
				}
			}
			else if(!mapGen.hadCollision())
			{
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
	 * @param message recieved message
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
