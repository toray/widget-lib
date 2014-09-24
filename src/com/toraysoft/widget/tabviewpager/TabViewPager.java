package com.toraysoft.widget.tabviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TabViewPager extends LinearLayout implements
				OnPageChangeListener{
	ViewPager mViewPager;
	TabView mTabView;
	
	public TabViewPager(Context context) {
		super(context);
		init();
	}

	public TabViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		
	}
	
	public void setAdapter(final PagerAdapter adapter) {
		post(new Runnable() {
			public void run() {
//				mViewPager.setId(R.id.custom_pager);
				mViewPager.setAdapter(adapter);
				mViewPager.setOffscreenPageLimit(adapter.getCount());
				if (adapter instanceof ITabViewHandler) {
					String[] tabs = ((ITabViewHandler) adapter).getTabs();
					mTabView.setTabs(tabs);
				}
			}
		});
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTabView = (TabView) getChildAt(0);
		mViewPager = (ViewPager) getChildAt(1);
		mViewPager.setOnPageChangeListener(this);
	}
	

	@Override
	public void onPageScrollStateChanged(int state) {
		if (mTabView != null) {
			mTabView.onPageScrollStateChanged(state);
		}
//		if (mOnPageChangeListener != null) {
//			mOnPageChangeListener.onPageScrollStateChanged(state);
//		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (mTabView != null) {
			mTabView.onPageScrolled(position, positionOffset,
					positionOffsetPixels);
		}
//		if (mOnPageChangeListener != null) {
//			mOnPageChangeListener.onPageScrolled(position, positionOffset,
//					positionOffsetPixels);
//		}
//		postInvalidate();
	}

	@Override
	public void onPageSelected(int position) {
		if (mTabView != null) {
			mTabView.onPageSelected(position);
		}
//		if (mOnPageChangeListener != null) {
//			mOnPageChangeListener.onPageSelected(position);
//		}
	}

//	@Override
//	public void onItemChange(int position) {
//		if (mViewPager == null)
//			return;
//		mViewPager.setCurrentItem(position);
//	}
}
