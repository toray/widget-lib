package com.toraysoft.widget.scollviewpager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.widget.ListView;

public abstract class TabViewPagerAdapter extends PagerAdapter implements ITabViewHandler{
	
	private View mCurrentView;
	
	public abstract int getListViewId();
	
	@Override
	public void setPrimaryItem(View container, int position, Object object) {
		this.mCurrentView =  (View)object;
	}
	
	public View getPrimaryItem() {
		return mCurrentView;
	}
	
	public ListView getPrimaryItemListView() {
		return (ListView) mCurrentView.findViewById(getListViewId());
	}
}
