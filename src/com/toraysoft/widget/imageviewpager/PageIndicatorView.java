package com.toraysoft.widget.imageviewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class PageIndicatorView extends View {

	private int mCurrentPage = -1;
	private int mTotalPage = 0;
	private int screenWidth = 0;
	private int screenWidthDefault = 480;

	private int colorDefault = Color.parseColor("#e2e2e2");
	private int colorSelect = Color.parseColor("#999999");

	public PageIndicatorView(Context context) {
		super(context);
	}

	public PageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setTotalPage(int nPageNum) {
		mTotalPage = nPageNum;
		if (mCurrentPage >= mTotalPage)
			mCurrentPage = mTotalPage - 1;
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setCurrentPage(boolean redraw, int nPageIndex) {
		if (nPageIndex < 0 || nPageIndex >= mTotalPage)
			return;

		if (redraw) {
			mCurrentPage = nPageIndex;
			this.invalidate();
		} else {
			if (mCurrentPage != nPageIndex) {
				mCurrentPage = nPageIndex;
				this.invalidate();
			}
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL);

		Rect r = new Rect();
		this.getDrawingRect(r);

		int iconWidth = getScaleLength(10);
		int iconHeight = getScaleLength(10);
		int space = getScaleLength(10);

		int x = (r.width() - (iconWidth * mTotalPage + space * (mTotalPage - 1))) / 2;
		// int y = r.height() - (r.height() - iconHeight) / 6;
		int y = r.height() - iconHeight * 2;

		for (int i = 0; i < mTotalPage; i++) {

			paint.setColor(colorDefault);

			if (i == mCurrentPage) {
				paint.setColor(colorSelect);
			}

			Rect r1 = new Rect();
			r1.left = x;
			r1.top = y;
			r1.right = x + iconWidth;
			r1.bottom = y + iconHeight;
			float radius = iconWidth / 2;

			canvas.drawColor(Color.TRANSPARENT);
			canvas.drawCircle(x, y, radius, paint);

			x += iconWidth + space;

		}

	}

	public int getScreenWidth() {
		if (screenWidth == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			dm = getContext().getResources().getDisplayMetrics();
			screenWidth = dm.widthPixels;
		}
		return screenWidth;
	}

	public int getScaleLength(int length) {
		return length * getScreenWidth() / screenWidthDefault;
	}

}
