package com.toraysoft.widget.resizelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ResizeLayout extends RelativeLayout {

	private OnResizeListener mListener;

	public ResizeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ResizeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResizeLayout(Context context) {
		super(context);
	}

	public void setOnResizeListener(OnResizeListener l) {
		mListener = l;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mListener != null) {
			mListener.onResize(w, h, oldw, oldh);
		}
	}

}
