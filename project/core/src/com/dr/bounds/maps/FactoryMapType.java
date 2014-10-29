package com.dr.bounds.maps;

import com.DR.dLib.dImage;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;

public class FactoryMapType extends MapType{

	// tile-able backgrounds for current map type
	private dImage firstBG, secondBG;
	// useless please get rid of
	private Rectangle useless = new Rectangle(0,0,0,0);
	
	public FactoryMapType(int type, Player p, Texture obstacleTexture, MapGenerator generator) {
		super(type, p, generator);
		typeName = "Factory";
		// add 12 obstacles to start with
		for(int x = 0; x < 12; x++)
		{
			obstacles.add(new dObstacle(0,0, obstacleTexture, p));
			obstacles.get(x).setRegenerate(false);
		}
		
		firstBG = new dImage(0,0, new Texture("MACHINE_BG.png"));
		secondBG = new dImage(0,-MainGame.VIRTUAL_HEIGHT, new Texture("MACHINE_BG.png"));
	}

	@Override
	public void render(SpriteBatch batch) {
		firstBG.render(batch);
		secondBG.render(batch);
		super.render(batch);
	}

	@Override
	public void update(float delta) {
		for(int x = 0; x < obstacles.size(); x++)
		{
			obstacles.get(x).update(delta);
			if(obstacles.get(x).shouldRegenerate())
			{
				generateDefault(x);
				obstacles.get(x).setRegenerate(false);
			}
			// check if player had collision
			if(gen.hadCollision() == false && Intersector.intersectRectangles(player.getBoundingRectangle(), obstacles.get(x).getBoundingRectangle(), useless)) // FIX
			{
				//obstacles.get(x).setColor(Color.BLUE);
				gen.setHadCollision(true);
				// send message to opponent saying player lost
				MainGame.requestHandler.sendReliableMessage(new byte[]{'L'});
				// test might have to remove
				break;
			}
		}
		
		// update backgrounds
		if(firstBG.getY() >= MainGame.camera.position.y + MainGame.VIRTUAL_HEIGHT / 2f)
		{
			firstBG.setY(secondBG.getY() - firstBG.getHeight());
		}
		if(secondBG.getY() >= MainGame.camera.position.y + MainGame.VIRTUAL_HEIGHT / 2f)
		{
			secondBG.setY(firstBG.getY() - secondBG.getHeight());
		}
	}
	
	@Override
	public void reset()
	{
		// reset backgrounds
		firstBG.setPos(0,0);
		secondBG.setPos(0,-MainGame.VIRTUAL_HEIGHT);
	}

}
