package com.toraysoft.widget.scrollscreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScrollScreenLayout extends HorizontalScrollView {
	Timer timer;
	int x;
	int width;
	int height;
	LinearLayout mLinearLayout;
	LinearLayout mLeftLinearLayout;
	LinearLayout mRightLinearLayout;
	LinearLayout mCurrentLinearLayout;

	TextView tv_left1;
	TextView tv_left2;
	TextView tv_left3;
	TextView tv_left4;
	TextView tv_left5;
	TextView tv_right1;
	TextView tv_right2;
	TextView tv_right3;
	TextView tv_right4;
	TextView tv_right5;
	Handler mHandler = new Handler(Looper.getMainLooper());

	List<String> tags;
	List<Drawable> tagLeftBgs;
	List<Drawable> tagRightBgs;
	
	int offset;

	public ScrollScreenLayout(Context context) {
		super(context);
		init();
	}

	public ScrollScreenLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ScrollScreenLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		setHorizontalScrollBarEnabled(false);
		timer = new Timer();
		tags = new ArrayList<String>();
		tagLeftBgs = new ArrayList<Drawable>();
		tagRightBgs = new ArrayList<Drawable>();
		
		mLinearLayout = new LinearLayout(getContext());
		mLinearLayout.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLeftLinearLayout = new LinearLayout(getContext());
		mLeftLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		mLeftLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
		mLeftLinearLayout.setOrientation(LinearLayout.VERTICAL);
		mRightLinearLayout = new LinearLayout(getContext());
		mRightLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		mRightLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
		mRightLinearLayout.setOrientation(LinearLayout.VERTICAL);
		mCurrentLinearLayout = mLeftLinearLayout;
		addView(mLinearLayout);
		mLinearLayout.addView(mLeftLinearLayout);
		mLinearLayout.addView(mRightLinearLayout);

		tv_left1 = new TextView(getContext());
		tv_left2 = new TextView(getContext());
		tv_left3 = new TextView(getContext());
		tv_left4 = new TextView(getContext());
		tv_left5 = new TextView(getContext());
		tv_right1 = new TextView(getContext());
		tv_right2 = new TextView(getContext());
		tv_right3 = new TextView(getContext());
		tv_right4 = new TextView(getContext());
		tv_right5 = new TextView(getContext());

		tv_left1.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		tv_left2.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		tv_left3.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		tv_left4.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		tv_left5.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		tv_right1.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		tv_right2.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		tv_right3.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		tv_right4.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		tv_right5.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		
		tv_left1.setTextColor(Color.WHITE);
		tv_left2.setTextColor(Color.WHITE);
		tv_left3.setTextColor(Color.WHITE);
		tv_left4.setTextColor(Color.WHITE);
		tv_left5.setTextColor(Color.WHITE);
		tv_right1.setTextColor(Color.WHITE);
		tv_right2.setTextColor(Color.WHITE);
		tv_right3.setTextColor(Color.WHITE);
		tv_right4.setTextColor(Color.WHITE);
		tv_right5.setTextColor(Color.WHITE);


		mLeftLinearLayout.addView(tv_left1);
		mLeftLinearLayout.addView(tv_left2);
		mLeftLinearLayout.addView(tv_left3);
		mLeftLinearLayout.addView(tv_left4);
		mLeftLinearLayout.addView(tv_left5);
		mRightLinearLayout.addView(tv_right1);
		mRightLinearLayout.addView(tv_right2);
		mRightLinearLayout.addView(tv_right3);
		mRightLinearLayout.addView(tv_right4);
		mRightLinearLayout.addView(tv_right5);
		
		setVisibility(View.GONE);
	}

	@Override
	public boolean arrowScroll(int direction) {
		return super.arrowScroll(direction);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}

	class ScrollTask extends TimerTask {

		@Override
		public void run() {
			if (width > 0) {
				if(offset>0){
					mHandler.post(new Runnable() {
						public void run() {
							if(getVisibility()!=View.VISIBLE)
								setVisibility(View.VISIBLE);
							int left = getLeft();
							int top = getTop();
							int right = getRight();
							int bottom = getBottom();
							layout(offset, top, right - left +offset, bottom);
						}
					});
					offset--;
				}else{
					x++;
					if (x > width) {
						x = 0;
						timer.cancel();
						mHandler.post(new Runnable() {
							public void run() {
								mLinearLayout.removeAllViews();
								if (mCurrentLinearLayout.equals(mLeftLinearLayout)) {
									mLinearLayout.addView(mRightLinearLayout);
									mLinearLayout.addView(mLeftLinearLayout);
									mCurrentLinearLayout = mRightLinearLayout;
									initTagLeft();
								} else {
									mLinearLayout.addView(mLeftLinearLayout);
									mLinearLayout.addView(mRightLinearLayout);
									mCurrentLinearLayout = mLeftLinearLayout;
									initTagRight();
								}
								scrollTo(0, 0);
								timer = new Timer();
								timer.scheduleAtFixedRate(new ScrollTask(), 0L, 7L);
							}
						});
						return;
					}
					scrollTo(x, 0);
				}
			}

		}

	}
	
	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
//		if(offset>0){
//			setMarginOffset(offset);
//		}
		// int width = computeHorizontalScrollRange() - getWidth();
		// if (this.width > 0 && changed) {
		// timer.scheduleAtFixedRate(new ScrollTask(), 0L, 5L);
		// }
	}

	public void setScrollLayoutParams(int width, int height) {
		this.width = width;
		this.height = height;
		// setLayoutParams(new LayoutParams(width, height));
		// mLinearLayout.setLayoutParams(new LayoutParams(width,
		// height));
		mLeftLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(width,
				height));
		mRightLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(width,
				height));
		setMarginOffset(width);
	}
	
	void setMarginOffset(int offset){
		int left = getLeft();
		int top = getTop();
		int right = getRight();
		int bottom = getBottom();
		this.offset = offset;
		layout(offset, top, width*2+ offset, top+height);
	}

	public void setTags(List<String> tags,List<Drawable> tagLeftBgs,List<Drawable> tagRgihtBgs){
		this.tags = tags;
		this.tagLeftBgs = tagLeftBgs;
		this.tagRightBgs = tagRgihtBgs;
		x = 0;
		initTagLeft();
		initTagRight();
		if(timer!=null){
			timer.cancel();
		}
		timer = new Timer();
		timer.scheduleAtFixedRate(new ScrollTask(), 0L, 7L);
	}

	public void resetTextView() {
		tv_left1.setText("");
		tv_left1.setBackgroundColor(Color.TRANSPARENT);
		tv_left2.setText("");
		tv_left2.setBackgroundColor(Color.TRANSPARENT);
		tv_left3.setText("");
		tv_left3.setBackgroundColor(Color.TRANSPARENT);
		tv_left4.setText("");
		tv_left4.setBackgroundColor(Color.TRANSPARENT);
		tv_left5.setText("");
		tv_left5.setBackgroundColor(Color.TRANSPARENT);
		tv_right1.setText("");
		tv_right1.setBackgroundColor(Color.TRANSPARENT);
		tv_right2.setText("");
		tv_right2.setBackgroundColor(Color.TRANSPARENT);
		tv_right3.setText("");
		tv_right3.setBackgroundColor(Color.TRANSPARENT);
		tv_right4.setText("");
		tv_right4.setBackgroundColor(Color.TRANSPARENT);
		tv_right5.setText("");
		tv_right5.setBackgroundColor(Color.TRANSPARENT);
	}

	public TextView getTextView(int position) {
		switch (position) {
		case 0:
			return tv_left1;
		case 1:
			return tv_left2;
		case 2:
			return tv_left3;
		case 3:
			return tv_left4;
		case 4:
			return tv_left5;
		case 5:
			return tv_right1;
		case 6:
			return tv_right2;
		case 7:
			return tv_right3;
		case 8:
			return tv_right4;
		case 9:
			return tv_right5;
		}
		return null;
	}
	
	public void initTagLeft(){
		int[] tagIndexs = getSequence(5);
		int[] tagBgIndexs = getSequence(6);
		int[] leftMargins = getSequence(50);
		if(tags!=null){
			for (int i = 0; i < 5; i++) {
				int index = 0;
				int bgIndex = 0;
				index = tagIndexs[i];
				bgIndex = tagBgIndexs[i];
				TextView tv = getTextView(i);
				if(index<tags.size()){
					tv.setVisibility(View.VISIBLE);
					tv.setText(tags.get(index));
				}else{
					tv.setVisibility(View.INVISIBLE);
					tv.setText("");
				}
				tv.setBackgroundDrawable(tagLeftBgs.get(bgIndex));
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv.getLayoutParams();
				float size = tv.getPaint().measureText(tv.getText().toString())+tv.getPaddingLeft()+tv.getPaddingRight();
				lp.setMargins((int)Math.floor(Math.random() * (width-size)), 5, 0, 5);
			}
		}
	}
	
	public void initTagRight(){
		int[] tagIndexs = getSequence(5);
		int[] tagBgIndexs = getSequence(6);
		int[] leftMargins = getSequence(50);
		if(tags!=null){
			for (int i = 5; i < 10; i++) {
				int index = 0;
				int bgIndex = 0;
				int j = i-5;
				index = tagIndexs[j]+5;
				bgIndex = tagBgIndexs[j];
				TextView tv = getTextView(i);
				if(index<tags.size()){
					tv.setVisibility(View.VISIBLE);
					tv.setText(tags.get(index));
				}else{
					tv.setVisibility(View.INVISIBLE);
					tv.setText("");
				}
				tv.setBackgroundDrawable(tagRightBgs.get(bgIndex));
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv.getLayoutParams();
				float size = tv.getPaint().measureText(tv.getText().toString())+tv.getPaddingLeft()+tv.getPaddingRight();;
				lp.setMargins((int)Math.floor(Math.random() * (width-size)), 5, 0, 5);
			}
		}
	}
	
//	public void initTag(){
//		int[] tagFirstIndexs = getSequence(5);
//		int[] tagSecondIndexs = getSequence(5);
//		int[] tagFirstBgIndexs = getSequence(6);
//		int[] tagSecondBgIndexs = getSequence(6);
//		if(tags!=null){
//			for (int i = 0; i < 10; i++) {
//				int index = 0;
//				int bgIndex = 0;
//				if(i<5){
//					index = tagFirstIndexs[i];
//					bgIndex = tagFirstBgIndexs[i];
//				}else{
//					index = tagSecondIndexs[i-5]+5;
//					bgIndex = tagSecondBgIndexs[i-5];
//				}
//				TextView tv = getTextView(i);
//				if(index<tags.size()){
//					tv.setVisibility(View.VISIBLE);
//					tv.setText(tags.get(index));
//				}else{
//					tv.setVisibility(View.INVISIBLE);
//					tv.setText("");
//				}
//				tv.setBackgroundDrawable(tagBgs.get(bgIndex));
//				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv.getLayoutParams();
//				lp.setMargins(i*5, 5, 0, 5);
//			}
//		}
//	}
	
	public int[] getSequence(int no) {
		int[] sequence = new int[no];
		for (int i = 0; i < no; i++) {
			sequence[i] = i;
		}
		Random random = new Random();
		for (int i = 0; i < no; i++) {
			int p = random.nextInt(no);
			int tmp = sequence[i];
			sequence[i] = sequence[p];
			sequence[p] = tmp;
		}
		random = null;
		return sequence;
	}
}
