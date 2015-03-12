package com.toraysoft.widget.recyclerviewpager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * RecyclerViewPager
 * 
 * @author Green
 * @since 15/1/18$ 上午2:06
 */
public class RecyclerViewPager extends RecyclerView {

	int mDisplayPadding;
	RecyclerViewPagerAdapter mViewPagerAdapter;
	OnPageChangeListener mOnPageChangeListener;
	OnRecyclerDataSetChangedListener mOnRecyclerDataSetChangedListener;
	int currentPage = 0;
	boolean isCenterScreen = false;

	public RecyclerViewPager(Context context) {
		super(context);
		init();
	}

	public RecyclerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	void init() {
		setLayoutManager(new LinearLayoutManager(getContext(),
				OrientationHelper.HORIZONTAL, false));
	}

	@Override
	public void swapAdapter(Adapter adapter,
			boolean removeAndRecycleExistingViews) {
		super.swapAdapter(adapter, removeAndRecycleExistingViews);
	}

	@Override
	public void setAdapter(Adapter adapter) {
		mViewPagerAdapter = new RecyclerViewPagerAdapter(this, adapter);
		super.setAdapter(mViewPagerAdapter);
		setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (getLayoutManager() != null
						&& getLayoutManager() instanceof LinearLayoutManager) {
					LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) getLayoutManager();
					int position = mLinearLayoutManager
							.findFirstCompletelyVisibleItemPosition();
					if (position >= 0 && position != currentPage) {
						currentPage = position;
						if (mOnPageChangeListener != null) {
							mOnPageChangeListener.onPageChange(position);
						}
					}
				}
			}
		});
	}

	@Override
	public Adapter getAdapter() {
		if (mViewPagerAdapter != null) {
			return mViewPagerAdapter.mAdapter;
		}
		return null;
	}

	public RecyclerViewPagerAdapter getWrapperAdapter() {
		return mViewPagerAdapter;
	}

	/**
	 * get the padding from center page to RecyclerViewEx's edge
	 * 
	 * @return
	 */
	public int getDisplayPadding() {
		return mDisplayPadding;
	}

	/**
	 * set the padding from center page to RecyclerViewEx's edge
	 * 
	 * @param displayPadding
	 */
	public void setDisplayPadding(int displayPadding) {
		mDisplayPadding = displayPadding;
	}

	public interface OnPageChangeListener {
		void onPageChange(int position);
	}
	
	public interface OnRecyclerDataSetChangedListener {
		void onDataSetChanged();
	}

	public void setOnPageChangeListener(OnPageChangeListener l) {
		this.mOnPageChangeListener = l;
	}
	
	public void setOnRecyclerDataSetChangedListener(OnRecyclerDataSetChangedListener l) {
		this.mOnRecyclerDataSetChangedListener = l;
	}
	
	public void notifyRecyclerDataSetChanged() {
		if(getAdapter() != null) {
			getAdapter().notifyDataSetChanged();
			if(mOnRecyclerDataSetChangedListener != null) {
				mOnRecyclerDataSetChangedListener.onDataSetChanged();
			}
		}
	}
	
	public void setIsCenterScreen(boolean value) {
		isCenterScreen = value;
	}
	
	public boolean isCenterScreen() {
		return isCenterScreen;
	}

}
