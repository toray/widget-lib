package com.toraysoft.widget.clickeffectlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class ClickEffectLayout extends LinearLayout {

	public ClickEffectLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ClickEffectLayout(Context context) {
		super(context);
		init();
	}

	void init() {
		setOrientation(LinearLayout.VERTICAL);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		requestDisallowInterceptTouchEvent(true);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN
					|| ev.getAction() == MotionEvent.ACTION_MOVE) {
				setAlpha(0.5f);
			} else {
				setAlpha(1);
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (event.getAction() == MotionEvent.ACTION_DOWN
					|| event.getAction() == MotionEvent.ACTION_MOVE) {
				setAlpha(0.5f);
			} else {
				setAlpha(1);
			}
		}
		return super.onTouchEvent(event);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN
					|| ev.getAction() == MotionEvent.ACTION_MOVE) {
				setAlpha(0.5f);
			} else {
				setAlpha(1);
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

}
