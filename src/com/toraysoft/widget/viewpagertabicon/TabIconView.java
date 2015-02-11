package com.toraysoft.widget.viewpagertabicon;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.toraysoft.widget.R;

public class TabIconView extends LinearLayout {

	int iconResId;
	int count;
	int currentItem;
	int iconWidth;

	public TabIconView(Context context) {
		super(context);
	}

	public TabIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		setOrientation(LinearLayout.HORIZONTAL);
	}

	void init(AttributeSet attrs) {
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
				R.styleable.TabIconView);
		iconResId = typedArray.getResourceId(R.styleable.TabIconView_iconSrc,0);
		iconWidth = (int) typedArray.getDimension(R.styleable.TabIconView_iconWidth, LayoutParams.WRAP_CONTENT);
		typedArray.recycle();
	}

	public void setTabs(int count) {
		removeAllViews();
		this.count = count;
		if(count<=1){
			setVisibility(INVISIBLE);
		}else{
			setVisibility(VISIBLE);
		}
		for (int i = 0; i < count; i++) {
			LinearLayout parent = new LinearLayout(getContext());
			ImageView iv = new ImageView(getContext());
			iv.setImageResource(iconResId);
			if (i == currentItem)
				iv.setSelected(true);
			else
				iv.setSelected(false);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			iv.setLayoutParams(params);
			parent.setGravity(Gravity.CENTER);
			parent.setLayoutParams(new LayoutParams(iconWidth,
					LayoutParams.WRAP_CONTENT));
			parent.addView(iv);
			addView(parent);
		}
	}

	void setCurrentItem(int currentItem) {
		if (currentItem < 0)
			currentItem = 0;
		if (currentItem > count)
			currentItem = count;
		this.currentItem = currentItem;
		for (int i = 0; i < count; i++) {
			LinearLayout parent = (LinearLayout) getChildAt(i);
			if(parent!=null){
				ImageView iv = (ImageView) parent.getChildAt(0);
				if (iv != null) {
					if (currentItem == i) {
						iv.setSelected(true);
					} else {
						iv.setSelected(false);
					}
				}
			}
		}
	}

	void onPageScrollStateChanged(int state) {

	}

	void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	void onPageSelected(int position) {
		setCurrentItem(position);
	}
}
