package com.toraysoft.widget.imageviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.toraysoft.widget.imageviewpager.AutoScrollViewPager.OnDataSetChangedListener;

public class PageIndicatorLayout extends LinearLayout implements
		OnPageChangeListener, OnDataSetChangedListener {

	PageIndicatorView mPageIndicatorView;
	private AutoScrollViewPager mAutoScrollViewPager;

	public PageIndicatorLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PageIndicatorLayout(Context context) {
		super(context);
		init();
	}

	public void init() {
		setOrientation(LinearLayout.VERTICAL);
		mPageIndicatorView = new PageIndicatorView(getContext());
		mPageIndicatorView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, mPageIndicatorView
						.getIndicatorHeight()));
		addView(mPageIndicatorView);
	}

	public void setViewPager(AutoScrollViewPager view) {
		if (view != null) {
			mAutoScrollViewPager = view;
			setTotalPage(mAutoScrollViewPager.getAdapter().getCount());
			setCurrentPage(true, 0);
			mAutoScrollViewPager.setOnPageChangeListener(PageIndicatorLayout.this);
			mAutoScrollViewPager.setOnDataSetChangedListener(this);
		}
	}

	public PageIndicatorView getPageIndicatorView() {
		return mPageIndicatorView;
	}

	public void setColor(int select, int normal) {
		if (mPageIndicatorView != null)
			mPageIndicatorView.setColor(select, normal);
	}

	public void setCurrentPage(boolean redraw, int nPageIndex) {
		if (mPageIndicatorView != null)
			mPageIndicatorView.setCurrentPage(redraw, nPageIndex);
	}

	public void setTotalPage(int nPageNum) {
		if (mPageIndicatorView != null)
			mPageIndicatorView.setTotalPage(nPageNum);
	}

	@Override
	public void onDataSetChanged() {
		setTotalPage(mAutoScrollViewPager.getAdapter().getCount());
		setCurrentPage(true, 0);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		setCurrentPage(false, arg0);
	}

}
