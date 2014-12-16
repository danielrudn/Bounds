package com.dr.bounds.screens;

import com.DR.dLib.ui.dScreen;
import com.DR.dLib.ui.dText;
import com.DR.dLib.dTweener;
import com.DR.dLib.ui.dUICard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;
import com.dr.bounds.maps.MapGenerator;

public class GameScreen extends dScreen {
	
	// current clients player
	private Player player;
	// camera's speed in pixels per second (camera moves upward)
	public static float CAMERA_SPEED = 500f;
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

	public GameScreen(float x, float y, Texture texture) {
		super(x, y, texture);
		
		player = new Player(MainGame.VIRTUAL_WIDTH/2f-32f,MainGame.VIRTUAL_HEIGHT/2f-32f, 6);

		Texture obstacle = new Texture("girder.png");
		
		mapGen = new MapGenerator(MapGenerator.TYPE_SPACE,obstacle, player);
		mapGen.generateSeed();
		// TODO: might remove
		mapGen.generateFirstSet();
		
		gameOverScreen = new GameOverScreen(getX(), getY(), texture, player.getSkinID());
		gameOverScreen.hide();
		
		scoreText = new dText(0,0,192f,"0");
		scoreText.setColor(0,0,0,0.5f);
		addObject(scoreText,dUICard.CENTER, dUICard.TOP);
		
	}
	
	@Override
	public void update(float delta)
	{
		if(isPaused() == false)
		{
			super.update(delta);
			mapGen.update(delta);
			gameOverScreen.update(delta);
			
			if(mapGen.hadCollision() && gameOverScreen.isVisible() == false)
			{
				gameOverScreen.setScore(playerScore);
				gameOverScreen.show();
				//dialog.show();'
			}
			// single player game over screen
		//	else if(MainGame.requestHandler.isMultiplayer() == false && mapGen.hadCollision() && gameOverScreen.wantsReplay())
			else if(mapGen.hadCollision() && gameOverScreen.wantsReplay())
			{
				// player wants a replay
					player.reset();
					mapGen.setHadCollision(false);
					scoreText.setText(Integer.toString(0));
			}
			else if(!mapGen.hadCollision() && gameOverScreen.isVisible() == false)
			{
				// only update player and opponent if game is running
				player.update(delta);
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
					scoreText.setSize(dTweener.ElasticOut(scoreTime, 300f, 192f - 300f, 2f));
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
		player.render(batch);
		scoreText.render(batch);
		gameOverScreen.render(batch);
	}
	
	@Override
	public void resume()
	{
		super.resume();
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
	
	public Player getPlayer()
	{
		return player;
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
	
}