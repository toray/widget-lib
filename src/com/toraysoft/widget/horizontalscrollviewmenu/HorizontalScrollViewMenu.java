package com.toraysoft.widget.horizontalscrollviewmenu;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class HorizontalScrollViewMenu extends HorizontalScrollView {

	LinearLayout mLinearLayout,mLinearLayoutMenu;
	View viewLeft,viewRight;
	ViewPager mViewPager;
	List<ViewHolder> points;

	int currentIndex = 0;
	int downX = 0;
	
	int screenWidth = 0;

	public HorizontalScrollViewMenu(Context context) {
		super(context);
		init();
	}

	public HorizontalScrollViewMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HorizontalScrollViewMenu(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	void init() {
		setHorizontalScrollBarEnabled(false);
		points = new ArrayList<ViewHolder>();
		
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		
		mLinearLayout = new LinearLayout(getContext());
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLinearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		addView(mLinearLayout);
		
		viewLeft = new LinearLayout(getContext());
		viewLeft.setLayoutParams(new LayoutParams(screenWidth/2, LayoutParams.WRAP_CONTENT));
		mLinearLayout.addView(viewLeft);
		
		mLinearLayoutMenu = new LinearLayout(getContext());
		mLinearLayoutMenu.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mLinearLayoutMenu.setOrientation(LinearLayout.HORIZONTAL);
		mLinearLayout.addView(mLinearLayoutMenu);
		
		viewRight = new LinearLayout(getContext());
		viewRight.setLayoutParams(new LayoutParams(screenWidth/2, LayoutParams.WRAP_CONTENT));
		mLinearLayout.addView(viewRight);
	}

	public void addMenu(View view) {
		if (mLinearLayoutMenu != null) {
			mLinearLayoutMenu.addView(view);
			final int index = mLinearLayoutMenu.getChildCount()-1;
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					setCurrent(index);
				}
			});
		}
	}
	
	public void setViewPager(ViewPager viewPager){
		this.mViewPager = viewPager;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		receiveChildInfo();
	}

	public void receiveChildInfo() {
		if (mLinearLayoutMenu != null && getWidth()>0) {
			int count = mLinearLayoutMenu.getChildCount();
			int l = (screenWidth - getWidth()) / 2;
			points.clear();
			for (int i = 0; i < count; i++) {
				int left = mLinearLayoutMenu.getChildAt(i).getLeft();
				int width = mLinearLayoutMenu.getChildAt(i).getWidth();
				ViewHolder vh = new ViewHolder();
				vh.left = left + width / 2 + l;
				vh.width = width;
				points.add(vh);
			}
			smoothScrollToCurrent();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			int x = getScrollX();
			for (int i = 0; i < points.size(); i++) {
				ViewHolder vh = points.get(i);
				if(x<vh.left + vh.width/2){
					setCurrent(i);
					break;
				}
				if(i==points.size()-1){
					setCurrent(i);
				}
			}
			return true;

		}
		return super.onTouchEvent(ev);
	}

	void smoothScrollToCurrent() {
		if(points.size()>0){
			smoothScrollTo(points.get(currentIndex).left, 0);
			setViewPager(currentIndex);
		}
	}

	void smoothScrollToNextPage() {
		if (currentIndex < mLinearLayoutMenu.getChildCount() - 1) {
			currentIndex++;
			smoothScrollTo(points.get(currentIndex).left, 0);
			setViewPager(currentIndex);
		}
	}

	void smoothScrollToPrePage() {
		if (currentIndex > 0) {
			currentIndex--;
			smoothScrollTo(points.get(currentIndex).left, 0);
			setViewPager(currentIndex);
		}
	}

	public boolean setCurrent(int current) {
		if(current<0){
			current = 0;
		}
		if(current>mLinearLayoutMenu.getChildCount() - 1){
			current = mLinearLayoutMenu.getChildCount() - 1;
		}
		if (current >= 0 && current <= mLinearLayoutMenu.getChildCount() - 1) {
			smoothScrollTo(points.get(current).left, 0);
			currentIndex = current;
			setViewPager(currentIndex);
			return true;
		}
		return false;
	}
	
	void setViewPager(int item){
		if(mViewPager!=null){
			mViewPager.setCurrentItem(item);
		}
	}
	
	class ViewHolder{
		public int left;
		public int width;
	}

}
