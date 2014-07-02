package com.toraysoft.widget.verticalscrolllayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class VerticalScrollLayout extends FrameLayout {

	private Scroller scroller;
	private int DURATION_TIME = 800;
	private int DURATION_TIME_SECOND = 500;
	private boolean POSITION_UP = false;
	private boolean isScrolled = false;
	private int height;
	private int offset;
	private OnScrolledListener mOnScrolledListener;

	public VerticalScrollLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VerticalScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VerticalScrollLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		scroller = new Scroller(getContext());
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (!scroller.isFinished()) {
			if (scroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = scroller.getCurrX();
				int y = scroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				// Keep on drawing until the animation has finished.
				invalidate();
			} else {
				clearChildrenCache();
			}
		} else {
			clearChildrenCache();
		}
	}

	public synchronized void scrolled(int height, int offset) {
		if (isScrolled)
			return;
		isScrolled = true;
		this.height = height;
		this.offset = offset;
		getChildAt(0).setLayoutParams(
				new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height
						+ offset * 2));
		smoothScrollTo(offset);
		postDelayed(new Runnable() {
			public void run() {
				if (mOnScrolledListener != null)
					mOnScrolledListener.onScrolled();
			}
		}, DURATION_TIME_SECOND);
	}

	public synchronized void revertScrolled() {
		if (!isScrolled)
			return;
		isScrolled = false;
		smoothScrollTo(-offset);
		FrameLayout.LayoutParams lpf = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, height + offset * 2);
		getChildAt(0).setLayoutParams(lpf);
		postDelayed(new Runnable() {
			public void run() {
				FrameLayout.LayoutParams lpf = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				getChildAt(0).setLayoutParams(lpf);
			}
		}, DURATION_TIME);
	}

	public void smoothScrollTo(int dy) {
		int oldScrollY = getScrollY();
		scroller.startScroll(getScrollX(), oldScrollY, getScrollX(), dy,
				DURATION_TIME);
		// postInvalidate();
	}

	private void enableChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);
		}
	}

	private void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}

	public boolean getIsPositionUp() {
		return POSITION_UP;
	}

	public void setIsPositionUp(boolean state) {
		POSITION_UP = state;
	}

	public void setOnScrolledListener(OnScrolledListener l) {
		this.mOnScrolledListener = l;
	}

	public interface OnScrolledListener {
		void onScrolled();
	}

}
