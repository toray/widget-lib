package com.toraysoft.widget.customtabviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.toraysoft.widget.R;

public class CustomTabView extends LinearLayout implements OnClickListener {

	LinearLayout layout_tabs, layout_underline;
	View tabs[];
	int tabBackground;
	int currentItem;
	int count;
	int width;

	OnItemChangeListener mOnItemChangeListener;

	public CustomTabView(Context context) {
		super(context);
	}

	public CustomTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		width = getResources().getDisplayMetrics().widthPixels;
		setOrientation(LinearLayout.VERTICAL);
	}

	@Override
	public void onClick(View v) {
		if (v.getTag() == null)
			return;
		int position = (Integer) v.getTag();
		setCurrentItem(position);
		if (mOnItemChangeListener != null) {
			mOnItemChangeListener.onItemChange(position);
		}
	}

	protected void setTabs(View[] tabs) {
		this.tabs = tabs;
		count = tabs.length;
		setLabels();
	}

	private void setLabels() {
		layout_tabs = new LinearLayout(getContext());
		layout_tabs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		layout_tabs.setOrientation(LinearLayout.HORIZONTAL);
		for (int i = 0; i < tabs.length; i++) {
			View tab = tabs[i];
			if (i == currentItem)
				tab.setSelected(true);
			else
				tab.setSelected(false);
			tab.setTag(i);
			tab.setOnClickListener(this);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			lp.weight = 1;
			tab.setLayoutParams(lp);
			layout_tabs.addView(tab);
		}
		addView(layout_tabs);
	}

	void setCurrentItem(int currentItem) {
		if (currentItem < 0)
			currentItem = 0;
		if (currentItem > count)
			currentItem = count;
		this.currentItem = currentItem;
		for (int i = 0; i < count; i++) {
			View v = layout_tabs.findViewWithTag(i);
			if (v != null) {
				if (currentItem == i) {
					v.setSelected(true);
				} else {
					v.setSelected(false);
				}
			}
		}
	}

	protected void onPageScrollStateChanged(int state) {
	}

	protected void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	protected void onPageSelected(int position) {
		setCurrentItem(position);
	}

	public void setOnItemChangeListener(OnItemChangeListener l) {
		this.mOnItemChangeListener = l;
	}

	public interface OnItemChangeListener {
		public void onItemChange(int position);
	}
}
