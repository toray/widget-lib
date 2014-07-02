package com.toraysoft.widget.navigation;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NavigationBar extends HorizontalScrollView implements
		OnClickListener {

	private OnScrollChangeListener mOnScrollChangeListener;
	private LinearLayout mRootView;
	private int currentPos = 0;
	private OnNavigationItemClickListener onNavigationItemClickListener;
	private int marginLeft;
	private int barWidth;

	private int colorDefault = Color.parseColor("#f1f1f1");
	private int colorSelect = Color.parseColor("#d9d9d9");

	public NavigationBar(Context context) {
		super(context);
		init();
	}

	public NavigationBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NavigationBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mRootView = new LinearLayout(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mRootView.setLayoutParams(params);
		mRootView.setGravity(Gravity.CENTER_VERTICAL);
		mRootView.setOrientation(LinearLayout.HORIZONTAL);
		addView(mRootView);
	}

	public void clear() {

	}

	public void addItem(NavigationItem item) {
		View view = LayoutInflater.from(getContext()).inflate(
				item.getLayoutResID(), null);
		ImageView iv_navigation = (ImageView) ((ViewGroup) view).getChildAt(0);
		// RelativeLayout rl_bg = (RelativeLayout) view
		// .findViewById(R.id.rl_navigation);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				item.getWidth(), item.getHeight());
		view.setLayoutParams(params);
		android.widget.RelativeLayout.LayoutParams params2 = new android.widget.RelativeLayout.LayoutParams(
				item.getIcon_width(), item.getIcon_height());
		iv_navigation.setLayoutParams(params2);

		view.setTag(item.getTag());
		item.setImageIcon(iv_navigation);
		Bitmap bitmap = item.getIcon();
		iv_navigation.setImageBitmap(bitmap);
		view.setBackgroundColor(item.getBgColor());
		view.setOnClickListener(this);

		mRootView.addView(view);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		this.marginLeft = l;
		if (mOnScrollChangeListener != null) {
			mOnScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
		}
	}

	public void setOnScrollChangeListener(
			OnScrollChangeListener mOnScrollChangeListener) {
		this.mOnScrollChangeListener = mOnScrollChangeListener;
	}

	public interface OnScrollChangeListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	public void setOnNavigationItemClickListener(OnNavigationItemClickListener l) {
		this.onNavigationItemClickListener = l;
	}

	// public int getBgCurrentPos() {
	// return currentPos;
	// }
	//
	// public void setBgCurrentPos(int currentPos) {
	// this.currentPos = currentPos;
	// }

	private void changeItemBgColor(int position) {
		if (position != currentPos) {
			if (mRootView.getChildAt(currentPos) != null) {
				mRootView.getChildAt(currentPos).setBackgroundColor(
						colorDefault);
			}
			if (mRootView.getChildAt(position) != null) {
				mRootView.getChildAt(position).setBackgroundColor(colorSelect);
			}
			currentPos = position;
		}
		turnItemLocation(position);
	}

	private void turnItemLocation(int position) {
		final View child = mRootView.getChildAt(position);
		if (child != null) {
			if (marginLeft - child.getLeft() < child.getWidth()
					&& marginLeft - child.getLeft() > 0) {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						scrollTo(child.getLeft(), 0);
						return;
					}
				});
			}
			if (child.getWidth() * (position + 1) - (marginLeft + barWidth) < child
					.getWidth()
					&& child.getWidth() * (position + 1)
							- (marginLeft + barWidth) > 0) {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						scrollTo(child.getLeft() - barWidth + child.getWidth(),
								0);
						return;
					}
				});
			}
		}
	}

	public void turnItemFarLocation(int position) {
		if (position != currentPos) {
			if (mRootView.getChildAt(currentPos) != null) {
				mRootView.getChildAt(currentPos).setBackgroundColor(
						colorDefault);
			}
			if (mRootView.getChildAt(position) != null) {
				mRootView.getChildAt(position).setBackgroundColor(colorSelect);
			}
			currentPos = position;
		}
		final View child = mRootView.getChildAt(position);
		if (child != null) {
			new Handler().post(new Runnable() {

				@Override
				public void run() {
					scrollTo(child.getLeft(), 0);
					return;
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		changeItemBgColor((Integer) v.getTag());
		if (onNavigationItemClickListener != null) {
			onNavigationItemClickListener.onNavigationItemClick(v);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		this.barWidth = getMeasuredWidth();
	}

	private BaseAdapter mAdapter;

	DataSetObserver mDataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			layoutChildren();
		}
	};

	public BaseAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(BaseAdapter mAdapter) {
		this.mAdapter = mAdapter;
		mAdapter.registerDataSetObserver(mDataSetObserver);
		layoutChildren();
	}

	public synchronized void layoutChildren() {
		mRootView.removeAllViews();
		if (mAdapter == null || mAdapter.getCount() == 0)
			return;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			View view = mAdapter.getView(i, null, this);
			if (currentPos == i)
				view.setBackgroundColor(colorSelect);
			else
				view.setBackgroundColor(colorDefault);
			view.setTag(i);
			view.setOnClickListener(this);
			mRootView.addView(view);
		}
	}

	public interface OnNavigationItemClickListener {
		public void onNavigationItemClick(View v);
	}

}
