package com.toraysoft.widget.galleryhorizontalscrollview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

public class PageIndicatorView extends LinearLayout {

	private Context mContext;
	private int mCurrentPage = -1;
	private int mTotalPage = 0;
	private int screenWidth = 0;
	private int screenWidthDefault = 640;

	private int colorDefault = Color.parseColor("#B7B7B7");
	private int colorSelect = Color.parseColor("#48CFAD");

	public PageIndicatorView(Context context) {
		super(context);
		this.mContext = context;
	}

	public PageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public void init(int totalPage) {
		this.mTotalPage = totalPage;
		this.mCurrentPage = -1;
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setGravity(Gravity.CENTER_HORIZONTAL);
		getView();
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setCurrentPage(boolean redraw, int nPageIndex) {
		if (nPageIndex < 0 || nPageIndex >= mTotalPage)
			return;

		if (redraw) {
			this.removeAllViews();
			getView();
		}
		mCurrentPage = nPageIndex;
		resetIndicator();
	}

	private int getScreenWidth() {
		if (screenWidth == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			dm = getContext().getResources().getDisplayMetrics();
			screenWidth = dm.widthPixels;
		}
		return screenWidth;
	}

	private int getScaleLength(int length) {
		return length * getScreenWidth() / screenWidthDefault;
	}

	private void getView() {
		for (int i = 0; i < mTotalPage; i++) {
			View view = new View(mContext);
			LayoutParams lp = new LayoutParams(getScaleLength(26),
					getScaleLength(6));
			lp.leftMargin = getScaleLength(8);
			lp.rightMargin = getScaleLength(8);
			view.setLayoutParams(lp);
			if (i == mCurrentPage) {
				view.setBackgroundColor(colorSelect);
			} else {
				view.setBackgroundColor(colorDefault);
			}
			view.setTag(i);
			this.addView(view);
		}
	}

	private void resetIndicator() {
		View viewl = this.findViewWithTag(mCurrentPage - 1);
		View viewm = this.findViewWithTag(mCurrentPage);
		View viewr = this.findViewWithTag(mCurrentPage + 1);
		if (viewl != null) {
			viewl.setBackgroundColor(colorDefault);
		}
		if (viewm != null) {
			viewm.setBackgroundColor(colorSelect);
		}
		if (viewr != null) {
			viewr.setBackgroundColor(colorDefault);
		}
	}

}
