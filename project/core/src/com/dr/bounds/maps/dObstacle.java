package com.dr.bounds.maps;

import com.DR.dLib.dObject;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.dr.bounds.MainGame;
import com.dr.bounds.Player;

public class dObstacle extends dObject{
	
	// width/height
	private float objWidth = 0, objHeight = 0;
	// whether or not the obstacle is under the screen and needs to be re-generated up top
	private boolean regenerate = false;
	// whether or not the player[s] have passed the obstacle and need to be given a point
	protected boolean passed = false;
	// whether or not we counted the score for this obstacle
	private boolean incrementedScore = false;
	// player object to check for score updates
	private Player player;
	// rectangle for collision
	protected static final Rectangle useless = new Rectangle();
	
	protected final static ShapeRenderer sr = new ShapeRenderer();
	
	public dObstacle(float x, float y, Texture texture, Player p) {
		super(x, y, texture);
		objWidth = texture.getWidth();
		objHeight = texture.getHeight();
		// set origin 0,0
		setOrigin(0,0);
		player = p;
	}

	public dObstacle(float x, float y, Sprite sprite, Player p) {
		super(x, y, sprite);
		objWidth = sprite.getWidth();
		objHeight = sprite.getHeight();
		setOrigin(0,0);
		
		player = p;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		getSprite().draw(batch);
		//renderDebug(batch);
	}
	
	protected void renderDebug(SpriteBatch batch)
	{
		batch.end();
		sr.setProjectionMatrix(batch.getProjectionMatrix());
		sr.begin(ShapeType.Line);
		sr.rect(this.getBoundingRectangle().x, this.getBoundingRectangle().y, this.getBoundingRectangle().width, this.getBoundingRectangle().height);
		sr.end();
		batch.begin();
	}
	
	@Override
	public void update(float delta) {
		// check if under cameras reach
		if(getY() >= MainGame.camera.position.y + MainGame.VIRTUAL_HEIGHT/2f && regenerate == false)
		{
			regenerate = true;
		}
		// check if player passed and needs to be awarded point
		if(player.getY() < getY())
		{
			passed = true;
		}
	}
	
	/**
	 * Whether or not the player had a collision with this obstacle.
	 * Default implementation just checks whether both bounding rectangles intersect.
	 * @param player
	 * @return True if there is a collision.
	 */
	public boolean hadCollision(Player player)
	{
		if(passed == false) // only check obstacles which have not been passed yet.
		{
			return Intersector.intersectRectangles(player.getBoundingRectangle(), this.getBoundingRectangle(), useless);
		}
		return false;
	}

	public boolean shouldRegenerate()
	{
		return regenerate;
	}
	
	public void setRegenerate(boolean r)
	{
		regenerate = r;
	}
	
	public void setWidth(float w)
	{
		setScaleX(w / getSprite().getRegionWidth());
		objWidth = w;
	}
	
	public void setHeight(float h)
	{
		setScaleY(h / getSprite().getRegionHeight());
		objHeight = h;
	}
	
	public void setDimensions(float w, float h)
	{
		setWidth(w);
		setHeight(h);
	}
	
	public void setPassed(boolean passed)
	{
		if(passed == false)
		{
			incrementedScore = false;
		}
		this.passed = passed;
	}
	
	public void setIncrementedScore(boolean score)
	{
		incrementedScore = score;
	}
	
	public boolean hasPassed()
	{
		return passed;
	}
	
	public boolean hasIncrementedScore()
	{
		return incrementedScore;
	}
	
	@Override
	public float getWidth()
	{
		return objWidth;
	}
	
	@Override
	public float getHeight()
	{
		return objHeight;
	}
}
