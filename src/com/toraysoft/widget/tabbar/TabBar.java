package com.toraysoft.widget.tabbar;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toraysoft.widget.R;

public class TabBar extends LinearLayout implements OnClickListener,OnPageChangeListener {

	int childResID;
	int position;
	ViewPager mViewPager;
	
	OnTabItemSelectListener mOnTabItemSelectListener;
	
	List<TabItem> items;

	public TabBar(Context context) {
		super(context);
		init();
	}

	public TabBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setGravity(Gravity.CENTER_VERTICAL);
		items = new ArrayList<TabItem>();
	}

	@Override
	public void onClick(View v) {
		if (v.getTag() != null && v.getTag() instanceof TabItem) {
			TabItem item = (TabItem) v.getTag();
			if(item.isTab){
				mViewPager.setCurrentItem(item.index);
			}
			if(mOnTabItemSelectListener!=null)
				mOnTabItemSelectListener.onSelect(item);
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		setCurrentItemPosition(position);
	}

	public void setChildResID(int childResID) {
		this.childResID = childResID;
	}

	public void addTabItem(TabItem item) {
		if (childResID == 0) {
			return;
		}
		View child = LayoutInflater.from(getContext())
				.inflate(childResID, null);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		child.setLayoutParams(lp);
		child.setTag(item);
		ImageView iv_icon = (ImageView) child.findViewById(R.id.iv_icon);
		TextView tv_title = (TextView) child.findViewById(R.id.tv_title);
		item.iv_icon = iv_icon;
		item.tv_title = tv_title;
		iv_icon.setImageDrawable(item.icon);
		if (TextUtils.isEmpty(item.title)) {
			tv_title.setVisibility(View.GONE);
		} else {
			tv_title.setText(item.title);
			tv_title.setVisibility(View.VISIBLE);
		}
		child.setOnClickListener(this);
		addView(child);
		if(item.isTab){
			item.index = items.size();
			items.add(item);
		}
	}
	
	public void setViewPager(ViewPager mViewPager) {
		this.mViewPager = mViewPager;
		if(mViewPager!=null){
			mViewPager.setOnPageChangeListener(this);
		}
	}
	
	public void setCurrentItemPosition(int position){
		if(position>items.size()) position=0;
		this.position = position;
		for (int i = 0; i < items.size(); i++) {
			TabItem item = items.get(i);
			if(position==i){
				item.setSelect(true);
			}else{
				item.setSelect(false);
			}
		}
	}
	
	public void setOnTabItemSelectListener(OnTabItemSelectListener l) {
		this.mOnTabItemSelectListener = l;
	}

	public interface OnTabItemSelectListener {
		void onSelect(TabItem item);
	}

}
