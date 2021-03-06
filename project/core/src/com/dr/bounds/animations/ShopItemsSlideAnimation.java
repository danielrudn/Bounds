package com.dr.bounds.animations;

import java.util.ArrayList;

import com.DR.dLib.dTweener;
import com.DR.dLib.animations.AnimationStatusListener;
import com.DR.dLib.animations.dAnimation;
import com.DR.dLib.ui.dUICard;
import com.dr.bounds.MainGame;

public class ShopItemsSlideAnimation extends dAnimation {
	
	private ArrayList<dUICard> items;
	private ArrayList<Float> startY;
	private static final float DELAY = 0.1f;

	@SuppressWarnings(value = { "all" })
	public ShopItemsSlideAnimation(float duration,	AnimationStatusListener listener, int ID, ArrayList<dUICard> objectToAnimate) {
		super(duration, listener, ID, null);
		items = objectToAnimate;
		startY = new ArrayList<Float>();
	}

	@Override
	protected void animate(float time, float duration, float delta) 
	{
		if(startY.size() > 0)
		{
			for(int x= 0; x < items.size(); x++)
			{
				if(time - x * DELAY >= 0)
				{
				//	items.get(x).setY(dTweener.ElasticOut(time - x * DELAY, startY.get(x), -MainGame.VIRTUAL_HEIGHT, duration - x * DELAY,6f));
					items.get(x).setX(dTweener.ElasticOut(time - x * DELAY, startY.get(x), MainGame.VIRTUAL_WIDTH, duration - x * DELAY,5f));
				}
			}
		}
	}
	
	@Override
	public void start()
	{
		super.start();
		startY.clear();
		for(int x = 0; x < items.size(); x++)
		{
			//items.get(x).setPos(items.get(x).getX(),items.get(x).getY() + MainGame.VIRTUAL_HEIGHT);
			//startY.add(items.get(x).getY());
			items.get(x).setPos(items.get(x).getX() - MainGame.VIRTUAL_WIDTH,items.get(x).getY());
			startY.add(items.get(x).getX());
		}
	}

}
