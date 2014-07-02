package com.toraysoft.widget.scollviewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Scroller;

public class CustomScrollDownLayout extends FrameLayout implements
				GestureDetector.OnGestureListener{
	
	GestureDetector mDetector;
	Scroller mScroller;
	FlingRunnable mFlinger;
	View mViewHeader;
//	View mCustomViewPager;
	CustomTabViewPager mCustomTabViewPager;
	
	int mPading;
	float xLast = 0f;
	float yLast = 0f;
	int mViewHeaderHeight = 0;
	boolean onFling = false;
	boolean onHorizontalScroll = false;
	boolean onListViewScroll = false;
	
	private CheckForLongPress mPendingCheckForLongPress;
	private CheckForLongPress2 mPendingCheckForLongPress2;
	private boolean mLongPressing;
	private MotionEvent downEvent;
	
	public static final double SCALE = 0.9d;
	private static final int CLOSEDELAY = 300;

	public CustomScrollDownLayout(Context context) {
		super(context);
		init();
	}

	public CustomScrollDownLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomScrollDownLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private class CheckForLongPress implements Runnable {
		public void run() {
			if(mCustomTabViewPager!=null && mCustomTabViewPager.getAdapter()!=null){
				ListView listview = ((TabViewPagerAdapter)mCustomTabViewPager.getAdapter()).getPrimaryItemListView();
				if(listview!=null && listview.getOnItemLongClickListener() != null){
					postDelayed(mPendingCheckForLongPress2, 100);
				}
			}
		}
	}

	private class CheckForLongPress2 implements Runnable {
		public void run() {
			mLongPressing = true;
			MotionEvent e = MotionEvent.obtain(downEvent.getDownTime(),
					downEvent.getEventTime()
							+ ViewConfiguration.getLongPressTimeout(),
					MotionEvent.ACTION_CANCEL, downEvent.getX(), downEvent
							.getY(), downEvent.getMetaState());
			CustomScrollDownLayout.super.dispatchTouchEvent(e);
		}
	}
	
	private void init() {
		mDetector = new GestureDetector(getContext(), this);
		mScroller = new Scroller(getContext());
		mFlinger = new FlingRunnable();
		
		mPendingCheckForLongPress = new CheckForLongPress();
		mPendingCheckForLongPress2 = new CheckForLongPress2();
	}
	
	private void move(int deltaY){
		if(mViewHeader!=null){
			mViewHeader.offsetTopAndBottom((int) -deltaY);
		}
		if(mCustomTabViewPager!=null){
			mCustomTabViewPager.offsetTopAndBottom((int) -deltaY);
		}
		postInvalidate();
	}
	
	class FlingRunnable implements Runnable {

		private void startCommon() {
			removeCallbacks(this);
		}

		public void run() {
			boolean noFinish = mScroller.computeScrollOffset();
			int curY = mScroller.getCurrY();
			int deltaY = curY - mLastFlingY;
			if (noFinish) {
				move(deltaY);
				mLastFlingY = curY;
				post(this);
			} else {
				removeCallbacks(this);
			}
		}

		public void startUsingDistance(int distance, int duration) {
			if (distance == 0)
				distance--;
			startCommon();
			mLastFlingY = 0;
			mScroller.startScroll(0, 0, 0, distance, duration);
			post(this);
		}

		private int mLastFlingY;
		private Scroller mScroller;

		public FlingRunnable() {
			mScroller = new Scroller(getContext());
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = true;
		
		if (mLongPressing && ev.getAction() != MotionEvent.ACTION_DOWN) {
			return false;
		}
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			mLongPressing = true;
		}
		
		handled = mDetector.onTouchEvent(ev);
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
	            xLast = ev.getX();  
	            yLast = ev.getY(); 
	            downEvent = ev;
				mLongPressing = false;
	            onFling = false;
				onHorizontalScroll = false;
				onListViewScroll = false;
				super.dispatchTouchEvent(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				float curX = ev.getX();  
	            float curY = ev.getY();
	            float yLast = this.yLast;
	            float x = curX - this.xLast;
	            float y = curY - this.yLast;
	            this.xLast = curX;  
	            this.yLast = curY;
	            if (Math.abs(x) < Math.abs(y) && handled && !onHorizontalScroll){
		  			mScroller.startScroll(0,(int)yLast, 0, (int)curY);
		  			ev.setAction(MotionEvent.ACTION_CANCEL);
		  			return super.dispatchTouchEvent(ev);
		  		}else{
		  			return super.dispatchTouchEvent(ev);
		  		}
			case MotionEvent.ACTION_CANCEL:
				super.dispatchTouchEvent(ev);
				break;
			case MotionEvent.ACTION_UP:
				super.dispatchTouchEvent(ev);
				if(!onFling){
					int top = 0;
					int height = 0;
					if(mViewHeader!=null){
						top = mViewHeader.getTop();
						height = mViewHeader.getHeight();
					}
		            if(Math.abs(top)<height*2/3){
		            	if(Math.abs(top)!=0){
		            		mFlinger.startUsingDistance(top, CLOSEDELAY);
		            	}
		            	mPading = 0;
		            }else{
		            	if(Math.abs(top)!=height){
		            		mFlinger.startUsingDistance(height+top, CLOSEDELAY);
		            	}
		            	mPading = -height;
		            }
				}
				break;
		}
		return true;
	}
	
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if(getChildCount()>1){
			mViewHeader = getChildAt(0);
			mCustomTabViewPager = (CustomTabViewPager)getChildAt(1);
		}
	}
	
	@Override
	protected void onLayout(boolean flag, int i, int j, int k, int l) {
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		if(mViewHeader!=null){
			mViewHeaderHeight = mViewHeader.getMeasuredHeight();
			mViewHeader.layout(0, (int)(mPading), w, mViewHeaderHeight+mPading);
			mCustomTabViewPager.layout(0, mViewHeaderHeight+mPading, w, mViewHeaderHeight+h+mPading);
		}
	}
	
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(Math.abs(velocityX)>Math.abs(velocityY)) return false;
		if(onListViewScroll){
			return true;
		}
		if(mViewHeader==null) return false;
		int top = mViewHeader.getTop();
		int height = mViewHeader.getHeight();
		if(velocityY>=0){
			if(Math.abs(top)!=0){
        		mFlinger.startUsingDistance(top, CLOSEDELAY);
        	}
        	mPading = 0;
		}else{
			if(Math.abs(top)!=height){
        		mFlinger.startUsingDistance(height+top, CLOSEDELAY);
        	}
			mPading = -height;
		}
		onFling = true;
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		if(Math.abs(distanceX)>10 && Math.abs(distanceX)>Math.abs(distanceY) || onHorizontalScroll){
			onHorizontalScroll = true;
			return false;
		}
//		View current_view = ((TabViewPagerAdapter)mCustomTabViewPager.getAdapter()).getPrimaryItem();
//		ListView listview = (ListView) current_view.findViewById(R.id.listview);
//		ListView listview = ((TabViewPagerAdapter)mCustomTabViewPager.getAdapter()).getPrimaryItemListView();
//		Log.v("listview",""+listview.getTag());
//		ViewPagerAdapter v = (ViewPagerAdapter) mViewPager.getAdapter(); 
//		View current_view = v.map.get(mViewPager.getCurrentItem());
//		ListView listview = (ListView) current_view.findViewById(R.id.listview);
//		System.out.println("count:"+listview.getCount());
//		System.out.println("FirstVisiblePosition:"+listview.getFirstVisiblePosition());
//		System.out.println("getTop:"+listview.getChildAt(0).getTop());
//		
//		mCustomViewPager.
//		System.out.println("fvp:"+listview.getFirstVisiblePosition()+"/"
//					+"t:"+listview.getTop()+"/"
//						+"y:"+distanceY+"/"
//							+"cc:"+listview.getChildCount()+"/"
//								+"mPading:"+mPading+"/"
//									+"mViewHeaderHeight:"+mViewHeaderHeight+"/"
//										+"onListViewScroll:"+onListViewScroll+"/");
		if(mCustomTabViewPager!=null && mCustomTabViewPager.getAdapter()!=null){
			ListView listview = ((TabViewPagerAdapter)mCustomTabViewPager.getAdapter()).getPrimaryItemListView();
			if(listview!=null){
				if(listview.getFirstVisiblePosition()==0 && (listview.getChildCount()>0 && 
						listview.getChildAt(0).getTop()==0 || listview.getChildCount()==0) && distanceY<0){
				}else if(listview.getChildCount()>0 && mPading==-mViewHeaderHeight || onListViewScroll){
					onListViewScroll = true;
					return false;
				}else if(listview.getChildCount()>0 && listview.getFirstVisiblePosition()>0 && distanceY<0 || onListViewScroll){
					onListViewScroll = true;
					return false;
				}
			}
		}
		
		distanceY = (float) ((double) distanceY * SCALE);
		if(mViewHeader==null) return false;
		int top = mViewHeader.getTop();
		int height = mViewHeader.getHeight();
		if(distanceY>0){
			int h = top+height;
			if(h<=0){
				move(h);
			}else{
				if(distanceY-top>height){
					distanceY = h;
				}
				move((int)distanceY);
			}
		}else{
			if(top>=0)
				move(top);
			else{
				if(top-distanceY>0)
					distanceY = top;
				move((int)distanceY);
			}
		}
//		if (mListView.getCount() == 0) {
//			flag = true;
//		} else {
//			View c = mListView.getChildAt(0);
//			if (mListView.getFirstVisiblePosition() == 0 && c != null
//					&& c.getTop() == 0) {
//				flag = true;
//			}
//		}
//		if(mListView.getCount()>0){
//			View c = mListView.getChildAt(mListView.getChildCount()-1);
//			if (mListView.getLastVisiblePosition() == mListView.getCount()-1 && c != null
//					&& c.getBottom() == getMeasuredHeight()) {
//				lastFlag = true;
//			}
//		}
//		if (deltaY < 0F && flag || getChildAt(0).getTop() > -MAX_LENGHT) { // deltaY
//			handled = move(deltaY, false);
//		}
//		else if(deltaY>0F && lastFlag || getChildAt(2).getTop()<getMeasuredHeight()){
//			handled = moveUp(deltaY, false);
//		}
//		else
//			handled = false;
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	
}
