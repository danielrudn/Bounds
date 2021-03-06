package com.dr.bounds.animations;

import java.util.ArrayList;

import com.DR.dLib.animations.AnimationStatusListener;
import com.DR.dLib.animations.dAnimation;
import com.DR.dLib.dTweener;
import com.DR.dLib.ui.dUICard;

public class SlideOutArrayAnimation extends dAnimation {

	private ArrayList<dUICard> objectsToAnimate;
	private ArrayList<Float> yPosAtStart;
	
	@SuppressWarnings(value = { "all" })
	public SlideOutArrayAnimation(ArrayList<dUICard> objectsToAnimate, float duration, AnimationStatusListener listener, int ID) {
		super(duration, listener, ID, null);
		this.objectsToAnimate = objectsToAnimate;
		yPosAtStart = new ArrayList<Float>();
	}

	@Override
	protected void animate(float time, float duration, float delta) {
		for(int x = 0; x < yPosAtStart.size(); x++)
		{
			objectsToAnimate.get(x).setY(dTweener.ElasticOut(time, yPosAtStart.get(x), -yPosAtStart.get(x) - objectsToAnimate.get(x).getHeight() * 1.5f, duration,6f));
		}
	}

	@Override
	public void start()
	{
		super.start();
		yPosAtStart.clear();
		for(int x = 0; x < objectsToAnimate.size(); x++)
		{
			yPosAtStart.add(new Float(objectsToAnimate.get(x).getY()));
		}
	}
}

