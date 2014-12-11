package com.dr.bounds.screens;

import com.DR.dLib.animations.AnimationStatusListener;
import com.DR.dLib.animations.SlideElasticAnimation;
import com.DR.dLib.animations.SlideInOrderAnimation;
import com.DR.dLib.ui.dButton;
import com.DR.dLib.ui.dScreen;
import com.DR.dLib.ui.dUICard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dr.bounds.MainGame;

public class MenuScreen extends dScreen implements AnimationStatusListener {

	// animations
	private SlideInOrderAnimation showButtonsAnimation;
	private final int SHOW_BUTTONS_ID = 123;
	private SlideElasticAnimation hideAnimation;
	private final int HIDE_ANIM_ID = 12345;
	// BUTTONS
	private dButton playButton;
	private dButton multiplayerButton;
	private dButton skinsButton;
	private dButton leaderboardsButton;
	private dButton achievementsButton;
	
	public MenuScreen(float x, float y, Texture texture) {
		super(x, y, texture);
		setPaddingTop(16f);
	
		hideAnimation = new SlideElasticAnimation(2f, this, HIDE_ANIM_ID,MainGame.VIRTUAL_WIDTH, 0, this);
		setHideAnimation(hideAnimation);
		
		//fix
		Texture buttonTexture = new Texture("button.png");
		buttonTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		playButton = new dButton(0,0, new Sprite(buttonTexture), "play");
		playButton.setTextSize(92f);
		playButton.setColor(new Color(52f/256f, 152f/256f, 219f/256f,1f));
		
		multiplayerButton = new dButton(0,0, new Sprite(buttonTexture), "invite");
		multiplayerButton.setTextSize(92f);
		multiplayerButton.setColor(new Color(52f/256f, 152f/256f, 219f/256f,1f));
		
		skinsButton = new dButton(0,0, new Sprite(buttonTexture), "inbox");
		skinsButton.setTextSize(92f);
		skinsButton.setColor(new Color(52f/256f, 152f/256f, 219f/256f,1f));
		
		leaderboardsButton = new dButton(0,0, new Sprite(buttonTexture), "scores");
		leaderboardsButton.setTextSize(92f);
		leaderboardsButton.setColor(new Color(52f/256f, 152f/256f, 219f/256f,1f));
		
		achievementsButton = new dButton(0,0, new Sprite(buttonTexture), "trophies");
		achievementsButton.setTextSize(92f);
		achievementsButton.setColor(new Color(52f/256f, 152f/256f, 219f/256f,1f));
		
		addObject(playButton, dUICard.CENTER, dUICard.TOP);
		playButton.setY(playButton.getY() + 364f);
		addObjectUnder(multiplayerButton,getIndexOf(playButton));
		addObjectUnder(skinsButton,getIndexOf(multiplayerButton));
		addObjectUnder(leaderboardsButton, getIndexOf(skinsButton));
		addObjectUnder(achievementsButton, getIndexOf(leaderboardsButton));
		
		playButton.setX(playButton.getX() - MainGame.VIRTUAL_WIDTH);
		multiplayerButton.setX(multiplayerButton.getX() - MainGame.VIRTUAL_WIDTH);
		skinsButton.setX(skinsButton.getX() - MainGame.VIRTUAL_WIDTH);
		leaderboardsButton.setX(leaderboardsButton.getX() - MainGame.VIRTUAL_WIDTH);
		achievementsButton.setX(achievementsButton.getX() - MainGame.VIRTUAL_WIDTH);
		
		
		showButtonsAnimation = new SlideInOrderAnimation(2f, this, SHOW_BUTTONS_ID, MainGame.VIRTUAL_WIDTH, new dButton[]{playButton, multiplayerButton, skinsButton, leaderboardsButton, achievementsButton});
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		if(hideAnimation.isActive())
		{
			hideAnimation.update(delta);
		}
		if(showButtonsAnimation.isActive())
		{
			showButtonsAnimation.update(delta);
		}
		if(playButton.isClicked())
		{
			switchScreen(MainGame.gameScreen);
		}
	}
	
	@Override
	public void show()
	{
		super.show();
		showButtonsAnimation.start();
	}

	@Override
	public void goBack() {
		if(MainGame.previousScreen != null)
		{
			//switchScreen(MainGame.previousScreen);
		}
	}

	@Override
	public void switchScreen(dScreen newScreen) {
		//this.hide();
		newScreen.show();
		MainGame.currentScreen = newScreen;
		MainGame.previousScreen = this;
	}

	@Override
	public void onAnimationStart(int ID, float duration) {
		if(ID == HIDE_ANIM_ID)
		{
			setVisible(true);
		}
	}

	@Override
	public void whileAnimating(int ID, float time, float duration, float delta) {
	}
	
	@Override
	public void onAnimationFinish(int ID) {
		if(ID == HIDE_ANIM_ID)
		{
			setVisible(false);
		}
	}

}