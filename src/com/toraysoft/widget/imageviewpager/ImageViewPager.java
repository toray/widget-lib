package com.toraysoft.widget.imageviewpager;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ImageViewPager extends RelativeLayout implements
		OnPageChangeListener {

	private ViewPager mViewPager;
	private PageIndicatorView mPageIndicatorView;
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
		mViewPager = new ViewPager(getContext());
		mImagePageAdapter = new ImagePageAdapter();
		mViewPager.setAdapter(mImagePageAdapter);
		mViewPager.setOnPageChangeListener(this);
		mPageIndicatorView = new PageIndicatorView(getContext());
		mPageIndicatorView.setTotalPage(views.size());
		mPageIndicatorView.setCurrentPage(true, 0);
		this.addView(mViewPager);
		this.addView(mPageIndicatorView);
	}

	class ImagePageAdapter extends PagerAdapter {

		public ImagePageAdapter() {
		}

		@Override
		public int getCount() {
			return views.size();
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
			((ViewPager) container).addView(views.get(position));
			return views.get(position);
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		mPageIndicatorView.setCurrentPage(false, arg0);
	}

}
