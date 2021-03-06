package com.dr.bounds.animations;

import java.util.ArrayList;

import com.DR.dLib.animations.AnimationStatusListener;
import com.DR.dLib.animations.dAnimation;
import com.DR.dLib.dTweener;
import com.DR.dLib.ui.dUICard;
import com.dr.bounds.MainGame;

public class SlideInArrayAnimation extends dAnimation {

	private ArrayList<dUICard> animObjects;
	private final float DISTANCE_FROM_TOP = 92f;
	
	@SuppressWarnings(value="all")
	public SlideInArrayAnimation(ArrayList<dUICard> objectsToAnimate, float duration, AnimationStatusListener listener, int ID) {
		super(duration, listener, ID, null);
		animObjects = objectsToAnimate;
	}

	@Override
	protected void animate(float time, float duration, float delta) {
		for(int x = 0; x < animObjects.size(); x++)
		{
			animObjects.get(x).setY(dTweener.ElasticOut(time, -animObjects.get(x).getHeight() - 24f,
					(DISTANCE_FROM_TOP + getPadding() + (x+1)*(animObjects.get(x).getHeight() + getPadding() + 16f)), duration, 5f));
		}
	}

	@Override
	public void start()
	{
		super.start();
		for(int x = 0; x < animObjects.size(); x++)
		{
			animObjects.get(x).setY(MainGame.camera.position.y - MainGame.VIRTUAL_HEIGHT / 2f - animObjects.get(x).getHeight() - getPadding());
		}
	}
	
	private float getPadding()
	{
		return 16f;
	}
}
