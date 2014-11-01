package com.dr.bounds.screens;

import java.util.ArrayList;

import com.DR.dLib.animations.AnimationStatusListener;
import com.DR.dLib.animations.ExpandAnimation;
import com.DR.dLib.animations.ShrinkAnimation;
import com.DR.dLib.ui.dImage;
import com.DR.dLib.ui.dScreen;
import com.DR.dLib.ui.dText;
import com.DR.dLib.dTweener;
import com.DR.dLib.ui.dUICard;
import com.DR.dLib.ui.dUICardList;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;
import com.dr.bounds.animations.SlideInArrayAnimation;
import com.dr.bounds.animations.SlideOutArrayAnimation;
import com.dr.bounds.ui.LoadingIcon;
import com.dr.bounds.ui.PlayerCard;

public class InviteScreen extends dUICardList implements AnimationStatusListener {

	// title card to be displayed at the top
	private dUICard titleCard;
	// timer for showing the transition animation to this screen
	private final float SHOW_DURATION = 3f;
	// time to show list of cards
	private boolean showCards = false;
	private final float SHOW_CARD_DURATION = 2f;
	// animation that plays when showing this screen
	private ExpandAnimation startAnimation;
	private SlideInArrayAnimation cardsAnimation;
	private static final int SHOW_ANIM_ID = 12345;
	private static final int HIDE_ANIM_ID = 34567;
	private static final int CARDS_ANIM_ID = 23456;
	private ShrinkAnimation hideAnimation;
	private SlideOutArrayAnimation hideCardsAnimation;
	private static final int HIDE_CARDS_ID = 45678;
	private dImage circleCover;
	// loading icon that shows while players are being loaded
	private LoadingIcon loadingIcon;
	
	public InviteScreen(float x, float y, Texture texture, ArrayList<dUICard> list) {
		super(x, y, texture, list);
		titleCard = new dUICard(0,0,texture);
		titleCard.setDimensions(getWidth(), 92f);
		titleCard.setColor(new Color(16f/256f, 174f/256f, 73f/256f,1f));
		titleCard.setHasShadow(false);
		dText titleText = new dText(0,0,64f,"INVITE");
		titleText.setColor(Color.WHITE);
		titleCard.addObject(titleText, dUICard.CENTER, dUICard.CENTER);
		setTitleCard(titleCard);
		setColor(Color.NAVY);
		
		cardsAnimation = new SlideInArrayAnimation(list, SHOW_CARD_DURATION, this, CARDS_ANIM_ID);
		// temp, remove
		setAlpha(0);
		Texture circle = new Texture("circle.png");
		circle.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		circleCover = new dImage(0,0,circle);
		startAnimation = new ExpandAnimation(circleCover, SHOW_DURATION, this, SHOW_ANIM_ID, new Color(46f/256f, 204f/256f, 113f/256f,1f), MainGame.VIRTUAL_HEIGHT * 2.5f);
		setShowAnimation(startAnimation);
		hideAnimation = new ShrinkAnimation(circleCover, 1f, this, HIDE_ANIM_ID, 0, MainGame.VIRTUAL_HEIGHT * 2.5f);
		hideCardsAnimation = new SlideOutArrayAnimation(list, 1.25f, this, HIDE_CARDS_ID);
		setHideAnimation(hideAnimation);
		loadingIcon = new LoadingIcon(getWidth()/2f - circle.getWidth() / 2f,getHeight()/2f - circle.getHeight() / 2f,circle);
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		updateAnimations(delta);
	
		if(startAnimation.isFinished() && cardsAnimation.isFinished())
		{
			for(int x = 0; x < getSize(); x++)
			{
				if(getListItem(x).isVisible() && getListItem(x).isClicked())
				{
					MainGame.requestHandler.sendInvite(((PlayerCard) getListItem(x)).getPlayerID());
					MainGame.currentScreen.switchScreen(MainGame.waitingRoomScreen);
				}
			}
		}
		
	}
	
	private void updateAnimations(float delta)
	{
		if(startAnimation.isActive())
		{
			startAnimation.update(delta);
		}
		if(cardsAnimation.isActive())
		{
			cardsAnimation.update(delta);
		}
		else if(cardsAnimation.isActive() == false && showCards == false)
		{
			loadingIcon.update(delta);
		}
		if(hideAnimation.isActive())
		{
			hideAnimation.update(delta);
			hideCardsAnimation.update(delta);
		}
		
		if(showCards && (startAnimation.isFinished() || startAnimation.getTime() >= 0.6f) && cardsAnimation.isActive() == false)
		{
			cardsAnimation.start();
		}
	}
	
	@Override
	public void render(SpriteBatch batch)
	{
		circleCover.render(batch);
		loadingIcon.render(batch);
		super.render(batch);
	}
	
	public void showCards()
	{
		showCards = true;
	}
	
	@Override
	public void goBack() {
		if(MainGame.previousScreen != null)
		{
			switchScreen(MainGame.debugCard);
			startAnimation.stop();
			cardsAnimation.stop();
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
		if(ID == SHOW_ANIM_ID)
		{	
			setTitleCard(titleCard);
			titleCard.setY(-titleCard.getHeight() * 1.5f);
			for(int x = 0; x < getSize(); x++)
			{
				getItem(x).setY(MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT / 2f - getItem(x).getHeight() - getPadding());
			}
			loadingIcon.start();
		}
		else if(ID == HIDE_ANIM_ID)
		{
			setVisible(true);
			hideCardsAnimation.start();
			loadingIcon.stop();
		}
		if(ID == CARDS_ANIM_ID)
		{
			loadingIcon.stop();
		}
	}

	@Override
	public void whileAnimating(int ID, float time, float duration, float delta) {
		if(ID == SHOW_ANIM_ID)
		{
			// bring in the title card
			if(time >= .6f)
			{
				titleCard.setY(dTweener.ElasticOut(time - .6f, -titleCard.getHeight() - getPadding(), titleCard.getHeight() + getPadding(), duration - .6f,5f));
			}
		}
		else if(ID == HIDE_ANIM_ID)
		{
			if(time >= .15f)
			{
				titleCard.setY(dTweener.ExponentialEaseOut(time - .15f, MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT / 2f,  -titleCard.getHeight() - getPadding() * 1.5f, duration - .15f));
			}
		}
	}
	
	@Override
	public void onAnimationFinish(int ID) {
		if(ID == SHOW_ANIM_ID)
		{
			// animation finished
			// replace circle cover with the card cover
		}
		else if(ID == HIDE_ANIM_ID)
		{
			MainGame.previousScreen = null;
		}
		else if(ID == CARDS_ANIM_ID)
		{
			showCards = false;
		}
	}

}
