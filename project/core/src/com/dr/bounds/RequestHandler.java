package com.dr.bounds;

public interface RequestHandler {

	public void requestSignIn();
	
	public void requestSignOut();
	
//	public void requestInviteActivity();
	
//	public void requestInboxActivity();
	
//	public void leaveRoom();
	
	public String getCurrentAccountName();
	
//	public String getOpponentName();
	
//	public boolean isMultiplayer();
	
//	public boolean isHost();
	
//	public int getOpponentStatus();
	
	public boolean isConnected();
	
	public boolean isConnecting();
	
//	public String getInviterName();
	
	public void submitToLeaderboard(int data, String leaderboardID);
	
	public void showLeaderboard(String leaderboardID);
	
	public void showShareIntent(String text);
	
//	public void sendReliableMessage(byte[] message);
	
//  public void sendUnreliableMessage(byte[] message);
	
//	public void loadRecentlyPlayedWithPlayers();
	
//	public void loadInvitablePlayers();
	
//	public void loadInvitations();
	
	// return the name of a recent player, given an index, relies on loadRecentlyPlayedWithPlayers
//	public String getRecentPlayerName(int index);
	
//	public String getRecentPlayerID(int index);
	
//	public String getInvitablePlayerName(int index);
	
//	public String getInvitablePlayerID(int index);
	
//	public String getInvitationID(int index);
	
//	public String getInviterName(int index);
	
//	public void sendInvite(String idToInvite);
	
//	public void acceptInvite(String idToAccept);
}
