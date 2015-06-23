package com.toraysoft.widget.galleryhorizontalscrollview;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.toraysoft.widget.galleryhorizontalscrollview.CustomHorizontalScrollView.OnCustomScrollListner;

public class CustomImageGallery extends LinearLayout implements
		OnCustomScrollListner, OnClickListener {

	private Context mContext;
	private CustomHorizontalScrollView mCustomHorizontalScrollView;
	private PageIndicatorView mPageIndicatorView;
	private LinearLayout mLinearLayout;
	private int itemWidth, itemHeight, count;
	private int NORMAL_MARGIN = 20;
	private int SELECT_MARGIN = 5;
	private int posotion = -1;
	private OnCustomImageGalleryClickListener mOnCustomImageGalleryClickListener;
	private List<View> views;

	public CustomImageGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public CustomImageGallery(Context context) {
		super(context);
		this.mContext = context;
	}

	public void initDatas(int itemWidth, int itemHeight, List<View> views) {
		this.setOrientation(LinearLayout.VERTICAL);
		this.itemWidth = itemWidth;
		this.itemHeight = itemHeight;
		this.views = views;
		this.count = views.size() + 2;
		init();
	}

	private void init() {
		removeAllViews();
		mCustomHorizontalScrollView = new CustomHorizontalScrollView(mContext);
		mPageIndicatorView = new PageIndicatorView(mContext);
		mLinearLayout = new LinearLayout(mContext);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		mCustomHorizontalScrollView.setLayoutParams(lp);
		mPageIndicatorView.setLayoutParams(lp);
		mLinearLayout.setLayoutParams(lp);
		initCustomHorizontalScrollView();
		initPageIndicatorView();
		this.addView(mCustomHorizontalScrollView);
		this.addView(mPageIndicatorView);
	}

	private void initCustomHorizontalScrollView() {
		mCustomHorizontalScrollView.setItemWidth(itemWidth);
		mCustomHorizontalScrollView.setCount(count);
		mCustomHorizontalScrollView.setOnCustomScrollListner(this);
		mCustomHorizontalScrollView.setHorizontalScrollBarEnabled(false);
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		for (int i = 0; i < count; i++) {
			if (i == 0 || i == count - 1) {
				mLinearLayout.addView(getItemView(true, i));
			} else {
				mLinearLayout.addView(getItemView(false, i));
			}
		}
		mCustomHorizontalScrollView.addView(mLinearLayout);
	}

	private RelativeLayout getItemView(boolean empty, int position) {
		RelativeLayout rl = new RelativeLayout(mContext);
		View view = new View(mContext);
		if (position == 0 || position == count - 1) {
			LayoutParams lp = new LayoutParams(itemWidth / 2, itemHeight);
			rl.setLayoutParams(lp);
		} else {
			LayoutParams lp = new LayoutParams(itemWidth, itemHeight);
			rl.setLayoutParams(lp);
			int loc = position - 1;
			if (loc < 0) {
				loc = 0;
			}
			view = views.get(loc);
		}
		if (position == 1) {
			view.setLayoutParams(getViewBounds(SELECT_MARGIN));
		} else {
			view.setLayoutParams(getViewBounds(NORMAL_MARGIN));
		}
		if (!empty) {
			view.setOnClickListener(this);
		}
		view.setTag(position);
		rl.addView(view);
		return rl;
	}

	private void setViewSize(final int pos) {
		mCustomHorizontalScrollView.post(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < count; i++) {
					View iv = (View) mLinearLayout.findViewWithTag(i);
					if (i == pos) {
						iv.setLayoutParams(getViewBounds(SELECT_MARGIN));
					} else {
						iv.setLayoutParams(getViewBounds(NORMAL_MARGIN));
					}
				}
			}
		});
	}

	private RelativeLayout.LayoutParams getViewBounds(int length) {
		RelativeLayout.LayoutParams lpv = new RelativeLayout.LayoutParams(
				new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
		lpv.leftMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, length, getResources()
						.getDisplayMetrics());
		lpv.rightMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, length, getResources()
						.getDisplayMetrics());
		lpv.topMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, length, getResources()
						.getDisplayMetrics());
		lpv.bottomMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, length, getResources()
						.getDisplayMetrics());
		return lpv;
	}

	@Override
	public void onCustomScrollPos(int pos) {
		if (posotion != pos) {
			setViewSize(pos);
			mPageIndicatorView.setCurrentPage(false, pos - 1);
			posotion = pos;
		}
	}

	private void initPageIndicatorView() {
		mPageIndicatorView.init(count - 2);
		mPageIndicatorView.setCurrentPage(true, 0);
	}

	public interface OnCustomImageGalleryClickListener {
		void onCustomImageGalleryClick(CustomImageGallery v, int pos);
	}

	public void setOnCustomImageGalleryClickListener(
			OnCustomImageGalleryClickListener l) {
		mOnCustomImageGalleryClickListener = l;
	}

	@Override
	public void onClick(View v) {
		if (v.getTag() != null && mOnCustomImageGalleryClickListener != null) {
			int pos = (Integer) v.getTag();
			if (pos > 0) {
				mOnCustomImageGalleryClickListener.onCustomImageGalleryClick(this,
						pos - 1);
			}
		}
	}

}
