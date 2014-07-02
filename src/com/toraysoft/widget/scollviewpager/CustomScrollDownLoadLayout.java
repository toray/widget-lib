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

public class CustomScrollDownLoadLayout extends FrameLayout implements
				GestureDetector.OnGestureListener{
	GestureDetector mDetector;
	Scroller mScroller;
	FlingRunnable mFlinger;
	View mViewHeader;
	ListView mListView;
	
	int mPading;
	float xLast = 0f;
	float yLast = 0f;
	int mViewHeaderHeight = 0;
	boolean onFling = false;
	boolean onListViewScroll = false;
	
	private CheckForLongPress mPendingCheckForLongPress;
	private CheckForLongPress2 mPendingCheckForLongPress2;
	private boolean mLongPressing;
	private MotionEvent downEvent;
	
	private static final int STATE_HOLD = 0;
	public static final int SCROLL_TO_CLOSE = 1;
	public static final int SCROLL_TO_LOADMORE = 2;
	
	private int mState = STATE_HOLD;
	
	
	public static final double SCALE = 0.9d;
	private static final int CLOSEDELAY = 300;
	
	private boolean hasMore = false;
	
	OnLoadMoreListioner mOnLoadMoreListioner;
	
	public CustomScrollDownLoadLayout(Context context) {
		super(context);
		init();
	}

	public CustomScrollDownLoadLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomScrollDownLoadLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private class CheckForLongPress implements Runnable {
		public void run() {
			if(mListView!=null && mListView.getOnItemLongClickListener() != null){
				postDelayed(mPendingCheckForLongPress2, 100);
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
			CustomScrollDownLoadLayout.super.dispatchTouchEvent(e);
		}
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
				if(mState==SCROLL_TO_LOADMORE &&  mOnLoadMoreListioner!=null){
					postDelayed(new Runnable() {
						public void run() {
							mOnLoadMoreListioner.onLoadMore();
						}
					},300);
				}
				mState = STATE_HOLD;
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
		if(mListView!=null){
			mListView.offsetTopAndBottom((int) -deltaY);
		}
		postInvalidate();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(!hasMore) return super.dispatchTouchEvent(ev);
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
	            if (handled){
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
					if((mListView.getFirstVisiblePosition()==0 && (mListView.getChildCount()>0 && 
							mListView.getChildAt(0).getTop()>=0 || mListView.getChildCount()==0))
							|| Math.abs(top)!=height){
		            	if(Math.abs(top)!=0){
		            		mState = SCROLL_TO_LOADMORE;
		            		mFlinger.startUsingDistance(top, CLOSEDELAY);
		            	}else{
		            		postDelayed(new Runnable() {
								public void run() {
									mOnLoadMoreListioner.onLoadMore();
								}
							},300);
		            	}
		            	mPading = 0;
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
			mListView = (ListView)getChildAt(1);
		}
	}
	
	@Override
	protected void onLayout(boolean flag, int i, int j, int k, int l) {
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		if(mViewHeader!=null){
			mViewHeaderHeight = mViewHeader.getMeasuredHeight();
			mViewHeader.layout(0, (int)(mPading)-mViewHeaderHeight, w, mPading);
			mListView.layout(0, mPading, w, h+mPading);
		}
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float velocityX,
			float velocityY) {
		if(onListViewScroll || mState==SCROLL_TO_CLOSE || !hasMore){
			return true;
		}
		if(mViewHeader==null) return false;
		int top = mViewHeader.getTop();
		int height = mViewHeader.getHeight();
		if((mListView.getFirstVisiblePosition()==0 && (mListView.getChildCount()>0 && 
				mListView.getChildAt(0).getTop()>=0 || mListView.getChildCount()==0))
				|| Math.abs(top)!=height){
			if(Math.abs(top)!=0){
				mState = SCROLL_TO_LOADMORE;
        		mFlinger.startUsingDistance(top, CLOSEDELAY);
        	}else{
        		postDelayed(new Runnable() {
					public void run() {
						mOnLoadMoreListioner.onLoadMore();
					}
				},300);
        	}
        	mPading = 0;
		}
		onFling = true;
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float distanceX,
			float distanceY) {
		if(mState==SCROLL_TO_CLOSE || !hasMore){
			return false;
		}
		if(mListView!=null){
			if(mListView.getFirstVisiblePosition()==0 && (mListView.getChildCount()>0 && 
					mListView.getChildAt(0).getTop()==0 || mListView.getChildCount()==0) && distanceY<0){
			
			}else if(mListView.getChildCount()>0 && mPading==-mViewHeaderHeight || onListViewScroll){
				onListViewScroll = true;
				return false;
			}else if(mListView.getChildCount()>0 && mListView.getFirstVisiblePosition()>0 
					&& distanceY<0 || onListViewScroll){
				onListViewScroll = true;
				return false;
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
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}
	
	public void setLoadMoreComplete(){
		int top = mViewHeader.getTop();
		int height = mViewHeader.getHeight();
		mFlinger.startUsingDistance(height+top, CLOSEDELAY);
		mPading = 0;
		mState = SCROLL_TO_CLOSE;
	}
	
	public void setOnLoadMoreListioner(OnLoadMoreListioner l) {
		this.mOnLoadMoreListioner = l;
	}
	
	public interface OnLoadMoreListioner {
		public abstract void onLoadMore();
	}
	
}
