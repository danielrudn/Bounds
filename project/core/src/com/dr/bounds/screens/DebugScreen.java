package com.dr.bounds.screens;

import com.DR.dLib.dButton;
import com.DR.dLib.dScreen;
import com.DR.dLib.dText;
import com.DR.dLib.dUICard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dr.bounds.MainGame;
import com.dr.bounds.RequestHandler;

public class DebugScreen extends dScreen {

	private String debugText = "";
	private float uptime = 0;
	private dUICard buttonsCard;
	private dText debug;
	dButton clearButton, inviteButton, inboxButton, leaveButton, sendFirstButton, sendSecondButton, signInButton, hideButton, statusButton;
	private RequestHandler requestHandler;
	private final Color buttonColor = Color.NAVY;
	
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
		
		sendFirstButton = new dButton(0,0,new Sprite(button),"send '5'");
		sendFirstButton.setColor(buttonColor);
		sendFirstButton.setTextSize(48f);
		sendFirstButton.setDimensions(192f, 64f);
		
		sendSecondButton = new dButton(0,0,new Sprite(button),"send '7'");
		sendSecondButton.setColor(buttonColor);
		sendSecondButton.setTextSize(48f);
		sendSecondButton.setDimensions(192f, 64f);
		
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
		buttonsCard.addObjectUnder(sendSecondButton,dUICard.CENTER,5);
		buttonsCard.addObjectUnder(signInButton,dUICard.CENTER,6);
		buttonsCard.addObjectUnder(hideButton,dUICard.CENTER,7);
		buttonsCard.addObjectUnder(statusButton,dUICard.CENTER,8);
		
		addObject(buttonsCard,dUICard.RIGHT_NO_PADDING,dUICard.TOP_NO_PADDING);
	}
	
	@Override
	public void update(float delta)
	{
		if(isVisible())
		{
			super.update(delta);
			
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
				System.out.println("BOUNDS: invite clicked!");
				requestHandler.loadRecentlyPlayedWithPlayers();
				MainGame.inviteScreen.show();
			}
			else if(inboxButton.isClicked())
			{
				debugText+="\nInbox Clicked!";
				requestHandler.requestInboxActivity();
			}
			else if(leaveButton.isClicked())
			{
				debugText+="\nLeave Clicked!";
				//requestHandler.leaveRoom();
				requestHandler.showLeaderboard(MainGame.SKIN_LEADERBOARD_ID);
			}
			else if(sendFirstButton.isClicked())
			{
				requestHandler.submitToLeaderboard(MainGame.getPlayerSkinID(), MainGame.SKIN_LEADERBOARD_ID);
			}
			else if(sendSecondButton.isClicked())
			{
				requestHandler.submitToLeaderboard(MainGame.getPlayerSkinID(), MainGame.SKIN_LEADERBOARD_ID);
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
	}

}
