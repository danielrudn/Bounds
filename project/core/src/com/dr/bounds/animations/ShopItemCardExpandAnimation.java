package com.dr.bounds.animations;

import com.DR.dLib.dTweener;
import com.DR.dLib.animations.AnimationStatusListener;
import com.DR.dLib.animations.dAnimation;
import com.dr.bounds.MainGame;
import com.dr.bounds.ui.ShopItemCard;

public class ShopItemCardExpandAnimation extends dAnimation {

	private float startX, startY;
	
	public ShopItemCardExpandAnimation(float duration, AnimationStatusListener listener, int ID, ShopItemCard objectToAnimate) {
		super(duration, listener, ID, objectToAnimate);
	}

	@Override
	protected void animate(float time, float duration, float delta) {
		getAnimatedObjects()[0].setPos(dTweener.ExponentialEaseOut(time, startX, -startX + MainGame.VIRTUAL_WIDTH / 2f - getAnimatedObjects()[0].getWidth() / 2f, duration),
			dTweener.ExponentialEaseOut(time, startY, -startY + MainGame.VIRTUAL_HEIGHT / 2f - (ShopItemCard.CARD_HEIGHT + 128f) / 2f, duration));
	}

	@Override
	public void start()
	{
		super.start();
		startX = this.getAnimatedObjects()[0].getX();
		startY = this.getAnimatedObjects()[0].getY();
	}

}
