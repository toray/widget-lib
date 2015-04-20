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

	private int iconWidth, iconHeight, space;
	private int OFFSET = 2, DOT_WIDTH = 8;

	public PageIndicatorView(Context context) {
		super(context);
		init();
	}

	public PageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		iconWidth = getScaleLength(DOT_WIDTH);
		iconHeight = getScaleLength(DOT_WIDTH);
		space = getScaleLength(DOT_WIDTH);
	}

	public void setColor(int select, int normal) {
		colorSelect = select;
		colorDefault = normal;
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
		
		int x = (r.width() - (iconWidth * mTotalPage + space * (mTotalPage - 1))) / 2;
//		System.out.println("---------x:" + x);
		int y = iconHeight / 2 + OFFSET;
//		System.out.println("---------y:" + y);

		for (int i = 0; i < mTotalPage; i++) {
			paint.setColor(colorDefault);

			if (i == mCurrentPage) {
				paint.setColor(colorSelect);
			}

			Rect r1 = new Rect();
			r1.left = x;
//			System.out.println("----------r1.left:"+r1.left);
			r1.top = y;
//			System.out.println("----------r1.top:"+r1.top);
			r1.right = x + iconWidth;
//			System.out.println("----------r1.right:"+r1.right);
			r1.bottom = y + iconHeight;
//			System.out.println("----------r1.bottom:"+r1.bottom);
			float radius = iconWidth / 2;
//			System.out.println("----------radius:"+radius);

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

	public int getIndicatorHeight() {
		return iconHeight + OFFSET * 2;
	}

}
