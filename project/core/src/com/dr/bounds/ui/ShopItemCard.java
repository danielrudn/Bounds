package com.dr.bounds.ui;

import com.DR.dLib.ui.dImage;
import com.DR.dLib.ui.dText;
import com.DR.dLib.ui.dUICard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.dr.bounds.SkinLoader;

public class ShopItemCard extends dUICard {

	private dText itemName;
	private dText itemPrice;
	private dImage itemImage;
	private dUICard imageCard;
	private static final float CARD_HEIGHT = 192f, CARD_WIDTH = 192f;
	
	public ShopItemCard(float x, float y, Texture texture, String name, int price, byte id) {
		super(x, y, texture);
		this.setClickable(true);
		this.setDimensions(CARD_WIDTH, CARD_HEIGHT);
		itemName = new dText(0,0,getFontSize(name,40f,10 ),name);
		itemName.setColor(Color.BLACK);
		
		itemPrice = new dText(0,0,32f,Integer.toString(price));
		itemPrice.setColor(Color.GRAY);
		
		imageCard = new dUICard(0,0,texture);
		imageCard.setDimensions(CARD_WIDTH, CARD_HEIGHT * .6f);
		imageCard.setHasShadow(false);
		imageCard.setColor(246f/256f, 246f/256f, 246f/256f, 1f);
		itemImage = new dImage(0,0,SkinLoader.getTextureForSkinID((int)id));
	
		imageCard.addObject(itemImage,dUICard.CENTER, dUICard.CENTER);
		addObject(imageCard,dUICard.LEFT_NO_PADDING, dUICard.TOP_NO_PADDING);
		addObjectUnder(itemName, getIndexOf(imageCard));
	//	itemName.setX(getX() + getPadding());
		itemName.setX(getX() + getWidth() / 2f - itemName.getWidth() / 2f);
		addObject(itemPrice,dUICard.CENTER, dUICard.BOTTOM);
	}
	
	private float getFontSize(String text, float defaultSize, float maxLength)
	{
		if(text.length() >= maxLength)
		{
			return defaultSize / text.length() * maxLength;
		}
		return defaultSize;
	}

}