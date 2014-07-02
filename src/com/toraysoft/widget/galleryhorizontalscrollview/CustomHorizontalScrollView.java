package com.toraysoft.widget.galleryhorizontalscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class CustomHorizontalScrollView extends HorizontalScrollView implements
		OnGestureListener {

	private int itemWidth;
	private int count;
	private OnCustomScrollListner onCustomScrollListner;

	GestureDetector mGestureDetector;
	boolean onFling = false;
	CheckScrollTask mCheckScrollTask;
	int lastL;
	int oldL;
	int currentPos;

	public CustomHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomHorizontalScrollView(Context context) {
		super(context);
		init();
	}

	void init() {
		mGestureDetector = new GestureDetector(getContext(), this);
		mCheckScrollTask = new CheckScrollTask();
	}

	public void setOnCustomScrollListner(OnCustomScrollListner l) {
		this.onCustomScrollListner = l;
	}

	public interface OnCustomScrollListner {

		void onCustomScrollPos(int pos);

	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		// System.out.println("onScrollChanged:" + l);
		lastL = l;
		int position = 0;
		int length = l;
		if (length <= itemWidth / 2) {
			position = 1;
		} else if (length >= ((count - 1) * itemWidth - itemWidth / 2)) {
			position = count - 2;
		} else {
			position = (length - itemWidth / 2) / itemWidth + 2;
		}

		if (onCustomScrollListner != null) {
			onCustomScrollListner.onCustomScrollPos(position);
			currentPos = position;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		onFling = false;
		mGestureDetector.onTouchEvent(ev);
		if (ev.getAction() == MotionEvent.ACTION_UP && !onFling) {
			System.out.println("ACTION_UP");
			System.out.println("after on touch :position:" + currentPos);
			postDelayed(new ScrollToCenter(), 50);
		}
		return super.onTouchEvent(ev);
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
		// System.out.println("onScroll:" + distanceX);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// Log.v("velocityX", "" + velocityX);
		onFling = true;
		oldL = -1;
		post(mCheckScrollTask);
		return onFling;
	}

	class CheckScrollTask implements Runnable {
		@Override
		public void run() {
			removeCallbacks(this);
			// System.out.println("oldL:" + oldL);
			// System.out.println("lastL:" + lastL);
			if (oldL == lastL) {
				System.out.println("scroll stop!!!!!!!!!!!!!");
				removeCallbacks(this);
				System.out
						.println("after check scroll :position:" + currentPos);
				postDelayed(new ScrollToCenter(), 50);
			} else {
				oldL = lastL;
				postDelayed(this, 10);
			}
		}
	}

	class ScrollToCenter implements Runnable {

		@Override
		public void run() {
			smoothScrollTo((currentPos - 1) * itemWidth, 0);
			removeCallbacks(this);
		}

	}
}
