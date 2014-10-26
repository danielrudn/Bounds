package com.dr.bounds.screens;

import java.util.ArrayList;

import com.DR.dLib.dImage;
import com.DR.dLib.dScreen;
import com.DR.dLib.dText;
import com.DR.dLib.dTweener;
import com.DR.dLib.dUICard;
import com.DR.dLib.dUICardList;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;
import com.dr.bounds.ui.LoadingIcon;
import com.dr.bounds.ui.PlayerCard;

public class InviteScreen extends dUICardList {

	// title card to be displayed at the top
	private dUICard titleCard;
	// timer for showing the transition animation to this screen
	private float showTime = 0;
	private final float SHOW_DURATION = 3f;
	private boolean showAnimation = false;
	// time to show list of cards
	private float showCardTime = 0;
	private final float SHOW_CARD_DURATION = 2f;
	private boolean showCards = false;
	// circle for transition to show screen
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
		// move title card upwards so that it can slide in for the show animation
		titleCard.setY(titleCard.getY() - titleCard.getHeight() - getPadding());
		// move the list up too
		for(int i = 0; i < getSize(); i++)
		{
			getListItem(i).setY(-getListItem(i).getHeight() - getPadding());
		}
		setColor(Color.NAVY);
		// temp, remove
		setAlpha(0);
		Texture circle = new Texture("circle.png");
		circle.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		circleCover = new dImage(0,0,circle);
		circleCover.setColor(new Color(46f/256f, 204f/256f, 113f/256f,1f));
		circleCover.setDimensions(0, 0);
		circleCover.setOriginCenter();
		
		loadingIcon = new LoadingIcon(getWidth()/2f - circle.getWidth() / 2f,getHeight()/2f - circle.getHeight() / 2f,circle);
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		if(showAnimation && showTime <= SHOW_DURATION)
		{
			showTime+=delta;
			//expand circle to cover the screen
			circleCover.setDimensions(dTweener.ExponentialEaseOut(showTime, 0, MainGame.VIRTUAL_HEIGHT * 2.5f, SHOW_DURATION)
							,dTweener.ExponentialEaseOut(showTime, 0, MainGame.VIRTUAL_HEIGHT * 2.5f, SHOW_DURATION));
			circleCover.setOriginCenter();
			// bring in the list and titleCard
			if(showTime >= .6f)
			{
				titleCard.setY(dTweener.ElasticOut(showTime - .6f, -titleCard.getHeight() - getPadding(), titleCard.getHeight() + getPadding(), SHOW_DURATION - .6f,5f));
			}
			if(showTime >= .15f && showTime <= 0.3)
			{
				loadingIcon.start();
			}
		}
		else if(showAnimation && showTime >= SHOW_DURATION)
		{
			// animation finished
			// replace circle cover with the card cover
			
			showAnimation = false;
		}
		
		// checks if showTime >= .6 so that animation doesn't look bad 
		if(showCards && showCardTime <= SHOW_CARD_DURATION && showTime >= .6f)
		{
			showCardTime+=delta;
			for(int x = 0; x < getSize(); x++)
			{
				getListItem(x).setY(dTweener.ElasticOut(showCardTime, -getListItem(x).getHeight() - 24f,
						(titleCard.getHeight() + getPadding() + (x+1)*(getListItem(x).getHeight() + getPadding() + 16f)), SHOW_CARD_DURATION, 5f));
			}
		}
		else if(showCards == false || showTime <= .6f)
		{
			loadingIcon.update(delta);
		}
		
		if(showTime >= SHOW_DURATION && showCardTime >= SHOW_CARD_DURATION)
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
		showCardTime = 0;
		loadingIcon.stop();
	}
	
	@Override
	public void show()
	{
		super.show();
		setTitleCard(titleCard);
		titleCard.setY(-titleCard.getHeight() * 1.5f);
		showAnimation = true;
		circleCover.setDimensions(0, 0);
		circleCover.setOriginCenter();
		circleCover.setPos(MainGame.getVirtualMouseX(), MainGame.getVirtualMouseY());
		showTime = 0;
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
		this.hide();
		newScreen.show();
		MainGame.currentScreen = newScreen;
		MainGame.previousScreen = this;
	}

}