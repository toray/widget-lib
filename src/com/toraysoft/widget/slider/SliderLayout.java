package com.toraysoft.widget.slider;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderLayout extends RelativeLayout {
//implements OnGestureListener{
//	GestureDetector mDetector;
//	
//	ImageView mImageView;
//	LayoutParams mLayoutParams;
//	TextView mTextViewLeft;
//	TextView mTextViewRight;
//	
//	FlingRunnable mFlinger;
//
//	int width;
//	int height;
//	int sliderWidth;
//	int sliderHeight;
//	int maxSliderWidth;
//	int minSliderWidth;
//	
//	float xLast = 0;
//	
//	public enum Slider{
//		LEFT,RIGHT,
//	}
//	
//	Slider mSlider = Slider.LEFT;
//	OnSliderChangeListener mOnSliderChangeListener;
	
	public SliderLayout(Context context) {
		super(context);
		init();
	}

	public SliderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SliderLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
//		this.width = Env.get().getScaleLength(DrawableSize.record_sliderlayout_width);
//		this.height = Env.get().getScaleLength(DrawableSize.record_sliderlayout_height);
//		this.sliderWidth = Env.get().getScaleLength(DrawableSize.record_sliderlayout_slider_width);
//		this.sliderHeight = Env.get().getScaleLength(DrawableSize.record_sliderlayout_slider_height);
//		this.maxSliderWidth = width - sliderWidth;
//		this.minSliderWidth = width - sliderWidth/2;
//		setLayoutParams(new LayoutParams(width,height));
//		
////		setBackgroundResource(Res.get().getDrawableId("yyssdk_record_music_control_background"));
//		ImageView background = new ImageView(getContext());
//		background.setLayoutParams(new LayoutParams(width,height));
//		background.setScaleType(ScaleType.FIT_XY);
//		background.setImageResource(Res.get().getDrawableId("yyssdk_record_music_control_background"));
//		background.setClickable(false);
//		this.addView(background);
//		
//		
//		mImageView = new ImageView(getContext());
//		mImageView.setScaleType(ScaleType.FIT_XY);
//		mLayoutParams = new LayoutParams(this.sliderWidth,
//				this.sliderHeight);
//		mImageView.setLayoutParams(mLayoutParams);
//		mImageView.setImageResource(Res.get().getDrawableId("yyssdk_record_music_control_slider"));
//		mImageView.setClickable(false);
//		this.addView(mImageView);
//
//		LinearLayout left = new LinearLayout(getContext());
//		left.setLayoutParams(new LayoutParams(width,height));
//		
//		mTextViewLeft = new TextView(getContext());
//		LayoutParams leftParmas = new LayoutParams(this.sliderWidth,
//				this.sliderHeight);
//		mTextViewLeft.setLayoutParams(leftParmas);
//		mTextViewLeft.setGravity(Gravity.CENTER);
//		mTextViewLeft.setText(Res.get().getStringId("yyssdk_ktv_record_normal"));
//		mTextViewLeft.setTextColor(Color.parseColor("#ff5eb1"));
//		left.addView(mTextViewLeft);
//		this.addView(left);
//		
//		LinearLayout right = new LinearLayout(getContext());
//		right.setLayoutParams(new LayoutParams(width,height));
//		right.setGravity(Gravity.RIGHT);
//		mTextViewRight = new TextView(getContext());
//		LayoutParams rightParmas = new LayoutParams(this.sliderWidth,
//				this.sliderHeight);
//		mTextViewRight.setLayoutParams(rightParmas);
//		mTextViewRight.setGravity(Gravity.CENTER);
//		mTextViewRight.setText(Res.get().getStringId("yyssdk_ktv_record_only_music"));
//		mTextViewRight.setTextColor(Color.WHITE);
//		right.addView(mTextViewRight);
//		this.addView(right);
//		
//		mDetector = new GestureDetector(getContext(), this);
//		mDetector.setIsLongpressEnabled(false);
//		
//		mFlinger = new FlingRunnable();
	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
////		mDetector.onTouchEvent(event);
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			xLast = (int) event.getX();
//			move(xLast);
//			break;
//		case MotionEvent.ACTION_UP:
//			xLast = event.getX();
//			int des = (int) xLast - sliderWidth / 2;
//			if (des < 0)
//				des = 0;
//			if (des > maxSliderWidth)
//				des = maxSliderWidth;
//			boolean isMoveLeft = xLast<width/2;
//			mFlinger.setMoveLeft(isMoveLeft);
//			mFlinger.startUsingDistance(xLast);
//			done(isMoveLeft);
//			break;
//		case MotionEvent.ACTION_MOVE:
//			xLast = (int) event.getX();
//			move(xLast);
//			break;
//		}
//		return true;
//	}
//	
//	private void move(float x){
//		int des = (int) x - sliderWidth / 2;
//		if (des < 0)
//			des = 0;
//		if (des > maxSliderWidth)
//			des = maxSliderWidth;
//		mLayoutParams.setMargins(des, 0, 0, 0);
//		mImageView.setLayoutParams(mLayoutParams);
//	}
//	
//	private void done(boolean isMoveLeft){
//		if(isMoveLeft && mSlider!=Slider.LEFT){
//			mTextViewLeft.setTextColor(Color.parseColor("#ff5eb1"));
//			mTextViewRight.setTextColor(Color.WHITE);
//			mSlider = Slider.LEFT;
//			if(mOnSliderChangeListener!=null){
//				mOnSliderChangeListener.onChange(mSlider);
//			}
//			return;
//		}
//		
//		if(!isMoveLeft && mSlider!=Slider.RIGHT){
//			mTextViewLeft.setTextColor(Color.WHITE);
//			mTextViewRight.setTextColor(Color.parseColor("#ff5eb1"));
//			mSlider = Slider.RIGHT;
//			if(mOnSliderChangeListener!=null){
//				mOnSliderChangeListener.onChange(mSlider);
//			}
//			return;
//		}
//	}
//	
//	public void reset(){
//		mTextViewLeft.setTextColor(Color.parseColor("#ff5eb1"));
//		mTextViewRight.setTextColor(Color.WHITE);
//		mSlider = Slider.LEFT;
//		mLayoutParams.setMargins(0, 0, 0, 0);
//	}
//	
////	Runnable move = new Runnable() {
////		enum Direction{
////			
////		}
////		
////        @Override
////        public void run() {
////        	locationX += 10;
////        	if(locationX>(width - sliderWidth)){
////        		mLayoutParams.setMargins(width - sliderWidth, 0, 0, 0);
////    			mImageView.setLayoutParams(mLayoutParams);
////                return;
////        	}
////        	if(locationX<=(width - sliderWidth)){
////        		mLayoutParams.setMargins((int)locationX, 0, 0, 0);
////    			mImageView.setLayoutParams(mLayoutParams);
////                postDelayed(move, 1);
////        	}
////        }
////    };
//	
//	
//	class FlingRunnable implements Runnable {
//		protected int step = 10;
//		protected boolean isMoveLeft = true;
//		protected float distance;
//		
//		public FlingRunnable() {
//			
//		}
//		
//		public void setMoveLeft(boolean isMoveLeft){
//			this.isMoveLeft = isMoveLeft;
//		}
//
//		private void startCommon() {
//			removeCallbacks(this);
//		}
//
//		public void run() {
//			if(isMoveLeft){
//				distance -= step;
//				if((distance-sliderWidth/2)<0){
//					mLayoutParams.setMargins(0, 0, 0, 0);
//	    			mImageView.setLayoutParams(mLayoutParams);
//				}else{
//					mLayoutParams.setMargins((int)(distance-sliderWidth/2), 0, 0, 0);
//	    			mImageView.setLayoutParams(mLayoutParams);
//	                post(this);
//				}
//			}else{
//				distance += step;
//				if(distance - sliderWidth / 2>maxSliderWidth){
//					mLayoutParams.setMargins(maxSliderWidth, 0, 0, 0);
//	    			mImageView.setLayoutParams(mLayoutParams);
//				}else{
//					mLayoutParams.setMargins((int)(distance - sliderWidth / 2), 0, 0, 0);
//	    			mImageView.setLayoutParams(mLayoutParams);
//	                post(this);
//				}
//			}
//		}
//
//		public void startUsingDistance(float distance) {
//			this.distance = distance;
//			startCommon();
//			post(this);
//		}
//		
//	}
//	
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		super.onSizeChanged(w, h, oldw, oldh);
//		// width = w;
//		// height = h;
//	}
//
//	@Override
//	public boolean onDown(MotionEvent arg0) {
//		return false;
//	}
//
//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//			float velocityY) {
//		return false;
//	}
//
//	@Override
//	public void onLongPress(MotionEvent e) {
//		
//	}
//
//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
//			float distanceY) {
//		return false;
//	}
//
//	@Override
//	public void onShowPress(MotionEvent e) {
//		
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent e) {
////		if (xLast < 0)
////			xLast = 0;
////		if (xLast > width)
////			xLast = width;
////		if(xLast<width/2){
////			mFlinger.moveBack = true;
////		}else{
////			mFlinger.moveBack = false;
////		}
////		mFlinger.startUsingDistance(xLast);
//		return false;
//	}
//	
//	
//	public void setOnSliderChangeListener(OnSliderChangeListener l){
//		this.mOnSliderChangeListener = l;
//	}
//	
//	public interface OnSliderChangeListener{
//		public void onChange(Slider slider);
//	}
}
