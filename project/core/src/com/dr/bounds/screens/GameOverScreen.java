package com.dr.bounds.screens;

import com.DR.dLib.dImage;
import com.DR.dLib.dText;
import com.DR.dLib.dTweener;
import com.DR.dLib.dUICard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;
import com.dr.bounds.SkinLoader;

public class GameOverScreen extends dScreen {

	// two darker panels containing info about users score and money won
	private dUICard scoreCard, moneyCard;
	// the counters that will be displayed on screen
	private float currentScore = 0, currentMoney = 0;
	// image of the player's skin (or winner if multiplayer)
	private dImage playerImage;
	// Text at the top E.G (Game Over, You Win, You lose...)
	private dText topText;
	// Timer for sliding card in
	private float showTime = 0;
	// duration for slide in
	private final float SHOW_DURATION = 2f;
	
	public GameOverScreen(float x, float y, Texture texture, int playerSkinID) {
		super(x, y, texture);
		setColor(52f/256f, 152f/256f, 219f/256f,1f);
		setPadding(32f);
		
		playerImage = new dImage(0,0, SkinLoader.getTextureForSkinID(playerSkinID));
		playerImage.setDimensions(128f, 128f);
		
		topText = new dText(0,0,64f,"GAME OVER");
		topText.setColor(Color.WHITE);
		
		scoreCard = new dUICard(0,0,texture);
		scoreCard.setColor(41f/256f, 128f/256f, 185f/256f, 1f);
		scoreCard.setHasShadow(false);
		scoreCard.setDimensions(MainGame.VIRTUAL_WIDTH, 128f);
		scoreCard.setPaddingLeft(32f);
		dText scoreCardText = new dText(0,0,72f, "Score: 0");
		scoreCardText.setColor(Color.WHITE);
		scoreCard.addObject(scoreCardText, dUICard.LEFT, dUICard.CENTER);
		
		moneyCard = new dUICard(0,0,texture);
		moneyCard.setColor(scoreCard.getColor());
		moneyCard.setHasShadow(false);
		moneyCard.setDimensions(MainGame.VIRTUAL_WIDTH, 128f);
		moneyCard.setPaddingLeft(32f);
		dText moneyCardText = new dText(0,0,72f, "Dots: 0");
		moneyCardText.setColor(Color.WHITE);
		moneyCard.addObject(moneyCardText, dUICard.LEFT, dUICard.CENTER);
		
		addObject(topText,dUICard.CENTER,dUICard.TOP);
		addObject(playerImage,dUICard.CENTER, dUICard.TOP);
		playerImage.setPos(getX() + getWidth()/2f - playerImage.getWidth()/2f, getY() + 356f - playerImage.getHeight()/2f);
		addObject(moneyCard,dUICard.LEFT_NO_PADDING,dUICard.BOTTOM);
		moneyCard.setY(moneyCard.getY() - 256f);
		addObjectOnTopOf(scoreCard,getIndexOf(moneyCard));
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		currentScore = dTweener.MoveToAndSlow(currentScore, 9, 1.5f * delta);
		((dText)scoreCard.getObject(0)).setText("score: " + Float.toString((int)currentScore+1));
		currentMoney = dTweener.MoveToAndSlow(currentMoney, 276, 1.5f * delta);
		((dText)moneyCard.getObject(0)).setText("dots: " + Float.toString((int)currentMoney+1));
		
		if(isVisible())
		{
			if(showTime <= SHOW_DURATION)
			{
				showTime+=delta;
				setY(dTweener.ElasticOut(showTime, MainGame.camera.position.y + MainGame.VIRTUAL_HEIGHT / 2f, -MainGame.VIRTUAL_HEIGHT, SHOW_DURATION, 8f));
			}
		}
	}
	
	@Override
	public void render(SpriteBatch batch)
	{
		super.render(batch);
	}

	@Override
	public void show()
	{
		super.show();
		setPos(MainGame.camera.position.x - MainGame.VIRTUAL_WIDTH / 2f,MainGame.camera.position.y + MainGame.VIRTUAL_HEIGHT / 2f);
		showTime = 0;
	}
}
