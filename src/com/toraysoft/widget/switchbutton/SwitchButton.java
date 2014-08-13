package com.toraysoft.widget.switchbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class SwitchButton extends RelativeLayout implements OnGestureListener {

	GestureDetector mDetector;

	ImageView bg_ImageView, tag_ImageView, mask_ImageView;
	LayoutParams mLayoutParams;

	FlingRunnable mFlinger;

	int width;
	int height;
	int sliderWidth;
	int sliderHeight;
	int maxSliderWidth;
	int minSliderWidth;
	int bg_imgId;
	int on_imgId;
	int off_imgId;
	int mask_imgId;
	int cornerWidth;

	int DEFAULT_DORNER_WIDTH = 12;

	float xLast = 0;

	public enum Slider {
		LEFT, RIGHT,
	}

	Slider mSlider = Slider.LEFT;
	OnSliderChangeListener mOnSliderChangeListener;

	public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		cornerWidth = getCornerWidth();
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		cornerWidth = getCornerWidth();
	}

	public SwitchButton(Context context) {
		super(context);
		cornerWidth = getCornerWidth();
	}

	private int getCornerWidth() {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				DEFAULT_DORNER_WIDTH, getResources().getDisplayMetrics());
	}

	@SuppressLint("NewApi")
	public void init(int width, int height, int sliderWidth, int sliderHeight,
			int bgImgId, int onImgId, int offImgId, int maskImgId) {
		this.width = width;
		this.height = height;
		this.sliderWidth = sliderWidth;
		this.sliderHeight = sliderHeight;
		this.maxSliderWidth = width - sliderWidth;
		this.minSliderWidth = width - sliderWidth / 2;
		this.bg_imgId = bgImgId;
		this.on_imgId = onImgId;
		this.off_imgId = offImgId;
		this.mask_imgId = maskImgId;
		setLayoutParams(new LayoutParams(width, height));

		bg_ImageView = new ImageView(getContext());
		bg_ImageView.setLayoutParams(new LayoutParams(width, height));
		bg_ImageView.setScaleType(ScaleType.FIT_XY);
		bg_ImageView.setImageResource(this.bg_imgId);
		bg_ImageView.setClickable(false);
		this.addView(bg_ImageView);

		tag_ImageView = new ImageView(getContext());
		tag_ImageView.setScaleType(ScaleType.FIT_XY);
		mLayoutParams = new LayoutParams(this.sliderWidth, this.sliderHeight);
		mLayoutParams.setMargins(-cornerWidth, 0, 0, 0);
		tag_ImageView.setLayoutParams(mLayoutParams);
		tag_ImageView.setImageResource(this.off_imgId);
		tag_ImageView.setClickable(false);
		this.addView(tag_ImageView);
		
		mask_ImageView = new ImageView(getContext());
		mask_ImageView.setLayoutParams(new LayoutParams(width, height));
		mask_ImageView.setScaleType(ScaleType.FIT_XY);
		mask_ImageView.setImageResource(this.mask_imgId);
		mask_ImageView.setClickable(false);
		this.addView(mask_ImageView);

		mDetector = new GestureDetector(getContext(), this);
		mDetector.setIsLongpressEnabled(false);
		mFlinger = new FlingRunnable();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xLast = (int) event.getX();
			move(xLast);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			xLast = event.getX();
			int des = (int) xLast - sliderWidth / 2;
			if (des < 0)
				des = 0;
			if (des > maxSliderWidth)
				des = maxSliderWidth;
			boolean isMoveLeft = xLast < width / 2;
			mFlinger.setMoveLeft(isMoveLeft);
			mFlinger.startUsingDistance(xLast);
			done(isMoveLeft);
			break;
		case MotionEvent.ACTION_MOVE:
			xLast = (int) event.getX();
			move(xLast);
			break;
		}
		return true;
	}

	private void move(float x) {
		int des = (int) x - sliderWidth / 2;
		if (des < -cornerWidth)
			des = -cornerWidth;
		if (des > maxSliderWidth + cornerWidth)
			des = maxSliderWidth + cornerWidth;
		mLayoutParams.setMargins(des, 0, 0, 0);
		tag_ImageView.setLayoutParams(mLayoutParams);
	}

	private void done(boolean isMoveLeft) {
		if (isMoveLeft && mSlider != Slider.LEFT) {
			mSlider = Slider.LEFT;
			tag_ImageView.setImageResource(off_imgId);
			if (mOnSliderChangeListener != null) {
				mOnSliderChangeListener.onChange(mSlider);
			}
			return;
		}
		if (!isMoveLeft && mSlider != Slider.RIGHT) {
			mSlider = Slider.RIGHT;
			tag_ImageView.setImageResource(on_imgId);
			if (mOnSliderChangeListener != null) {
				mOnSliderChangeListener.onChange(mSlider);
			}
			return;
		}
	}

	public void reset() {
		mSlider = Slider.LEFT;
		mLayoutParams.setMargins(-cornerWidth, 0, 0, 0);
	}

	public void setBtnState(Slider type) {
		if (type == Slider.LEFT) {
			mSlider = Slider.LEFT;
			mLayoutParams.setMargins(-cornerWidth, 0, 0, 0);
			tag_ImageView.setImageResource(off_imgId);
		} else if (type == Slider.RIGHT) {
			mSlider = Slider.RIGHT;
			mLayoutParams.setMargins(maxSliderWidth + cornerWidth, 0,
					-cornerWidth, 0);
			tag_ImageView.setImageResource(on_imgId);
		}
	}

	class FlingRunnable implements Runnable {
		protected int step = 10;
		protected boolean isMoveLeft = true;
		protected float distance;

		public FlingRunnable() {

		}

		public void setMoveLeft(boolean isMoveLeft) {
			this.isMoveLeft = isMoveLeft;
		}

		private void startCommon() {
			removeCallbacks(this);
		}

		@Override
		public void run() {
			if (isMoveLeft) {
				distance -= step;
				if ((distance - sliderWidth / 2) < 0) {
					mLayoutParams.setMargins(-cornerWidth, 0, 0, 0);
					tag_ImageView.setLayoutParams(mLayoutParams);
				} else {
					mLayoutParams.setMargins(
							(int) (distance - sliderWidth / 2), 0, 0, 0);
					tag_ImageView.setLayoutParams(mLayoutParams);
					post(this);
				}
			} else {
				distance += step;
				if ((distance - sliderWidth / 2) > maxSliderWidth) {
					mLayoutParams.setMargins(maxSliderWidth + cornerWidth, 0,
							-cornerWidth, 0);
					tag_ImageView.setLayoutParams(mLayoutParams);
				} else {
					mLayoutParams.setMargins(
							(int) (distance - sliderWidth / 2), 0, 0, 0);
					tag_ImageView.setLayoutParams(mLayoutParams);
					post(this);
				}
			}
		}

		public void startUsingDistance(float distance) {
			this.distance = distance;
			startCommon();
			post(this);
		}

	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	public void setOnSliderChangeListener(OnSliderChangeListener l) {
		this.mOnSliderChangeListener = l;
	}

	public interface OnSliderChangeListener {
		public void onChange(Slider slider);
	}

}
