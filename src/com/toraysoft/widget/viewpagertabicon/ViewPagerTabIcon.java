package com.toraysoft.widget.viewpagertabicon;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ViewPagerTabIcon extends RelativeLayout implements OnPageChangeListener{
	ViewPager mViewPager;
	TabIconView mTabIconView;
	
	OnPageChangeListener mOnPageChangeListener;
	
	public ViewPagerTabIcon(Context context) {
		super(context);
		init();
	}

	public ViewPagerTabIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	void init(){
		
	}
	
	public void setAdapter(final PagerAdapter adapter) {
		post(new Runnable() {
			public void run() {
//				mViewPager.setId(R.id.custom_pager);
				mViewPager.setAdapter(adapter);
				mViewPager.setOffscreenPageLimit(adapter.getCount());
				mTabIconView.setTabs(adapter.getCount());
			}
		});
	}
	
	public void setSelectPager(int position){
		if(mViewPager!=null){
			mViewPager.setCurrentItem(position);
		}
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mViewPager = (ViewPager) getChildAt(0);
		mTabIconView = (TabIconView) getChildAt(1);
		mViewPager.setOnPageChangeListener(this);
	}
	
	public void setOnPageChangeListener(OnPageChangeListener l) {
		mOnPageChangeListener = l;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (mTabIconView != null) {
			mTabIconView.onPageScrollStateChanged(state);
		}
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(state);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		if (mTabIconView != null) {
			mTabIconView.onPageSelected(position);
		}
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageSelected(position);
		}
	}

}
