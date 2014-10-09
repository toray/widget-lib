package com.toraysoft.widget.scrollview;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class AutoHorizontalScrollView extends HorizontalScrollView {
	Timer timer;
	int x;
	int width;
	
	public AutoHorizontalScrollView(Context context) {
		super(context);
		init();
	}

	public AutoHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public AutoHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	void init(){
		setHorizontalScrollBarEnabled(false);
		timer = new Timer();
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
			if(width>0){
				x++;
				if(x>width) x=0;
				scrollTo(x, 0);
			}
		}
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		width = computeHorizontalScrollRange()-getWidth();
		if(width>0 && changed){
			timer.scheduleAtFixedRate(new ScrollTask(), 0L, 15L);
		}
	}
	
}
