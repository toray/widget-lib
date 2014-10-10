package com.toraysoft.widget.simplepullview;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.toraysoft.widget.R;

public class SimplePullView extends FrameLayout implements
		GestureDetector.OnGestureListener{
	String TAG = "SimplePullView";
	
	private ImageView mUpdateArrow;
	private ImageView mLoadMoreArrow;
	private String mDate;
	private ProgressBar mUpdateProgressBar;
	private ProgressBar mLoadMoreProgressBar;
	private int mState;
	private TextView mUpdateTitle;
	private TextView mLoadMoreTitle;
	ListView mListView;
	private GestureDetector mDetector;
	private FlingRunnable mFlinger;
	private FlingLoadMoreRunnable mFlingLoadMore;
	private int mPading;
	private int mDestPading;
	private int mLastTop;
	private int mBottomLastTop;
	private LinearLayout mFirstChild;
	private LinearLayout mLastChild;
	private FrameLayout mUpdateContent;
	private FrameLayout mLoadMoreContent;
	private OnRefreshListioner mRefreshListioner;
	private boolean isShowHead = true;
	private boolean hasRefresh = true;
	private boolean hasMore = false;
	private boolean listviewDoScroll = false;
	private boolean isFirstLoading = false;
	private boolean mLongPressing;//
	private boolean mPendingRemoved = false;// 
	private String pulldowntorefresh;
	private String releasetorefresh;
	private String pulluptoload;
	private String releasetoload;
	private String loading;
	private String update_time;
	Rect r = new Rect();
	private MotionEvent downEvent;
	private CheckForLongPress mPendingCheckForLongPress = new CheckForLongPress();
	private CheckForLongPress2 mPendingCheckForLongPress2 = new CheckForLongPress2();
	private float lastY;
	
	private float xDistance = 0;
	private float yDistance = 0;
	private float xLast = 0;
	private float yLast = 0;
	private boolean isHandler = false;
	
	public static int MAX_LENGHT = 0;
	public static final int STATE_REFRESH = 1;
	public static final int STATE_LOADMORE = 10;
	public static final int SCROLL_TO_CLOSE = 2;
	public static final int SCROLL_TO_REFRESH = 3;
	public static final int SCROLL_TO_LOADMORE = 4;
	public static final double SCALE = 0.9d;
	private static final int CLOSEDELAY = 300;
	private static final int REFRESHDELAY = 300;
	private Animation mAnimationDown;
	private Animation mAnimationUp;
	
	
	private AnimationListener mAnimationDownListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {}
		
		@Override
		public void onAnimationRepeat(Animation animation) {}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			mUpdateArrow.setImageResource(R.drawable.arrow_up);
			mLoadMoreArrow.setImageResource(R.drawable.arrow_down);
		}
	};
	
	private AnimationListener mAnimationUpListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {}
		
		@Override
		public void onAnimationRepeat(Animation animation) {}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			mUpdateArrow.setImageResource(R.drawable.arrow_down);
			mLoadMoreArrow.setImageResource(R.drawable.arrow_up);
		}
	};
	
	private class CheckForLongPress implements Runnable {
		public void run() {
			if (mListView.getOnItemLongClickListener() == null) {
			} else {
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
			SimplePullView.super.dispatchTouchEvent(e);
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
				if(mState==SCROLL_TO_REFRESH)
					move(deltaY, true);
				else if(mState==SCROLL_TO_LOADMORE)
					moveUp(deltaY, true);
				else
					move(deltaY, true);
				mLastFlingY = curY;
				post(this);
			} else {
				removeCallbacks(this);
				if (mState == SCROLL_TO_CLOSE) {
					mState = -1;
				}
				if(!hasRefresh && mState==STATE_REFRESH){
					onRefreshComplete();
				}
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
	
	class FlingLoadMoreRunnable implements Runnable {

		private void startCommon() {
			removeCallbacks(this);
		}

		public void run() {
			boolean noFinish = mScroller.computeScrollOffset();
			int curY = mScroller.getCurrY();
			int deltaY = curY - mLastFlingY;
			if (noFinish) {
				moveUp(deltaY, true);
				mLastFlingY = curY;
				post(this);
			} else {
				removeCallbacks(this);
				if (mState == SCROLL_TO_CLOSE) {
					mState = -1;
				}
				if(!hasMore && mState==STATE_LOADMORE){
					onLoadMoreComplete();
				}
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

		public FlingLoadMoreRunnable() {
			mScroller = new Scroller(getContext());
		}
	}
	
	public interface OnRefreshListioner {
		public abstract void onRefresh();

		public abstract void onLoadMore();
	}
	
	
	public SimplePullView(Context context) {
		super(context);
		mDetector = new GestureDetector(context, this);
		mFlinger = new FlingRunnable();
		mFlingLoadMore = new FlingLoadMoreRunnable();
		init();
		addRefreshBar();
	}

	public SimplePullView(Context context, AttributeSet att) {
		super(context, att);
		mDetector = new GestureDetector(context,this);
		mFlinger = new FlingRunnable();
		mFlingLoadMore = new FlingLoadMoreRunnable();
		init();
		addRefreshBar();
	}
	
	private void addRefreshBar() {
		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.refresh_bar2, null);
		addView(view);
		mFirstChild = (LinearLayout) view;
		mUpdateArrow = (ImageView) view.findViewById(R.id.iv_arrow);
		mUpdateProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
//		mUpdateContent = (FrameLayout) view.findViewById(
//				R.id.iv_content);
//
//		mUpdateArrow = new ImageView(getContext());
//		FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		mUpdateArrow.setScaleType(ImageView.ScaleType.FIT_CENTER);
//		mUpdateArrow.setLayoutParams(layoutparams);
		mUpdateArrow.setImageResource(R.drawable.arrow_down);
//
//		mUpdateContent.addView(mUpdateArrow);
//
//		FrameLayout.LayoutParams layoutparams1 = new FrameLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		layoutparams1.gravity = Gravity.CENTER;
//		mUpdateProgressBar = new ProgressBar(getContext(), null,
//				android.R.attr.progressBarStyleSmallInverse);
//		mUpdateProgressBar.setIndeterminate(false);
//		int i = getResources().getDimensionPixelSize(R.dimen.updatebar_padding);
//		mUpdateProgressBar.setPadding(i, i, i, i);
//		mUpdateProgressBar.setLayoutParams(layoutparams1);
//
//		mUpdateContent.addView(mUpdateProgressBar);

		mUpdateTitle = (TextView) findViewById(R.id.tv_title);
	}
	
	private void addLoadMoreBar(){
		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.loadmore_bar2, null);
		addView(view);
		mLastChild = (LinearLayout) view;
		mLoadMoreArrow = (ImageView) view.findViewById(R.id.iv_arrow);
		mLoadMoreProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
//		mLoadMoreContent = (FrameLayout) view.findViewById(
//				R.id.iv_content);
//
//		mLoadMoreArrow = new ImageView(getContext());
//		FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		mLoadMoreArrow.setScaleType(ImageView.ScaleType.FIT_CENTER);
//		mLoadMoreArrow.setLayoutParams(layoutparams);
		mLoadMoreArrow.setImageResource(R.drawable.arrow_up);
//
//		mLoadMoreContent.addView(mLoadMoreArrow);
//
//		FrameLayout.LayoutParams layoutparams1 = new FrameLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		layoutparams1.gravity = Gravity.CENTER;
//		mLoadMoreProgressBar = new ProgressBar(getContext(), null,
//				android.R.attr.progressBarStyleSmallInverse);
//		mLoadMoreProgressBar.setIndeterminate(false);
//		int i = getResources().getDimensionPixelSize(R.dimen.updatebar_padding);
//		mLoadMoreProgressBar.setPadding(i, i, i, i);
//		mLoadMoreProgressBar.setLayoutParams(layoutparams1);
//
//		mLoadMoreContent.addView(mLoadMoreProgressBar);

		mLoadMoreTitle = (TextView) view.findViewById(R.id.tv_title);
		if(!hasMore){
			view.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mListView = (ListView) getChildAt(1);
		addLoadMoreBar();
	}
	
	private void init() {
		MAX_LENGHT = getResources().getDimensionPixelSize(
				R.dimen.updatebar_height);// 62.0dip
		setDrawingCacheEnabled(false);
		setClipChildren(false);
		mDetector.setIsLongpressEnabled(false);
		mPading = -MAX_LENGHT;
		mLastTop = -MAX_LENGHT;
		
		pulldowntorefresh = getContext().getString(R.string.drop_dowm);
		releasetorefresh = getContext().getString(R.string.release_update);
		pulluptoload = getContext().getString(R.string.drop_up);
		releasetoload = getContext().getString(R.string.release_loadmore);
		loading = getContext().getText(R.string.doing_update).toString();
		update_time = getContext().getText(R.string.update_time).toString();
		
		mAnimationUp = AnimationUtils.loadAnimation(getContext(),
				R.anim.rotate_up);
		mAnimationUp.setAnimationListener(mAnimationUpListener);
		mAnimationDown = AnimationUtils.loadAnimation(getContext(),
				R.anim.rotate_down);
		mAnimationDown.setAnimationListener(mAnimationDownListener);
		
	}
	
	
	private boolean move(float deltaY, boolean auto) {
		if (deltaY > 0 && mFirstChild.getTop() == -MAX_LENGHT) {
			mPading = -MAX_LENGHT;
			return false;
		}

		if (auto) {
			if (mFirstChild.getTop() - deltaY < mDestPading) {
				deltaY = mFirstChild.getTop() - mDestPading;
			}
			mFirstChild.offsetTopAndBottom((int) -deltaY);
			mListView.offsetTopAndBottom((int) -deltaY);
			mLastChild.offsetTopAndBottom((int) -deltaY);
			mPading = mFirstChild.getTop();
			if (mDestPading == 0 && mFirstChild.getTop() == 0
					&& mState == SCROLL_TO_REFRESH) {
				onRefresh();
			} else if (mDestPading == -MAX_LENGHT) {
			}
			invalidate();
			updateView();
			return true;
		} else {
			if (mState != STATE_REFRESH
					|| (mState == STATE_REFRESH && deltaY > 0)) {
				mFirstChild.offsetTopAndBottom((int) -deltaY);
				mListView.offsetTopAndBottom((int) -deltaY);
				mLastChild.offsetTopAndBottom((int) -deltaY);
				mPading = mFirstChild.getTop();
			} else if (mState == STATE_REFRESH && deltaY < 0
					&& mFirstChild.getTop() <= 0) {
				if (mFirstChild.getTop() - deltaY > 0) {
					deltaY = mFirstChild.getTop();
				}
				mFirstChild.offsetTopAndBottom((int) -deltaY);
				mListView.offsetTopAndBottom((int) -deltaY);
				mLastChild.offsetTopAndBottom((int) -deltaY);
				mPading = mFirstChild.getTop();
			}
		}
		if (deltaY > 0 && mFirstChild.getTop() <= -MAX_LENGHT) {
			mPading = -MAX_LENGHT;
			deltaY = -MAX_LENGHT - mFirstChild.getTop();
			mFirstChild.offsetTopAndBottom((int) deltaY);
			mListView.offsetTopAndBottom((int) deltaY);
			mLastChild.offsetTopAndBottom((int) deltaY);
			mPading = mFirstChild.getTop();
			updateView();
			invalidate();
			return false;
		}
		updateView();
		invalidate();
		return true;
	}
	
	private boolean moveUp(float deltaY, boolean auto) {
		if (deltaY < 0 && mLastChild.getTop() == getMeasuredHeight()) {
			mPading = -MAX_LENGHT;
			return false;
		}
		if (auto) {
			mFirstChild.offsetTopAndBottom((int) -deltaY);
			mListView.offsetTopAndBottom((int) -deltaY);
			mLastChild.offsetTopAndBottom((int) -deltaY);
			mPading = mFirstChild.getTop();
			if (mDestPading == 0 && mLastChild.getBottom() == getMeasuredHeight()
					&& mState == SCROLL_TO_LOADMORE) {
				onLoadMore();
			} else if (mDestPading == -MAX_LENGHT) {
			}
			invalidate();
			updateViewByUp();
			return true;
		} else {
			if (mState != STATE_LOADMORE
					|| (mState == STATE_LOADMORE && deltaY > 0)) {
				mFirstChild.offsetTopAndBottom((int) -deltaY);
				mListView.offsetTopAndBottom((int) -deltaY);
				mLastChild.offsetTopAndBottom((int) -deltaY);
				mPading = mFirstChild.getTop();
			} 
			else if (mState == STATE_LOADMORE && deltaY < 0
					&& mFirstChild.getTop() <= 0) {
				if (mFirstChild.getTop() - deltaY > 0) {
					deltaY = mFirstChild.getTop();
				}
				mFirstChild.offsetTopAndBottom((int) -deltaY);
				mListView.offsetTopAndBottom((int) -deltaY);
				mLastChild.offsetTopAndBottom((int) -deltaY);
				mPading = mFirstChild.getTop();
			}
		}
		if (deltaY < 0 && mLastChild.getTop() > getMeasuredHeight()) {
			mPading = -MAX_LENGHT;
			deltaY = -MAX_LENGHT - mFirstChild.getTop();
			mFirstChild.offsetTopAndBottom((int) deltaY);
			mListView.offsetTopAndBottom((int) deltaY);
			mLastChild.offsetTopAndBottom((int) deltaY);
			mPading = mFirstChild.getTop();
			updateViewByUp();
			invalidate();
			return false;
		}
		updateViewByUp();
		invalidate();
		return true;
	}
	
	private void updateView() {
		if(!isShowHead){
			mFirstChild.setVisibility(INVISIBLE);
			return;
		}
		String s = "";
		s = mDate == null ? "" : update_time + mDate;
		if (mState != STATE_REFRESH) {
			if (mFirstChild.getTop() < 0) {
				mUpdateArrow.setVisibility(View.VISIBLE);
				mUpdateProgressBar.setVisibility(View.INVISIBLE);
				mUpdateTitle.setText(pulldowntorefresh + s);

				if (mLastTop >= 0 && mState != SCROLL_TO_CLOSE) {
					mUpdateArrow.startAnimation(mAnimationUp);
				}

			} else if (mFirstChild.getTop() > 0) {
				mUpdateTitle.setText(releasetorefresh + s);
				mUpdateProgressBar.setVisibility(View.INVISIBLE);
				mUpdateArrow.setVisibility(View.VISIBLE);

				if (mLastTop <= 0) {
					mUpdateArrow.startAnimation(mAnimationDown);
				}
			}
		}
		mLastTop = mFirstChild.getTop();
	}
	
	private void updateViewByUp() {
		if (mState != STATE_LOADMORE) {
			if (mLastChild.getBottom() > getMeasuredHeight()) {
				mLoadMoreArrow.setVisibility(View.VISIBLE);
				mLoadMoreProgressBar.setVisibility(View.INVISIBLE);
				mLoadMoreTitle.setText(pulluptoload);
				if (mBottomLastTop <= getMeasuredHeight()-MAX_LENGHT && mState != SCROLL_TO_CLOSE) {
					mLoadMoreArrow.startAnimation(mAnimationUp);
				}

			} else if (mLastChild.getBottom()< getMeasuredHeight()) {
				mLoadMoreTitle.setText(releasetoload);
				mLoadMoreProgressBar.setVisibility(View.INVISIBLE);
				mLoadMoreArrow.setVisibility(View.VISIBLE);
				if (mBottomLastTop >= getMeasuredHeight()-MAX_LENGHT) {
					mLoadMoreArrow.startAnimation(mAnimationDown);
				}
			}
		}
		mBottomLastTop = mLastChild.getTop();
	}

	private boolean release() {
		if (listviewDoScroll) {
			listviewDoScroll = false;
			return true;
		}
		if (mFirstChild.getTop() > 0) {
			scrollToUpdate();
		}else if(mLastChild.getBottom()-getMeasuredHeight() < 0){
			scrollToLoadMore();
		}
		else {
			scrollToClose();
		}
		invalidate();
		return false;
	}
	
	private void scrollToClose() {
		mDestPading = -MAX_LENGHT;
		mFlinger.startUsingDistance(MAX_LENGHT, CLOSEDELAY);
	}
	
	private void scrollLoadMoreToClose() {
		mDestPading = MAX_LENGHT;
		mFlingLoadMore.startUsingDistance(-MAX_LENGHT, CLOSEDELAY);
	}

	private void scrollToUpdate() {
		mState = SCROLL_TO_REFRESH;
		mDestPading = 0;
		mFlinger.startUsingDistance(mFirstChild.getTop(), REFRESHDELAY);
	}
	
	private void scrollToLoadMore() {
		mState = SCROLL_TO_LOADMORE;
		mDestPading = 0;
		mFlingLoadMore.startUsingDistance(mLastChild.getBottom()-getMeasuredHeight(), REFRESHDELAY);
	}
	
	private void onRefresh() {

		mState = STATE_REFRESH;
		String s = "";
		if (mDate == null)
			s = "";
		else
			s = update_time + mDate;
		mUpdateTitle.setText(loading + s);
		mUpdateProgressBar.setVisibility(View.VISIBLE);
		mUpdateArrow.setVisibility(View.INVISIBLE);
		if (mRefreshListioner != null) {
			mRefreshListioner.onRefresh();
		}
	}

	public void onRefreshComplete() {
		onRefreshComplete(null);
	}

	public void onRefreshComplete(String date) {
		mDate = date;
		mState = SCROLL_TO_CLOSE;
		mUpdateArrow.setImageResource(R.drawable.arrow_down);
		scrollToClose();
	}
	
	
	public void onLoadMore() {
		mState = STATE_LOADMORE;
		mLoadMoreTitle.setText(R.string.doing_update);
		mLoadMoreProgressBar.setVisibility(View.VISIBLE);
		mLoadMoreArrow.setVisibility(View.INVISIBLE);
		if(hasMore && mRefreshListioner != null) {
			mRefreshListioner.onLoadMore();
		}
	}

	public void onLoadMoreComplete(String date) {
		mState = SCROLL_TO_CLOSE;
		mLoadMoreArrow.setImageResource(R.drawable.arrow_up);
		scrollLoadMoreToClose();
	}

	public void onLoadMoreComplete() {
		onLoadMoreComplete(null);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		if (isFirstLoading) {
			return false;
		}
		int action;
		float y = e.getY();
		action = e.getAction();
		if (mLongPressing && action != MotionEvent.ACTION_DOWN) {
			return false;
		}
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			mLongPressing = true;
		}
		boolean handled = true;
		handled = mDetector.onTouchEvent(e);
		switch (action) {
		case MotionEvent.ACTION_UP:
			boolean f1 = mListView.getTop() <= e.getY()
					&& e.getY() <= mListView.getBottom();
			if (!handled && mFirstChild.getTop() == -MAX_LENGHT && f1
					|| mState == STATE_REFRESH) {
				super.dispatchTouchEvent(e);
			} else {
				handled = release();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			handled = release();
			super.dispatchTouchEvent(e);
			break;
		case MotionEvent.ACTION_DOWN:
			//
			xDistance = yDistance = 0f;  
            xLast = e.getX();  
            yLast = e.getY(); 
			downEvent = e;
			mLongPressing = false;
			postDelayed(mPendingCheckForLongPress, ViewConfiguration
					.getLongPressTimeout() + 100);
			mPendingRemoved = false;
			super.dispatchTouchEvent(e);
			break;
		case MotionEvent.ACTION_MOVE:
			float curX = e.getX();  
            float curY = e.getY();             
            xDistance += Math.abs(curX - xLast);  
            yDistance += Math.abs(curY - yLast);  
            xLast = curX;  
            yLast = curY;  
            if(xDistance > yDistance){  
            	isHandler = false;
            }else{
            	isHandler = true;
            }
			float deltaY = lastY - y;
			lastY = y;
			if (!mPendingRemoved) {
				removeCallbacks(mPendingCheckForLongPress);
				mPendingRemoved = true;
			}
			if (!handled && mFirstChild.getTop() == -MAX_LENGHT) {
				try {
					return super.dispatchTouchEvent(e);
				} catch (Exception e2) {
					e2.printStackTrace();
					return true;
				}
			} else if (handled && (mListView.getTop() > 0 && deltaY < 0
					|| mListView.getBottom()<getMeasuredHeight() && deltaY > 0)) {
				e.setAction(MotionEvent.ACTION_CANCEL);
				super.dispatchTouchEvent(e);
			}
			break;

		default:
			break;
		}

		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	
	@Override
	public boolean onFling(MotionEvent motionevent, MotionEvent e, float f,
			float f1) {
		return false;
	}
	
	@Override
	protected void onLayout(boolean flag, int i, int j, int k, int l) {
//		Log.v("onLayout","i:"+i+"		j:"+j+"		k:"+k+"		l:"+l);
//		Log.v("onLayout","mPading:"+mPading);
		int top = mPading;
		int w = getMeasuredWidth();
		mFirstChild.layout(0, top, w, top + MAX_LENGHT);
		int h = getMeasuredHeight() + mPading + MAX_LENGHT;
		mListView.layout(0, top + MAX_LENGHT, w, h);
		mLastChild.layout(0, h, w, h + MAX_LENGHT);
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
	}
	
	@Override
	public boolean onScroll(MotionEvent curdown, MotionEvent cur, float deltaX,
			float deltaY) {
		if(!isHandler)
			return false;
		deltaY = (float) ((double) deltaY * SCALE);
		boolean handled = false;
		boolean flag = false;
		boolean lastFlag = false;
		
		if (mListView.getCount() == 0) {
			flag = true;
		} else {
			View c = mListView.getChildAt(0);
			if (mListView.getFirstVisiblePosition() == 0 && c != null
					&& c.getTop() == 0) {
				flag = true;
			}
		}
		if(mListView.getCount()>0){
			View c = mListView.getChildAt(mListView.getChildCount()-1);
			if (mListView.getLastVisiblePosition() == mListView.getCount()-1 && c != null
					&& c.getBottom() == getMeasuredHeight()) {
				lastFlag = true;
			}
		}
		if (deltaY < 0F && flag || getChildAt(0).getTop() > -MAX_LENGHT) { // deltaY
			handled = move(deltaY, false);
		}
		else if(deltaY>0F && lastFlag || getChildAt(2).getTop()<getMeasuredHeight()){
			handled = moveUp(deltaY, false);
		}
		else
			handled = false;
		return handled;
	}
	
	public void onShowPress(MotionEvent motionevent) {
	}

	public boolean onSingleTapUp(MotionEvent motionevent) {
		return false;
	}

	public void setRefreshListioner(OnRefreshListioner RefreshListioner) {
		mRefreshListioner = RefreshListioner;
	}
	
	public void setHorizontalHandler(boolean isShowHead){
		this.isShowHead = isShowHead;
	}
	
	public void setShowHead(boolean isShowHead){
		this.isShowHead = isShowHead;
	}
	
	public void setHasRefresh(boolean hasRefresh) {
		this.hasRefresh = hasRefresh;
		if(hasRefresh){
			mFirstChild.setVisibility(VISIBLE);
		}else{
			mFirstChild.setVisibility(INVISIBLE);
		}
	}
	
	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
		if(hasMore){
			getChildAt(2).setVisibility(View.VISIBLE);
		}else{
			getChildAt(2).setVisibility(View.GONE);
		}
	}
	
	public void setHeadBgColor(int color) {
		if(mFirstChild != null) {
			mFirstChild.setBackgroundColor(color);
		}
	}
	
	public void setFooterbgColor(int color) {
		if(mLastChild != null) {
			mLastChild.setBackgroundColor(color);
		}
	}
	
	public void setHeadTitleColor(int color) {
		if(mUpdateTitle != null) {
			mUpdateTitle.setTextColor(color);
		}
	}
	
	public void setFooterTitleColor(int color) {
		if(mLoadMoreTitle != null) {
			mLoadMoreTitle.setTextColor(color);
		}
	}
	
}
