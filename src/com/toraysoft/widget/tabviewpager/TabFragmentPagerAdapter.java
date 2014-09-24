package com.toraysoft.widget.tabviewpager;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public abstract class TabFragmentPagerAdapter extends FragmentStatePagerAdapter implements ITabViewHandler{
	
	public TabFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

}
