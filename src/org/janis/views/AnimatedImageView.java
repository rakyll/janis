/*
 * @author Burcu Dogan
 */

package org.janis.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public abstract class AnimatedImageView extends ImageView {
	
	final private Object animationLock = new Object();

	/**
	 * Instantiates a new AnimatedImageView.
	 *
	 * @param context the context
	 */
	public AnimatedImageView(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new AnimatedImageView.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public AnimatedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Instantiates a new AnimatedImageView with style.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public AnimatedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/**
	 * Sets a new image drawable, input drawable should 
	 * be a animation list.
	 *
	 * @param animationDrawable
	 */
	@Override
	public void setImageDrawable(Drawable animationDrawable) {
		super.setImageDrawable(animationDrawable);
		startAnimation();
	}
	
	/**
	 * Sets a new image res, input should 
	 * be a animation list.
	 *
	 * @param resId resource identifier to 
	 * the animation drawable
	 */
	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		startAnimation();
	}
	
	/**
	 * Start animation, by default it runs
	 * infinitely.
	 */
	public void startAnimation(){
		synchronized (animationLock) {
			AnimationDrawable animationImage = 
				(AnimationDrawable) getDrawable();
			// Starts the animation
			animationImage.start();
		}
		
	}
	
	/**
	 * Stops the image animation.
	 */
	public void stopAnimation(){
		synchronized (animationLock) {
			AnimationDrawable animationImage = 
				(AnimationDrawable) getDrawable();
			// Starts the animation
			animationImage.stop();
		}
	}

}
