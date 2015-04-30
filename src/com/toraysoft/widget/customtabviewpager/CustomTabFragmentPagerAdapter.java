package com.toraysoft.widget.customtabviewpager;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public abstract class CustomTabFragmentPagerAdapter extends FragmentStatePagerAdapter implements ICustomTabViewHandler{
	
	public CustomTabFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}
}
