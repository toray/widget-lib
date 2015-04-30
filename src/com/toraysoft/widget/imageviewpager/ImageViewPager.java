package com.toraysoft.widget.imageviewpager;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ImageViewPager extends RelativeLayout {

	private AutoScrollViewPager mViewPager;
	private PageIndicatorLayout mPageIndicatorLayout;
	private List<View> views;
	private ImagePageAdapter mImagePageAdapter;

	public ImageViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageViewPager(Context context) {
		super(context);
	}

	public void initViews(List<View> views) {
		this.views = views;
		mViewPager = new AutoScrollViewPager(getContext());
		mImagePageAdapter = new ImagePageAdapter();
		mViewPager.setAdapter(mImagePageAdapter);
		mPageIndicatorLayout = new PageIndicatorLayout(getContext());
		LayoutParams mLayoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mLayoutParams.bottomMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources()
						.getDisplayMetrics());
		mPageIndicatorLayout.setLayoutParams(mLayoutParams);
		mPageIndicatorLayout.setViewPager(mViewPager);
		this.addView(mViewPager);
		this.addView(mPageIndicatorLayout);
	}

	public void initViews(List<View> views,
			PageIndicatorLayout mPageIndicatorLayout) {
		this.views = views;
		mViewPager = new AutoScrollViewPager(getContext());
		mImagePageAdapter = new ImagePageAdapter();
		mViewPager.setAdapter(mImagePageAdapter);
		this.mPageIndicatorLayout = mPageIndicatorLayout;
		mPageIndicatorLayout.setViewPager(mViewPager);
		if (mPageIndicatorLayout != null) {
			mPageIndicatorLayout.setTotalPage(views.size());
			mPageIndicatorLayout.setCurrentPage(true, 0);
		}
		this.addView(mViewPager);
	}

	public AutoScrollViewPager getAutoScrollViewPager() {
		return mViewPager;
	}

	class ImagePageAdapter extends PagerAdapter {

		public ImagePageAdapter() {
		}

		@Override
		public int getCount() {
			return views == null ? 0 : views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if(views.get(position).getParent() != null) {
				((ViewGroup)views.get(position).getParent()).removeView(views.get(position));	
			}
			((ViewPager) container).addView(views.get(position));
			return views.get(position);
		}

	}

	public void setPagePosition(int pos) {
		if (pos >= 0 && pos < views.size()) {
			if (mViewPager != null)
				mViewPager.setCurrentItem(pos);
			if (mPageIndicatorLayout != null)
				mPageIndicatorLayout.setCurrentPage(true, pos);
		}
	}

}
