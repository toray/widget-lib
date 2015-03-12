package com.toraysoft.widget.recyclerviewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.toraysoft.widget.recyclerviewpager.RecyclerViewPager.OnPageChangeListener;
import com.toraysoft.widget.recyclerviewpager.RecyclerViewPager.OnRecyclerDataSetChangedListener;

public class RecyclerPageIndicator extends LinearLayout implements
		OnPageChangeListener, OnRecyclerDataSetChangedListener {

	PageIndicatorView mPageIndicatorView;
	private RecyclerViewPager mRecyclerViewPager;

	public RecyclerPageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RecyclerPageIndicator(Context context) {
		super(context);
		init();
	}

	public void init() {
		mPageIndicatorView = new PageIndicatorView(getContext());
		mPageIndicatorView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, mPageIndicatorView
						.getIndicatorHeight()));
		addView(mPageIndicatorView);
	}

	public void setRecyclerViewPager(RecyclerViewPager rvp) {
		if (rvp != null) {
			mRecyclerViewPager = rvp;
			setTotalPage(mRecyclerViewPager.getAdapter().getItemCount());
			setCurrentPage(true, 0);
			mRecyclerViewPager.setOnPageChangeListener(this);
			mRecyclerViewPager.setOnRecyclerDataSetChangedListener(this);
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
	public void onPageChange(int position) {
		setCurrentPage(false, position);
	}

	@Override
	public void onDataSetChanged() {
		setTotalPage(mRecyclerViewPager.getAdapter().getItemCount());
		setCurrentPage(true, 0);
	}

}
