package com.dr.bounds.screens;

import com.DR.dLib.ui.dScreen;
import com.DR.dLib.dTweener;
import com.DR.dLib.ui.dUICard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;

public class DialogBoxScreen extends dScreen {

	// time for the screen to fade and dialog box to slide in
	private float showTime = 4f;
	// duration of fading/sliding in
	private final float SHOW_DURATION = 2f;
	// dialog box that will be shown
	private dUICard dialogBox;
	// time for screen to fade/slide out
	private float hideTime = 4f;
	// hide duration
	private final float HIDE_DURATION = 2f;
	// whether or not the box is in process of hiding
	private boolean isHiding = false;
	
	
	public DialogBoxScreen(float x, float y, Texture texture) {
		super(x, y, texture);
		setColor(0,0,0,0);
		dialogBox = new dUICard(0,0,texture);
		dialogBox.setColor(Color.WHITE);
		dialogBox.setDimensions(MainGame.VIRTUAL_WIDTH - 128f, 256f);
		
		addObject(dialogBox,dUICard.CENTER, dUICard.CENTER);
		setVisible(false);
	}
	
	public DialogBoxScreen(float x, float y, Texture texture, dUICard dialogBox)
	{
		super(x,y,texture);
		setColor(0,0,0,0);
		this.dialogBox = dialogBox;
		addObject(dialogBox,dUICard.CENTER, dUICard.CENTER);
		setVisible(false);
	}
	
	@Override
	public void update(float delta)
	{
		super.update(delta);
		if(showTime <= SHOW_DURATION && isVisible() && isHiding == false)
		{
			showTime+=delta;
			if(showTime <= SHOW_DURATION / 3f)
			{
				setAlpha(dTweener.LinearEase(showTime, 0, .5f, SHOW_DURATION/3f));
			}
			dialogBox.setX(dTweener.ElasticOut(showTime, getX() + getWidth() + 15f, (getX() + getWidth()/2f - dialogBox.getWidth()/2f) - (getX() + getWidth() + 15f)
					, SHOW_DURATION, 6f));
		}
		else if(hideTime <= HIDE_DURATION)
		{
			hideTime+=delta;
			if(hideTime <= HIDE_DURATION / 2f)
			{
				setAlpha(dTweener.LinearEase(hideTime, .5f, -.5f, HIDE_DURATION / 2f));
			}
			dialogBox.setX(dTweener.ElasticOut(hideTime, (getX() + getWidth()/2f - dialogBox.getWidth()/2f), MainGame.VIRTUAL_WIDTH, HIDE_DURATION));
		}
		else if(hideTime >= HIDE_DURATION && isHiding == true)
		{
			super.hide();
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
		showTime = 0;
		hideTime = HIDE_DURATION + 1f;
		setPos(MainGame.camera.position.x - MainGame.VIRTUAL_WIDTH/2f, MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT/2f);
		dialogBox.setX(getX() + getWidth() + 15f);
		setAlpha(0);
		isHiding = false;
	}
	
	@Override
	public void hide()
	{
		if(isVisible())
		{
			//don't call super.hide until animations have finished
			hideTime = 0;
			isHiding = true;
		}
	}
	
	public void setDialogBox(dUICard dialogBox)
	{
		this.dialogBox = dialogBox;
		updateObjectPosition();
	}
	
	public dUICard getDialogBox()
	{
		return dialogBox;
	}
	
	public void setIsHiding(boolean hide)
	{
		isHiding = hide;
	}
	
	public boolean isHiding()
	{
		return isHiding;
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
