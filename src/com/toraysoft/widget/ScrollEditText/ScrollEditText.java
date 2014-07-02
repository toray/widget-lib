package com.toraysoft.widget.ScrollEditText;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class ScrollEditText extends EditText{
	
	public ScrollEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScrollEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollEditText(Context context) {
		super(context);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true); 
		return  super.dispatchTouchEvent(ev);
	}
}
