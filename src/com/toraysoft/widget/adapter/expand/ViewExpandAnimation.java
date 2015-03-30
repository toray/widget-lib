package com.toraysoft.widget.adapter.expand;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

public class ViewExpandAnimation extends Animation {

	private View mAnimationView = null;
	private LayoutParams mViewLayoutParams = null;
	private int mStart = 0;
	private int mEnd = 0;
	OnExpandListener mOnExpandListener;
	Handler mHandler = new Handler();

	public ViewExpandAnimation(View view,OnExpandListener l) {
		this.mOnExpandListener = l;
		animationSettings(view, 300);
	}

	public ViewExpandAnimation(View view, int duration,OnExpandListener l) {
		this.mOnExpandListener = l;
		animationSettings(view, duration);
	}

	private void animationSettings(final View view, int duration) {
		setDuration(duration);
		view.setVisibility(View.VISIBLE);
		mAnimationView = view;
		mViewLayoutParams = (LayoutParams) view.getLayoutParams();
		mStart = mViewLayoutParams.bottomMargin;
		mEnd = (mStart == 0 ? (0 - view.getHeight()) : 0);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		if (interpolatedTime < 1.0f) {
			mViewLayoutParams.bottomMargin = mStart
					+ (int) ((mEnd - mStart) * interpolatedTime);
			// invalidate
			mAnimationView.requestLayout();
		} else {
			if(mOnExpandListener!=null){
				mOnExpandListener.onExpandFinish();
			}
			mViewLayoutParams.bottomMargin = mEnd;
			mAnimationView.requestLayout();
			if (mEnd != 0) {
				mAnimationView.setVisibility(View.GONE);
			}
		}
	}
}