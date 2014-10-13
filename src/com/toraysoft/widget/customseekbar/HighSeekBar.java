package com.toraysoft.widget.customseekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class HighSeekBar extends SeekBar{


	public HighSeekBar(Context context) {
		super(context);
	}
	
	public HighSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public HighSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		System.out.println("onFinishInflate:"+getHeight());
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		System.out.println("onWindowFocusChanged:"+getHeight());
	}

	public interface OnHighChangeListener{
		void onChange(int height);
	}
}	
