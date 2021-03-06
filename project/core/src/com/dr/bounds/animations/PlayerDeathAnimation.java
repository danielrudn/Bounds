package com.dr.bounds.animations;

import com.DR.dLib.dTweener;
import com.DR.dLib.animations.dAnimation;
import com.dr.bounds.Player;

public class PlayerDeathAnimation extends dAnimation {

	public PlayerDeathAnimation(float duration, Player p) {
		super(duration, null, 1, p);
	}

	@Override
	protected void animate(float time, float duration, float delta)
	{
		this.getAnimatedObjects()[0].setAlpha(dTweener.ExponentialEaseOut(time, 1, -.95f, duration));
		((Player)this.getAnimatedObjects()[0]).getSprite().setSize(dTweener.ExponentialEaseOut(time, 64f, -64f, duration), dTweener.ExponentialEaseOut(time, 64f, -64f, duration));
	}
	
	@Override
	public void start()
	{
		super.start();
		getAnimatedObjects()[0].setOriginCenter() ;
	}

}
