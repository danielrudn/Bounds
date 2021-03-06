package com.dr.bounds.screens;

import java.util.ArrayList;

import com.DR.dLib.animations.AnimationStatusListener;
import com.DR.dLib.animations.SlideExponentialAnimation;
import com.DR.dLib.ui.dButton;
import com.DR.dLib.ui.dScreen;
import com.DR.dLib.ui.dText;
import com.DR.dLib.ui.dUICardList;
import com.DR.dLib.dTweener;
import com.DR.dLib.ui.dUICard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.dr.bounds.BoundsAssetManager;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;
import com.dr.bounds.ui.ImageButton;
import com.dr.bounds.ui.RecentGamesGraph;
import com.dr.bounds.ui.GameInfoCard;

public class GameOverScreen extends dUICardList implements AnimationStatusListener {

	// two darker panels containing info about users score and money won
	private GameInfoCard infoCard;
	// best score
	private dText bestScoreText;
	// players final score for score counter
	private float playerScore = 0;
	// player instance
	private Player player;
	// replay button
	private dButton replayButton;
	// when user clicks replay, this turns false and resets the game
	private boolean wantsReplay = false;
	// Text at the top EX: (Game Over)
	private dText topText;
	// card containing above elements
	private dUICard topCard;
	// animations
	private SlideExponentialAnimation showAnimation;
	private static final int SHOW_ANIM_ID = 123;
	// graph
	private RecentGamesGraph rgg;
	// test
	private ShapeRenderer sr;
	// buttons
	private ImageButton shareButton, leaderboardsButton, achievementsButton;
	
	public GameOverScreen(float x, float y, Texture texture, Player player) {
		super(x, y, texture, new ArrayList<dUICard>());
		setColor(37f/256f, 116f/256f, 169f/256f,0f);
		setPadding(32f);
		setPaddingLeft(64f);
		
		this.player = player;
		
		showAnimation = new SlideExponentialAnimation(1f, this, SHOW_ANIM_ID, -MainGame.VIRTUAL_WIDTH, 0, this);
		this.setShowAnimation(showAnimation);
		
		topCard = new dUICard(getX(), getY(), texture);
		topCard.setColor(getColor());
		topCard.setPadding(32f);
		topCard.setPaddingLeft(64f);
		topCard.setDimensions(MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT / 2f - 128f);
		topCard.setHasShadow(false);
		
		topText = new dText(0,0,64f,"GAME OVER");
		topText.setColor(Color.WHITE);
		
		infoCard = new GameInfoCard(0,0, BoundsAssetManager.getTexture("card"));
		
		bestScoreText = new dText(0,0, 48f, "BEST: 0");
		bestScoreText.setColor(Color.WHITE);
		
		replayButton = new dButton(0,0, BoundsAssetManager.getTexture("replay.png"), "");
		replayButton.setDimensions(192f, 192f);
		replayButton.setColor(1f, 1f , 1f, 0.85f);
		
		shareButton = new ImageButton(0, 0, BoundsAssetManager.getTexture("card"), BoundsAssetManager.getTexture("shareIcon.png"), "SHARE");
		shareButton.setColor(34f/256f, 49f/256f, 63f/256f,1f);
		
		leaderboardsButton = new ImageButton(0, 0, BoundsAssetManager.getTexture("card"), BoundsAssetManager.getTexture("leaderboardsIcon.png"), "SCORES");
		leaderboardsButton.setColor(34f/256f, 49f/256f, 63f/256f,1f);
		
		achievementsButton = new ImageButton(0, 0, BoundsAssetManager.getTexture("card"), BoundsAssetManager.getTexture("achievementsIcon.png"), "TROPHIES");
		achievementsButton.setColor(34f/256f, 49f/256f, 63f/256f,1f);
		
		topCard.addObject(topText,dUICard.CENTER,dUICard.TOP);
		topCard.addObject(bestScoreText, dUICard.CENTER, dUICard.BOTTOM);
		addObject(infoCard, dUICard.LEFT_NO_PADDING, dUICard.CENTER);
		addCardAsObject(topCard);
		addObject(shareButton, dUICard.LEFT, dUICard.CENTER);
		addObject(leaderboardsButton, dUICard.CENTER, dUICard.CENTER);
		addObject(achievementsButton, dUICard.RIGHT, dUICard.CENTER);
		shareButton.setY(shareButton.getY() + 192f);
		leaderboardsButton.setY(shareButton.getY());
		achievementsButton.setY(shareButton.getY());
		addObject(replayButton, dUICard.RIGHT, dUICard.BOTTOM);
		replayButton.setOriginCenter();
		
		sr = new ShapeRenderer();
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		if(rgg != null)
		{
			rgg.update(delta);
		}
		if(isVisible())
		{
			if(replayButton.isClicked())
			{
				setY(0);
				MainGame.camera.position.y = MainGame.VIRTUAL_HEIGHT / 2f;
				if(wantsReplay == false)
				{
					wantsReplay = true;
					showAnimation.stop();
				}
			}
			else if(shareButton.isClicked())
			{
				MainGame.requestHandler.showShareIntent("I got a score of " + this.playerScore + " in Bounds!");
			}
			else if(leaderboardsButton.isClicked())
			{
				MainGame.requestHandler.showLeaderboard("");
			}
			else if(achievementsButton.isClicked())
			{
				System.out.println("achievements");
			}
			
			
			// single player reset
			if(wantsReplay)
			{
				setX(dTweener.MoveToAndSlow(getX(), 0 + MainGame.VIRTUAL_WIDTH * 2f,3f*delta));
				rgg.setX(rgg.getX() + getX());
				if(getX() >= MainGame.VIRTUAL_WIDTH)
				{
					reset();
				}
			}	
		}
	}
	
	@Override
	public void render(SpriteBatch batch)
	{
		if(isVisible())
		{
			batch.end();
			sr.setProjectionMatrix(MainGame.camera.combined);
			sr.begin(ShapeType.Filled);
			/*sr.rect(this.getX(), this.getY(), MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT/2f, new Color(37f/256f, 116f/256f, 169f/256f,1f), new Color(150f/256f, 116f/256f, 169f/256f,1f),
					new Color(3 7f/256f, 116f/256f, 169f/256f,1f), new Color(150f/256f, 116f/256f, 169f/256f,1f));
			sr.setColor(new Color(37f/256f, 116f/256f, 169f/256f,1f));
			sr.rect(this.getX(), this.getY()+MainGame.VIRTUAL_HEIGHT/2f, MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT/2f);*/
			sr.rect(this.getX(), this.getY(), MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT/2f+80, new Color(210f/256f, 82f/256f, 127f/256f,1f), new Color(192f/256f, 57f/256f, 43f/256f, 1f),
					new Color(210f/256f, 82f/256f, 127f/256f,1f), new Color(192f/256f, 57f/256f, 43f/256f, 1f));
			sr.setColor(34f/256f, 49f/256f, 63f/256f,1f);
			sr.rect(this.getX(), this.getY()+MainGame.VIRTUAL_HEIGHT/2f+80f, MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT/2f-80f);
			sr.end();
			batch.begin();
			super.render(batch);
			if(rgg != null)
			{
				rgg.render(batch);
			}
		}
	}
	
	public void show(int score, int combo, int coinsCollected)
	{
		super.show();
		// set score, combo, and coins
		infoCard.setCoinText(Integer.toString(player.getCoins()));
		infoCard.setComboText(Integer.toString(combo));
		infoCard.setBestComboText(Integer.toString(player.getBestCombo()));
		bestScoreText.setText("BEST: " + player.getBestScore());
	}
	
	public void reset()
	{
		wantsReplay = false;
		hide();
		playerScore = 0;
		rgg = null;
	}
	
	public void setTitleMessage(String title)
	{
		topText.setText(title);
		updateObjectPosition();
	}
	
	public void setScore(int score)
	{
		playerScore = score;
	}
	
	
	public boolean wantsReplay()
	{
		return wantsReplay;
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

	@Override
	public void onAnimationStart(int ID, float duration) {
		if(ID == SHOW_ANIM_ID)
		{
			setPos(MainGame.camera.position.x + MainGame.VIRTUAL_WIDTH /2f,MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT / 2f);
			rgg = new RecentGamesGraph(getWidth() + getWidth()/2f - 396f / 2f , topText.getY() + 128f, BoundsAssetManager.getTexture("card"), 396,256, "Score Last 5 Games");
			player.addRecentScore((int) playerScore);
			if(playerScore > player.getBestScore())
			{
				player.setBestScore((int)playerScore);
			}
			ArrayList<Vector2> scores = new ArrayList<Vector2>();
			for(int x = 0; x < player.getRecentScores().size(); x++)
			{
				scores.add(new Vector2(x,player.getRecentScores().get(x)));
			}
			rgg.setPoints(scores);
			player.savePlayerData();
			rgg.show();
		}
	}

	@Override
	public void whileAnimating(int ID, float time, float duration, float delta) {
		if(ID == SHOW_ANIM_ID && time > duration / 6f)
		{
			rgg.setX(dTweener.ExponentialEaseOut(time, getWidth() + getWidth() / 2f - 396f / 2f, -getWidth(), duration));
		}
	}
	
	@Override
	public void onAnimationFinish(int ID) {
		if(ID == SHOW_ANIM_ID)
		{
			this.setX(MainGame.camera.position.x - MainGame.VIRTUAL_WIDTH / 2f);
		}
	}
}
