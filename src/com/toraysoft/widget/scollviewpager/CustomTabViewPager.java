package com.toraysoft.widget.scollviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.toraysoft.widget.R;
import com.toraysoft.widget.scollviewpager.CustomTabView.OnItemChangeListener;

public class CustomTabViewPager extends LinearLayout implements
		OnPageChangeListener, OnItemChangeListener {

	ViewPager mViewPager;
	CustomTabView mCustomTabView;
	OnPageChangeListener mOnPageChangeListener;

	public CustomTabViewPager(Context context) {
		super(context);
		init();
	}

	public CustomTabViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		mCustomTabView = new CustomTabView(getContext());
		mViewPager = new ViewPager(getContext());

		mCustomTabView = new CustomTabView(getContext());
		mCustomTabView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mCustomTabView.setOnItemChangeListener(this);
		addView(mCustomTabView);

		mViewPager = new ViewPager(getContext());
		mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addView(mViewPager);
		mViewPager.setOnPageChangeListener(this);
	}

	protected int getViewPagerChildCount() {
		if (mViewPager == null)
			return -1;
		return mViewPager.getChildCount();
	}

	// protected int getCurrentView(){
	// if(mViewPager==null) return -1;
	// return mViewPager.getCurrentItem();
	// }
	protected View getCurrentView() {
		int position = 0;
		if (mViewPager.getCurrentItem() > 0)
			position = 1;
		return mViewPager.getChildAt(position);
	}

	public void setAdapter(final PagerAdapter adapter) {
		post(new Runnable() {
			public void run() {
				mViewPager.setId(R.id.custom_pager);
				mViewPager.setAdapter(adapter);
				mViewPager.setOffscreenPageLimit(adapter.getCount());
				if (adapter instanceof ITabViewHandler) {
					String[] tabs = ((ITabViewHandler) adapter).getTabs();
					mCustomTabView.setTabs(tabs);
				}
			}
		});
	}

	public PagerAdapter getAdapter() {
		return mViewPager.getAdapter();
	}

	public void setOnPageChangeListener(OnPageChangeListener l) {
		this.mOnPageChangeListener = l;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (mCustomTabView != null) {
			mCustomTabView.onPageScrollStateChanged(state);
		}
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(state);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (mCustomTabView != null) {
			mCustomTabView.onPageScrolled(position, positionOffset,
					positionOffsetPixels);
		}
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrolled(position, positionOffset,
					positionOffsetPixels);
		}
		postInvalidate();
	}

	@Override
	public void onPageSelected(int position) {
		if (mCustomTabView != null) {
			mCustomTabView.onPageSelected(position);
		}
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageSelected(position);
		}
	}

	@Override
	public void onItemChange(int position) {
		if (mViewPager == null)
			return;
		mViewPager.setCurrentItem(position);
	}

	public int getCustomTabViewHeight() {
		return mCustomTabView.getHeight();
	}

	public void changeViewPagerHeight(int height) {
		mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				height));
		mViewPager.invalidate();
	}

	public CustomTabView getCustomTabView() {
		return mCustomTabView;
	}

	public void setMaskBgRes(int res) {
		mCustomTabView.setMaskResBg(res);
	}

	public void setHasMask(boolean mask) {
		mCustomTabView.setHasMask(mask);
	}

	public void setUnread(int count,int position) {
		mCustomTabView.setUnread(count,position);
	}
	
	public void setMessageUnread(String count) {
		mCustomTabView.setMessageUnread(count);
	}

	public void setUserUnread(String count) {
		mCustomTabView.setUserUnread(count);
	}

	public int getCurrentPosition() {
		return mViewPager.getCurrentItem();
	}

}
