package com.toraysoft.widget.enable;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class EnableViewPager extends ViewPager {

	public EnableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EnableViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		return false;
	}

}
