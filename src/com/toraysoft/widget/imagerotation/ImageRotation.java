package com.toraysoft.widget.imagerotation;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class ImageRotation extends RelativeLayout {

	ImageView iv_default, iv_anim;
	int[] resources;
	boolean isAnim;
	int index = 0;
	Animation mAnimationShow;
	Animation mAnimationHide;
	Handler mHandler = new Handler();
	AnimTask mAnimTask;

	public ImageRotation(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public ImageRotation(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public ImageRotation(Context context) {
		super(context);
		init(null);
	}

	void init(AttributeSet attrs) {
		iv_default = new ImageView(getContext(),attrs);
		iv_default.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		iv_default.setScaleType(ScaleType.FIT_XY);
		iv_anim = new ImageView(getContext(),attrs);
		iv_anim.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		iv_anim.setScaleType(ScaleType.FIT_XY);
		addView(iv_default);
		addView(iv_anim);
		
		mAnimTask = new AnimTask();
		
		mAnimationShow = new AlphaAnimation(0f, 1f);
		mAnimationShow.setDuration(800);
		mAnimationHide = new AlphaAnimation(1f, 0f);
		mAnimationHide.setDuration(800);
		
		iv_anim.setVisibility(View.GONE);
	}

	public void setResources(int[] resources) {
		this.resources = resources;
		if(resources.length>0){
			iv_default.setImageResource(resources[0]);
		}
	}
	
//	public void setAnimation(Animation animation){
//		this.mAnimation = animation;
//	}
	
	public void start(){
		if(resources==null || resources.length<1 || isAnim){
			return;
		}
		isAnim = true;
		mHandler.post(mAnimTask);
	}
	
	public void stop(){
		isAnim = false;
		index = 0;
		iv_anim.setVisibility(View.GONE);
		if(resources!=null && resources.length>0){
			iv_default.setImageResource(resources[0]);
		}
	}
	
	class AnimTask implements Runnable {

		public AnimTask() {
		}

		@Override
		public void run() {
			if(!isAnim){
				return;
			}
			index++;
			if(index>=resources.length){
				index = 1;
			}
			int resId = resources[index];
			doAnim(resId);
		}
		
		public void doAnim(final int resId){
			iv_anim.setVisibility(View.VISIBLE);
			iv_anim.setImageResource(resId);
			iv_anim.startAnimation(mAnimationShow);
			long durationShow = mAnimationShow.getDuration();
			long durationHide = mAnimationHide.getDuration();
			long duration = durationShow+durationHide;
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if(isAnim){
						iv_anim.clearAnimation();
						iv_anim.startAnimation(mAnimationHide);
					}
				}
			}, durationShow);
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if(isAnim){
						iv_anim.setVisibility(View.GONE);
					}
				}
			}, duration);
			
			if(isAnim){
				mHandler.postDelayed(this, duration);
			}
		}

	};
	
}
