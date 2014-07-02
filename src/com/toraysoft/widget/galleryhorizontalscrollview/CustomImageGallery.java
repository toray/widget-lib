package com.toraysoft.widget.galleryhorizontalscrollview;

import com.toraysoft.widget.galleryhorizontalscrollview.CustomHorizontalScrollView.OnCustomScrollListner;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CustomImageGallery extends LinearLayout implements
		OnCustomScrollListner {

	private Context mContext;
	private CustomHorizontalScrollView mCustomHorizontalScrollView;
	private PageIndicatorView mPageIndicatorView;
	private LinearLayout mLinearLayout;
	private int itemWidth, itemHeight, count, defaultImg;
	private int NORMAL_MARGIN = 20;
	private int SELECT_MARGIN = 5;
	private int posotion = -1;

	public CustomImageGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public CustomImageGallery(Context context) {
		super(context);
		this.mContext = context;
	}

	public void initDatas(int itemWidth, int itemHeight, int count,
			int defaultImg) {
		this.setOrientation(LinearLayout.VERTICAL);
		this.itemWidth = itemWidth;
		this.itemHeight = itemHeight;
		this.count = count;
		this.defaultImg = defaultImg;
		init();
	}

	private void init() {
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
		if (position == 0 || position == count - 1) {
			LayoutParams lp = new LayoutParams(new LayoutParams(itemWidth / 2,
					itemHeight));
			rl.setLayoutParams(lp);
		} else {
			LayoutParams lp = new LayoutParams(new LayoutParams(itemWidth,
					itemHeight));
			rl.setLayoutParams(lp);
		}
		ImageView iv = new ImageView(mContext);
		if (position == 1) {
			iv.setLayoutParams(getImageViewBounds(SELECT_MARGIN));
		} else {
			iv.setLayoutParams(getImageViewBounds(NORMAL_MARGIN));
		}
		iv.setScaleType(ScaleType.FIT_XY);
		if (!empty) {
			iv.setImageResource(defaultImg);
		}
		iv.setTag(position);
		rl.addView(iv);
		return rl;
	}

	private void setImageViewSize(final int pos) {
		mCustomHorizontalScrollView.post(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 8; i++) {
					ImageView iv = (ImageView) mLinearLayout.findViewWithTag(i);
					if (i == pos) {
						iv.setLayoutParams(getImageViewBounds(SELECT_MARGIN));
					} else {
						iv.setLayoutParams(getImageViewBounds(NORMAL_MARGIN));
					}
				}
			}
		});
	}

	private RelativeLayout.LayoutParams getImageViewBounds(int length) {
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
			setImageViewSize(pos);
			mPageIndicatorView.setCurrentPage(false, pos - 1);
			posotion = pos;
		}
	}

	private void initPageIndicatorView() {
		mPageIndicatorView.init(count - 2);
		mPageIndicatorView.setCurrentPage(true, 0);
	}

}
