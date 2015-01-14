package com.dr.bounds.ui;

import com.DR.dLib.dTweener;
import com.DR.dLib.animations.AnimationStatusListener;
import com.DR.dLib.animations.dAnimation;
import com.DR.dLib.ui.dButton;
import com.DR.dLib.ui.dImage;
import com.DR.dLib.ui.dText;
import com.DR.dLib.ui.dUICard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;
import com.dr.bounds.SkinLoader;
import com.dr.bounds.animations.ShopItemCardExpandAnimation;
import com.dr.bounds.animations.ShopItemCardShrinkAnimation;

public class ShopItemCard extends dUICard implements AnimationStatusListener {

	private dText itemName;
	private dText itemPrice;
	private dImage itemImage;
	private dUICard imageCard;
	private byte skinID;
	public static final float CARD_HEIGHT = 128f, CARD_WIDTH = MainGame.VIRTUAL_WIDTH - 256f;
	private dAnimation expandAnimation;
	private final int EXPAND_ANIM_ID = 2015;
	// for expand anim
	private float startX = 0, startY = 0, endY = 0, endX = 0;
	private boolean expanded = false;
	// shrink
	private final int SHRINK_ANIM_ID = 2012;
	private dAnimation shrinkAnimation;
	// set/buy button
	private dButton acceptButton;
	private dButton cancelButton;
	// black screen to fade in when an item is clicked
	private dImage fadeCover;
	
	public ShopItemCard(float x, float y, Texture texture, String name, int price, byte id) {
		super(x, y, texture);
		this.setClickable(true);
		this.setDimensions(CARD_WIDTH, CARD_HEIGHT);
		skinID = id;
		itemName = new dText(0,0,getFontSize(name,48f,10),name);
		itemName.setColor(Color.BLACK);
		
		itemPrice = new dText(0,0,48f,Integer.toString(price));
		itemPrice.setColor(44f/256f, 62f/256f, 80f/256f,1f);
		
		imageCard = new dUICard(0,0,texture);
		imageCard.setDimensions(CARD_WIDTH*.25f, CARD_HEIGHT);
		imageCard.setHasShadow(false);
		imageCard.setColor(246f/256f, 246f/256f, 246f/256f, 1f);
		itemImage = new dImage(0,0,SkinLoader.getTextureForSkinID((int)id));
	
		imageCard.addObject(itemImage,dUICard.CENTER, dUICard.CENTER);
		addObject(imageCard,dUICard.LEFT_NO_PADDING, dUICard.TOP_NO_PADDING);
		addObjectUnder(itemName, getIndexOf(imageCard));
		itemName.setPos(imageCard.getX() + imageCard.getWidth() + getPadding()*1.5f, getY() + getPadding());
		addObjectUnder(itemPrice, getIndexOf(itemName));
		itemPrice.setY(getY() + getHeight() - getPadding()*2f - itemPrice.getHeight());
		
		if(Player.isSkinUnlocked(id))
		{
			acceptButton = new dButton(MainGame.VIRTUAL_WIDTH/2f, MainGame.VIRTUAL_HEIGHT*2f, new Sprite(texture), "SET", new Texture("circle.png"), 2f);
			acceptButton.setColor(46f/256f, 204f/256f, 113f/256f,1f);
		}
		else
		{
			acceptButton = new dButton(MainGame.VIRTUAL_WIDTH/2f, MainGame.VIRTUAL_HEIGHT*2f, new Sprite(texture), "BUY", new Texture("circle.png"), 2f);
			acceptButton.setColor(Color.GRAY);
		}
		acceptButton.setTextSize(92f);
		acceptButton.setDimensions((CARD_WIDTH + 128f)/2f, 128f);
		
		cancelButton = new dButton(acceptButton.getX() - acceptButton.getWidth(), MainGame.VIRTUAL_HEIGHT*2f, new Sprite(texture), "BACK");
		cancelButton.setTextSize(92f);
		cancelButton.setDimensions((CARD_WIDTH + 128f)/2f, 128f);
		cancelButton.setColor(231f/256f, 76f/256f, 60f/256f,1f);
		
		expandAnimation = new ShopItemCardExpandAnimation(1f, this, EXPAND_ANIM_ID, this);
		shrinkAnimation = new ShopItemCardShrinkAnimation(1f,this,SHRINK_ANIM_ID,this);
		
		fadeCover = new dImage(0,0,texture);
		fadeCover.setDimensions(MainGame.VIRTUAL_WIDTH, MainGame.VIRTUAL_HEIGHT);
		fadeCover.setColor(0,0,0,0);
		
	}
	
	@Override
	public void render(SpriteBatch batch)
	{
		fadeCover.render(batch);
		super.render(batch);
		acceptButton.render(batch);
		cancelButton.render(batch);
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		if(expandAnimation.isActive())
		{
			expandAnimation.update(delta);
		}
		if(shrinkAnimation.isActive())
		{
			shrinkAnimation.update(delta);
		}
		if(expanded)
		{
			acceptButton.update(delta);
			cancelButton.update(delta);
			if(acceptButton.isClicked())
			{
				MainGame.setPlayerSkin(skinID);
			}
		}
	}
	
	private float getFontSize(String text, float defaultSize, float maxLength)
	{
		if(text.length() >= maxLength)
		{
			return defaultSize / text.length() * maxLength;
		}
		return defaultSize;
	}
	
	public void expand()
	{
		if(expandAnimation.isActive() == false && shrinkAnimation.isActive() == false && expanded == false)
		{
			expandAnimation.start();
		}
	}
	
	public void shrink()
	{
		if(shrinkAnimation.isActive() == false && expandAnimation.isActive() == false && expanded)
		{
			shrinkAnimation.start();
		}
	}
	
	public dButton getCancelButton()
	{
		return cancelButton;
	}
	
	public boolean isFinishedShrink()
	{
		return shrinkAnimation.isFinished();
	}

	@Override
	public void onAnimationStart(int ID, float duration) {
		if(ID == EXPAND_ANIM_ID)
		{
			startX = getX();
			startY = getY();
			this.setClipping(false);
			imageCard.setClipping(false);
			expanded = true;
			//acceptButton.setY(dTweener.ExponentialEaseOut(time, MainGame.VIRTUAL_HEIGHT, -MainGame.VIRTUAL_HEIGHT + imageCard.getY() + imageCard.getHeight(), duration));
			acceptButton.setY(MainGame.VIRTUAL_HEIGHT / 2f);
			cancelButton.setY(acceptButton.getY());
			acceptButton.setAlpha(0);
			cancelButton.setAlpha(0);
		}
		else if(ID == SHRINK_ANIM_ID)
		{
			setClipping(true);
			imageCard.setClipping(true);
		}
	}

	@Override
	public void whileAnimating(int ID, float time, float duration, float delta) {
		if(ID == EXPAND_ANIM_ID)
		{
			setDimensions(dTweener.ExponentialEaseOut(time, ShopItemCard.CARD_WIDTH, 128f, duration)
					, dTweener.ExponentialEaseOut(time, ShopItemCard.CARD_HEIGHT, 128f, duration));
			itemImage.setPos(getX() + imageCard.getWidth() / 2f - itemImage.getWidth() / 2f, getY() + imageCard.getHeight() / 2f - itemImage.getHeight() / 2f);
			itemName.setPos(getX() + imageCard.getWidth() + getPadding()*2f, getY() + getPadding()*2f);
			itemPrice.setPos(getX() + imageCard.getWidth() + getPadding()*2f, itemName.getY() + itemName.getHeight() + getPadding()*4f);	
			if(time < duration/2f)
			{
				acceptButton.setAlpha(dTweener.LinearEase(time,0, 1f, duration/2f));
				cancelButton.setAlpha(acceptButton.getColor().a);
			}
			fadeCover.setAlpha(dTweener.LinearEase(time, 0, .4f, duration));
		}
		else if(ID == SHRINK_ANIM_ID)
		{
			setPosition(dTweener.ExponentialEaseOut(time, endX,-endX + startX, duration),dTweener.ExponentialEaseOut(time, endY,-endY + startY, duration));
			itemImage.setPos(getX() + imageCard.getWidth() / 2f - itemImage.getWidth() / 2f, getY() + imageCard.getHeight() / 2f - itemImage.getHeight() / 2f);
			itemName.setPos(getX() + imageCard.getWidth() + getPadding()*2f, getY() + getPadding()*2f);
			itemPrice.setPos(getX() + imageCard.getWidth() + getPadding()*2f, itemName.getY() + itemName.getHeight() + getPadding()*4f);
			if(time < duration/2f)
			{
				acceptButton.setAlpha(dTweener.ExponentialEaseOut(time,1f, -1f, duration/2f));
				cancelButton.setAlpha(acceptButton.getColor().a);
			}
			fadeCover.setAlpha(dTweener.ExponentialEaseOut(time, .4f, -.395f, duration));
		}
	}
	
	@Override
	public void onAnimationFinish(int ID) {
		if(ID == EXPAND_ANIM_ID)
		{
			setClickable(false);
			endX = getX();
			endY = getY();
		}
		else if(ID == SHRINK_ANIM_ID)
		{
			setClickable(true);
			expanded = false;
			fadeCover.setAlpha(0);
			acceptButton.setAlpha(1f);
			cancelButton.setAlpha(1f);
			acceptButton.setY(MainGame.VIRTUAL_HEIGHT);
			cancelButton.setY(acceptButton.getY());
		}
	}

}