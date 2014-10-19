package com.dr.bounds.ui;

import com.DR.dLib.dButton;
import com.DR.dLib.dImage;
import com.DR.dLib.dText;
import com.DR.dLib.dUICard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dr.bounds.MainGame;
import com.dr.bounds.SkinLoader;

public class PlayerCard extends dUICard {

	/*
	 * Player cards that appear in a list when inviting someone
	 */
	
	// image of the player's ball
	private dImage skinImage;
	// players name 
	private dText nameText;
	// invite button
	private dButton inviteButton;
	
	public PlayerCard(float x, float y, Texture texture, int skinID, String name) {
		super(x, y, texture);
		setDimensions(MainGame.VIRTUAL_WIDTH - 128f, 128f);
		setClickable(true);
		setPaddingLeft(16f);
		skinImage = new dImage(0,0,SkinLoader.getTextureForSkinID(skinID));
		nameText = new dText(0,0,64f,name);
		inviteButton = new dButton(0,0,new Sprite(texture), "invite");
		inviteButton.setTextColor(Color.BLACK);
		
		addObject(skinImage, dUICard.LEFT, dUICard.CENTER);
		addObject(nameText, dUICard.CENTER, dUICard.CENTER);
	}
	
	public PlayerCard(float x, float y, Texture texture, TextureRegion skin, String name) {
		super(x, y, texture);
		setDimensions(MainGame.VIRTUAL_WIDTH - 128f, 128f);
		setClickable(true);
		setPaddingLeft(16f);
		skinImage = new dImage(0,0, skin);
		nameText = new dText(0,0,64f,name);
		inviteButton = new dButton(0,0,new Sprite(texture), "invite");
		inviteButton.setTextColor(Color.BLACK);
		
		addObject(skinImage, dUICard.LEFT, dUICard.CENTER);
		addObject(nameText, dUICard.CENTER, dUICard.CENTER);
	}

}
