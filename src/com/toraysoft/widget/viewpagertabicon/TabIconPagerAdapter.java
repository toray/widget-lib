package com.toraysoft.widget.viewpagertabicon;

import android.support.v4.view.PagerAdapter;

public abstract class TabIconPagerAdapter extends PagerAdapter{
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		TabIconView tabIconView = getTabIconView();
		if(tabIconView != null){
			tabIconView.setTabs(getCount());
		}
	}
	
	protected abstract TabIconView getTabIconView();
}
