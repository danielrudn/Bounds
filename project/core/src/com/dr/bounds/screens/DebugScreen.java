package com.dr.bounds.screens;

import com.DR.dLib.ui.dButton;
import com.DR.dLib.ui.dScreen;
import com.DR.dLib.ui.dText;
import com.DR.dLib.ui.dUICard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dr.bounds.MainGame;
import com.dr.bounds.RequestHandler;

public class DebugScreen extends dScreen {

	private String debugText = "";
	private float uptime = 0;
	private dUICard buttonsCard;
	private dText debug;
	dButton clearButton, inviteButton, inboxButton, leaveButton, sendFirstButton, signInButton, hideButton, statusButton;
	private RequestHandler requestHandler;
	private final Color buttonColor = Color.NAVY;
	
	private DialogBoxScreen exitDialog;
	
	public DebugScreen(float x, float y, Texture texture, Texture button) {
		super(x, y, texture);
		requestHandler = MainGame.requestHandler;
		setAlpha(0);

		buttonsCard = new dUICard(MainGame.VIRTUAL_WIDTH-250,0, texture);
		buttonsCard.setDimensions(256f,MainGame.VIRTUAL_HEIGHT);
		buttonsCard.setHasShadow(false);
		buttonsCard.setColor(Color.LIGHT_GRAY);
		debug = new dText(0,0,32f,debugText);
		debug.setMultiline(true);
		addObject(debug, dUICard.LEFT, dUICard.TOP);
		
		buttonsCard.addObject(new dText(0,0,44f,"debug menu"),dUICard.CENTER,dUICard.TOP);
		clearButton = new dButton(0,0,new Sprite(button),"clear");
		clearButton.setColor(buttonColor);
		clearButton.setTextSize(48f);
		clearButton.setDimensions(192f, 64f);
		
		inviteButton = new dButton(0,0,new Sprite(button),"invite");
		inviteButton.setColor(buttonColor);
		inviteButton.setTextSize(48f);
		inviteButton.setDimensions(192f, 64f);
		
		inboxButton = new dButton(0,0,new Sprite(button),"inbox");
		inboxButton.setColor(buttonColor);
		inboxButton.setTextSize(48f);
		inboxButton.setDimensions(192f, 64f);
		
		leaveButton = new dButton(0,0,new Sprite(button),"leave");
		leaveButton.setColor(buttonColor);
		leaveButton.setTextSize(48f);
		leaveButton.setDimensions(192f, 64f);
		
		sendFirstButton = new dButton(0,0,new Sprite(button),"play");
		sendFirstButton.setColor(buttonColor);
		sendFirstButton.setTextSize(48f);
		sendFirstButton.setDimensions(192f, 64f);
		
		signInButton = new dButton(0,0,new Sprite(button), "sign in");
		signInButton.setColor(buttonColor);
		signInButton.setTextSize(48f);
		signInButton.setDimensions(192f, 64f);
		
		hideButton = new dButton(0,0,new Sprite(button), "hide bar");
		hideButton.setColor(buttonColor);
		hideButton.setTextSize(48f);
		hideButton.setDimensions(192f, 64f);
		
		statusButton = new dButton(0,0,new Sprite(button), "status");
		statusButton.setColor(buttonColor);
		statusButton.setTextSize(48f);
		statusButton.setDimensions(192f, 64f);
		
		buttonsCard.addObjectUnder(clearButton, dUICard.CENTER, 0);
		buttonsCard.addObjectUnder(inviteButton, dUICard.CENTER, 1);
		buttonsCard.addObjectUnder(inboxButton,dUICard.CENTER,2);
		buttonsCard.addObjectUnder(leaveButton,dUICard.CENTER,3);
		buttonsCard.addObjectUnder(sendFirstButton,dUICard.CENTER,4);
		buttonsCard.addObjectUnder(signInButton,dUICard.CENTER,5);
		buttonsCard.addObjectUnder(hideButton,dUICard.CENTER,6);
		buttonsCard.addObjectUnder(statusButton,dUICard.CENTER,7);
		
		addObject(buttonsCard,dUICard.RIGHT_NO_PADDING,dUICard.TOP_NO_PADDING);
		
		dUICard exitCard = new dUICard(0,0,texture);
		exitCard.setDimensions(MainGame.VIRTUAL_WIDTH - 256f,128f);
		dText exitConfirm = new dText(0,0,48f,"Quit Bounds?");
		exitConfirm.setMultiline(true);
		dButton quitButton = new dButton(0,0,new Sprite(texture),"yes");
		quitButton.setDimensions(exitCard.getWidth()/2f, 64f);
		quitButton.setTextColor(Color.BLACK);
		quitButton.setTextSize(48f);
		dButton stayButton = new dButton(0,0, new Sprite(texture),"no");
		stayButton.setDimensions(exitCard.getWidth()/2f, 64f);
		stayButton.setTextColor(Color.BLACK);
		stayButton.setTextSize(48f);
		exitCard.addObject(stayButton, dUICard.LEFT_NO_PADDING, dUICard.BOTTOM_NO_PADDING);
		exitCard.addObject(quitButton, dUICard.RIGHT_NO_PADDING, dUICard.BOTTOM_NO_PADDING);
		exitCard.addObject(exitConfirm, dUICard.CENTER, dUICard.TOP);
		
		exitDialog = new DialogBoxScreen(0,0,texture, exitCard);
	}
	
	@Override
	public void render(SpriteBatch batch)
	{
		super.render(batch);
		exitDialog.render(batch);
	}
	
	@Override
	public void update(float delta)
	{
		if(isVisible())
		{
			super.update(delta);
			exitDialog.update(delta);
			uptime+=Gdx.graphics.getDeltaTime();
			debug.setText("DEBUG:\n------------------------------------------\n"
					+ "FPS: " + Gdx.graphics.getFramesPerSecond() + "\nDeltaTime: " + Gdx.graphics.getDeltaTime() + "\nUptime: " + uptime +
					"\n------------------------------------------"
					+ debugText);
			
			buttonsCard.update(Gdx.graphics.getDeltaTime());
			if(clearButton.isClicked())
			{
				debugText = "";
			}
			else if(inviteButton.isClicked())
			{
				debugText+="\nInvite Clicked!";
				//requestHandler.requestInviteActivity();
				// temp, remove
			//	requestHandler.loadRecentlyPlayedWithPlayers();
				//MainGame.inviteScreen.show();
				MainGame.currentScreen.switchScreen(MainGame.inviteScreen);
			}
			else if(inboxButton.isClicked())
			{
				debugText+="\nInbox Clicked!";
				// requestHandler.requestInboxActivity();
		//		MainGame.requestHandler.loadInvitations();
				MainGame.currentScreen.switchScreen(MainGame.inboxScreen);
			}
			else if(leaveButton.isClicked())
			{
				debugText+="\nLeave Clicked!";
				//requestHandler.leaveRoom();
			}
			else if(sendFirstButton.isClicked())
			{
				MainGame.currentScreen.switchScreen(MainGame.gameScreen);
			}
			else if(signInButton.isClicked())
			{
				debugText+="\nSigning in...";
				requestHandler.requestSignIn();
			}
			else if(hideButton.isClicked())
			{
				this.hide();
				buttonsCard.hide();
			}
			else if(statusButton.isClicked())
			{
				debugText+="\nisConnected: " + requestHandler.isConnected() + "\nisConnecting: " + requestHandler.isConnecting();
			}
		
			if(exitDialog.isVisible() && ((dButton) exitDialog.getDialogBox().getObject(1)).isClicked())// quit
			{
				Gdx.app.exit();
			}
			else if(exitDialog.isVisible() && ((dButton) exitDialog.getDialogBox().getObject(0)).isClicked())// stay
			{
				exitDialog.hide();
			}
			
			if(MainGame.getVirtualMouseY() < 10f)
			{
			//	this.show();
			//	buttonsCard.show();
			}
			
		}
	}
	
	public void addText(String t)
	{
		debugText+=t;
		GameScreen.log(t);
	}
	
	@Override
	public void goBack() {
		// No previous screen in main menu, so show exit dialog on back
	//	Gdx.app.exit();
		if(exitDialog.isVisible())
		{
			exitDialog.hide();
		}
		else
		{
			exitDialog.show();
		}
	}

	@Override
	public void switchScreen(dScreen newScreen) {
		newScreen.show();
		MainGame.currentScreen = newScreen;
		MainGame.previousScreen = this;
		this.hide();
	}

}
