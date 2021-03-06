package com.dr.bounds;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.DR.dLib.dObject;
import com.DR.dLib.dTweener;
import com.DR.dLib.utils.dUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;
import com.dr.bounds.screens.GameScreen;

public class Player extends dObject {
	
	private final int SKIN_DIMENSIONS = 64;
	private static int skinID = MainGame.PLACEHOLDER_SKIN_ID;
	private boolean moveCenter = false;
	private boolean changeVelocity = false;
	private Vector2 targetVelocity = new Vector2(0,0);
	private Vector2 playerVelocity = new Vector2(0,0);
	private float squeezeTime = 0;
	private boolean squeezed = true;
	// whether the player hit a wall, used for combos
	private boolean hitWall = false;
	// y position when the user taps the screen
	private float startY = 0;
	// bounding rectangle used for collisions
	private Rectangle boundingRect = new Rectangle(SKIN_DIMENSIONS, SKIN_DIMENSIONS,SKIN_DIMENSIONS,SKIN_DIMENSIONS);
	// 5 recent scores
	private final List<Integer> recentScores = new ArrayList<Integer>(5);
	// unlocked skins
	private final Set<Byte> unlockedSkins = new TreeSet<Byte>();
	// best score
	private int bestScore = 0;
	// best combo
	private int bestCombo = 0;
	// amount of coins
	private int numCoins = 0;
	// whether the player has completed the tutorial
	private boolean completedTutorial = false;
	// temp
	private ParticleEffect trailEffect = new ParticleEffect();
	
	
	// test
	private float targetRotation = 0f, startRotation = 0f;
	
	public Player(float x,float y, int id)
	{
		super(x, y, new Sprite(BoundsAssetManager.SkinLoader.getTextureForSkinID(MainGame.PLACEHOLDER_SKIN_ID)));
		trailEffect.load(Gdx.files.internal("trail2.p"), Gdx.files.internal(""));
//		trailEffect.getEmitters().get(0).getTint().setColors(new float[]{Color.GREEN.r,Color.GREEN.g, Color.GREEN.b, 1f});
		setSkinID(id);
		loadPlayerData();
	}

	@Override
	public void render(SpriteBatch batch) {
		trailEffect.draw(batch);
		getSprite().draw(batch);
	}

	@Override
	public void update(float delta) {
		// add velocity
		setPosition(getX() + playerVelocity.x * delta, getY() + playerVelocity.y * delta);
		boundingRect.set(getX() + 8f, getY() + 8f, getWidth()-16f, getHeight()-16f);
		trailEffect.update(delta);
		trailEffect.setPosition(getX() + getWidth()/2f, getY() + getHeight() / 2f);
		//if(Gdx.input.isTouched()|| Gdx.input.isKeyJustPressed(Keys.ANY_KEY))
		if((Gdx.input.isTouched() && Gdx.input.justTouched()))
		{
			if(touchedLeftSide())// user touched left half of screen
			{
				targetVelocity.set(-32f*32f,0);
				if(moveCenter)
				{
					playerVelocity.set(-18*18f,0);
					moveCenter = false;
				}
			//	this.getSprite().setRotation(-45f);
				targetRotation = -45f;
			}
			else // user touched right half of screen
			{
				targetVelocity.set(32f*32f,0);
				if(moveCenter)
				{
					playerVelocity.set(18*18f,0);
					moveCenter = false;
				}
			//	this.getSprite().setRotation(45f);
				targetRotation = 45f;
			}
			changeVelocity = true;
			setScale(16f / getWidth(),76f / getHeight());
			squeezeTime = 0;
			squeezed = true;
			startRotation = this.getSprite().getRotation();
		}
		
		if(changeVelocity)
		{
			changeVelocity(delta);
		}
		if(moveCenter)
		{
			moveCenter(delta);
		}
		
		if(squeezed)
		{
			if(squeezeTime <= 1.5f)
			{
				squeezeTime+=delta;
				this.getSprite().setRotation(dTweener.ElasticOut(squeezeTime, startRotation, targetRotation - startRotation, 1.5f));
				setScale(dTweener.ElasticOut(squeezeTime, 24f, 40f, 1.5f) / getWidth(), dTweener.ExponentialEaseOut(squeezeTime, 76f, -12f, 1.5f) / getHeight());
			//	System.out.println("TARGET: " + targetRotation + " START: " + startRotation + " current: " + getSprite().getRotation() + " origin: " + this.getOriginX() + "," + this.getOriginY());
			}
			else
			{
				squeezed = false;
			}
		}
		this.getSprite().setOriginCenter();
	}
	private void changeVelocity(float delta)
	{
		playerVelocity.set(dTweener.MoveToAndSlow(playerVelocity.x, targetVelocity.x, delta*11f), dTweener.MoveToAndSlow(playerVelocity.y, targetVelocity.y, delta));
		setY(getY() - GameScreen.CAMERA_SPEED * delta * 1.1f);
		// check if passed bounds and need to move back to center
		checkBounds();
	}
	
	private void checkBounds()
	{
		if(getX() <= -5|| getX() >= MainGame.VIRTUAL_WIDTH - getWidth() + 5)// changes with +- 5 to account for ball squeezing
		{
			moveCenter = true;
			changeVelocity = false;
			setScale(16f / getWidth(),76f / getHeight());
			playerVelocity.set(0,0);
			targetVelocity.set(0,0);
			squeezeTime = 0;
			squeezed = true;
			startY = getY();
			hitWall = true;
			startRotation = this.getSprite().getRotation();
			targetRotation = 0f;
		}
		else
		{
			hitWall = false;
		}
	}
	
	private void moveCenter(float delta)
	{
		if(getX() < MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f - 14f || getX() > MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f + 14f)
		{
			//setPosition(dTweener.MoveToAndSlow(getX(), MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f, 4f*delta),getY());
			setX(dTweener.MoveToAndSlow(getX(), MainGame.VIRTUAL_WIDTH/2f - getWidth()/2f,5.5f*delta));
			setY(dTweener.MoveToAndSlow(getY(), startY - 475f, 5.5f*delta));
		}
		else
		{
			moveCenter = false;
		}
	}
	
	private boolean touchedLeftSide()
	{
		if(dUtils.getVirtualMouseX() <= MainGame.VIRTUAL_WIDTH/2f || Gdx.input.isKeyJustPressed(Keys.LEFT))
		{
			return true;
		}
		else if(Gdx.input.isKeyJustPressed(Keys.RIGHT))
		{
			return false;
		}
		return false;
	}
	
	private void loadPlayerData()
	{
		XmlReader reader = new XmlReader();
		try {
			Element pData = reader.parse(Gdx.files.local("pData.xml"));
			// load coins
			numCoins = Integer.parseInt(pData.getChildByName("Coins").getAttribute("amount"));
			// load complete tutorial
			completedTutorial = Boolean.parseBoolean(pData.getChildByName("Tutorial").getAttribute("complete"));
			// load scores
			Element scores = pData.getChildByName("Scores").getChildByName("RecentScores");
			bestScore = Integer.parseInt(pData.getChildByName("Scores").getAttribute("best"));
			bestCombo = Integer.parseInt(pData.getChildByName("Scores").getAttribute("combo"));
			String[] scoreArray = scores.get("OldestToLatest").replaceAll("[ \t\n\f\r]", "").split(",");
			for(int x = 0; x < scoreArray.length; x++)
			{
				recentScores.add(Integer.parseInt(scoreArray[x]));
			}
			// load unlocked skins
			Element skins = pData.getChildByName("Skins");
			setSkinID(Integer.parseInt(skins.getAttribute("current")));
			String[] skinArray = skins.get("SkinID").replaceAll("[ \t\n\f\r]", "").split(","); 
			for(int x = 0; x < skinArray.length; x++)
			{
				unlockedSkins.add(Byte.parseByte(skinArray[x]));
			}
		}catch (IOException e)	{
			// what happened here ??
			setDefaultValues();
		}
		catch(SerializationException se)
		{
			// file not found
			setDefaultValues();
		}
		catch(GdxRuntimeException gdx)
		{
			// error parsing file
			setDefaultValues();
		}
		catch (Exception e)
		{
			// missing something from pData
			setDefaultValues();
		}
	}
	
	public void savePlayerData()
	{
		StringWriter stringWriter = new StringWriter();
		// write
		XmlWriter writer = new XmlWriter(stringWriter);
		try {
			writer.element("pData")
				.element("Coins").attribute("amount", numCoins).pop()
				.element("Tutorial").attribute("complete", completedTutorial).pop()
				.element("Scores").attribute("best", bestScore).attribute("combo", bestCombo)
					.element("RecentScores").attribute("OldestToLatest", recentScores.toString().replaceAll("\\[", "").replaceAll("\\]", "")).pop()
				.pop()
				.element("Skins").attribute("current",skinID).attribute("SkinID", unlockedSkins.toString().replaceAll("\\[", "").replaceAll("\\]", "")).pop()
			.pop();
		// save
		FileHandle pData = Gdx.files.local("pData.xml");
		pData.writeString(stringWriter.toString(), false);
		writer.close();
		}catch (IOException e)	{
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs when the game fails to load player data for some reason
	 */
	private void setDefaultValues()
	{
		for(int x = 0; x < 5; x++)
		{
			recentScores.add(0);
		}
		// add default skin
		unlockedSkins.add((byte) 1);
		setSkinID(unlockedSkins.iterator().next());
		bestScore = 0;
		bestCombo = 0;
		numCoins = 0;
		completedTutorial = false;
	}
	
	public void addRecentScore(int score)
	{
		// shift current scores down
		for(int x = 1; x < recentScores.size(); x++)
		{
			recentScores.set(x-1, recentScores.get(x));
		}
		recentScores.set(recentScores.size()-1, score);
	}
	
	public boolean isSkinUnlocked(Byte id)
	{
		return unlockedSkins.contains((Byte)id);
	}
	
	// reset state when player dies in a level
	public void reset()
	{
	//	setOrigin(0,0);
		setPos(MainGame.VIRTUAL_WIDTH / 2f - getWidth()/2f, MainGame.VIRTUAL_HEIGHT / 2f - getHeight() / 2f);
		moveCenter = false;
		changeVelocity = false;
		startY = getY();
		setAlpha(1f);
		getSprite().setSize(SKIN_DIMENSIONS,SKIN_DIMENSIONS);
	}
	
	
	public void setSkinID(int id)
	{
		skinID = id;
		getSprite().setRegion(BoundsAssetManager.SkinLoader.getTextureForSkinID(skinID));
	}
	
	public void setBestCombo(int combo)
	{
		if(combo > bestCombo)
		{
			bestCombo = combo;
			MainGame.requestHandler.submitToLeaderboard(bestCombo, MainGame.COMBO_LEADERBOARD_ID);
		}
	}
	
	public void setBestScore(int score)
	{
		if(score > bestScore)
		{
			bestScore = score;
			MainGame.requestHandler.submitToLeaderboard(bestScore, MainGame.SCORE_LEADERBOARD_ID);
		}
	}
	
	public int getBestCombo()
	{
		return bestCombo;
	}
	
	public int getBestScore()
	{
		return bestScore;
	}
	
	public List<Integer> getRecentScores()
	{
		return recentScores;
	}
	
	public Set<Byte> getUnlockedSkins()
	{
		return unlockedSkins;
	}
	
	public int getSkinID()
	{
		return skinID;
	}
	
	public void setCoins(int coins)
	{
		numCoins = coins;
	}
	
	public int getCoins()
	{
		return numCoins;
	}
	
	public boolean isMovingCenter()
	{
		return moveCenter;
	}
	
	public boolean hasHitWall()
	{
		return hitWall;
	}
	
	@Override
	public Rectangle getBoundingRectangle()
	{
		return boundingRect;
	}

}
