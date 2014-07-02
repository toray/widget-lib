package com.toraysoft.widget.ScrollTextView;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class ScrollTextView extends TextView{

	public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScrollTextView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		this.setMovementMethod(ScrollingMovementMethod.getInstance());
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true); 
		return  super.dispatchTouchEvent(ev);
	}
}
