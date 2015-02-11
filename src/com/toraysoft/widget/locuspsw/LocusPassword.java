package com.toraysoft.widget.locuspsw;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LocusPassword extends View {

	LocusTips mLocusTips;
	List<LocusPoint> sPoints;
	LocusPoint[][] mPoints;
	LocusLine mLocusLine;
	int pointPadding;
	int pswMinLength = 4;
	float lt = 0f;
	float tt = 0f;

	Paint mPaint;

	boolean isTouch = true;
	boolean isChecking = false;
	boolean isMovingNoPoint = false;

	float moveingX, moveingY = 0f;

	static final int CLEAR_TIME = 0;

	OnLocusDrawListener mOnLocusDrawListener;
	OnLocusCompleteListener mLocusCompleteListener;
	
	public final static int ERRNO_TOO_SHORT = 5001;

	public LocusPassword(Context context) {
		super(context);
		init();
	}

	public LocusPassword(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

//	public LocusPassword(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		init();
//	}

	void init() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		sPoints = new ArrayList<LocusPoint>();
	}

	public void setPoints(LocusPoint[][] mPoints, LocusLine line,
			int pointPadding) {
		this.mPoints = mPoints;
		this.mLocusLine = line;
		this.pointPadding = pointPadding;
		this.postInvalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		if(mPoints==null)
			return;
		if (sPoints.size() > 0) {
			LocusPoint tp = sPoints.get(0);
			for (int i = 1; i < sPoints.size(); i++) {
				LocusPoint p = sPoints.get(i);
				drawLine(canvas, tp, p);
				tp = p;
			}
			if (isMovingNoPoint) {
				drawLine(canvas, tp, moveingX-tp.w/2, moveingY-tp.h/2);
			}
		}

		int w = this.getWidth();
		int h = this.getHeight();
		if(mPoints.length>0 && lt==0 && tt==0){
			int l = 0;
			for (int j = 0; j < mPoints[0].length; j++) {
				l += mPoints[0][j].w;
				if (j < mPoints[0].length - 1)
					l += pointPadding;
			}
			lt = ((float) w - l) / 2;
			int t = 0;
			for (int j = 0; j < mPoints.length; j++) {
				t += mPoints[j][0].h;
				if (j < mPoints[j].length - 1)
					t += pointPadding;
			}
			tt = ((float) h - t) / 2;
			if(mOnLocusDrawListener!=null)
				mOnLocusDrawListener.onLocusTop((int)(tt-pointPadding));
		}
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				LocusPoint p = mPoints[i][j];
				canvas.save();
				p.x = (p.w + pointPadding) * j + lt;
				p.y = (p.h + pointPadding) * i+tt;
				canvas.translate(p.x, p.y);
				if (p.state == LocusPoint.STATE_CHECK) {
					p.mDrawableClick.draw(canvas);
				} else {
					p.mDrawableDefault.draw(canvas);
				}
				canvas.restore();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isTouch) {
			return false;
		}
		isMovingNoPoint = false;
		boolean isFinish = false;
		float ex = event.getX();
		float ey = event.getY();
		LocusPoint p = null;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			reset();
			checkSelectPoint(ex, ey);
			p = checkSelectPoint(ex, ey);
			if (p != null) {
				isChecking = true;
				if (mLocusCompleteListener != null) {
					mLocusCompleteListener.onLocusStart();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (isChecking) {
				p = checkSelectPoint(ex, ey);
				if (p == null) {
					isMovingNoPoint = true;
					moveingX = ex;
					moveingY = ey;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
//			p = checkSelectPoint(ex, ey);
			isChecking = false;
			isFinish = true;
			break;
		}
		if (!isFinish && isChecking && p != null) {
			int rk = crossPoint(p);
			if (rk == 2) {
				isMovingNoPoint = true;
				moveingX = ex;
				moveingY = ey;
			} else if (rk == 0) {
				p.state = LocusPoint.STATE_CHECK;
				addPoint(p);
			}
		}
		if (isFinish) {
//			if (this.sPoints.size() == 1) {
//				this.reset();
//			} else 
			if (this.sPoints.size() < pswMinLength
					&& this.sPoints.size() > 0) {
				if (mLocusCompleteListener != null) {
					mLocusCompleteListener.onLocusError(ERRNO_TOO_SHORT);
				}
			} else if (mLocusCompleteListener != null) {
				if (this.sPoints.size() >= pswMinLength) {
					// isTouch = false;
					mLocusCompleteListener.onLocusComplete(toPointString());
				}

			}
			this.reset();
		}
		this.postInvalidate();
		return true;
	}

	/**
	 * 画两点的连接
	 * 
	 * @param canvas
	 * @param a
	 * @param b
	 */
	private void drawLine(Canvas canvas, LocusPoint a, LocusPoint b) {
		drawLine(canvas, a, b.x, b.y);
	}

	private void drawLine(Canvas canvas, LocusPoint a, float x, float y) {
		float ax = a.x;
		float ay = a.y;
		float bx = x;
		float by = y;
		canvas.save();
		float ah = (float) MathTools.distance(ax, ay, bx, by);
		float degrees = getDegrees(a, x, y);
		float tw = ((float) mLocusLine.h) / 2
				* (float) Math.sin(degrees * Math.PI / 180);
		float th = ((float) mLocusLine.h) / 2
				* (float) Math.cos(degrees * Math.PI / 180);
		canvas.translate(a.x + a.w / 2 + tw, a.y + a.h / 2 - th);
		canvas.rotate(degrees);
		mLocusLine.mDrawable.setBounds(0, 0, (int) ah, mLocusLine.h);
		mLocusLine.mDrawable.draw(canvas);
		canvas.restore();
	}

	public float getDegrees(LocusPoint a, LocusPoint b) {
		return getDegrees(a, b.x, b.y);
	}

	public float getDegrees(LocusPoint a, float x, float y) {
		float ax = a.x;// a.index % 3;
		float ay = a.y;// a.index / 3;
		float bx = x;// b.index % 3;
		float by = y;// b.index / 3;
		float degrees = 0;
		if (bx == ax) // y轴相等 90度或270
		{
			if (by > ay) // 在y轴的下边 90
			{
				degrees = 90;
			} else if (by < ay) // 在y轴的上边 270
			{
				degrees = 270;
			}
		} else if (by == ay) // y轴相等 0度或180
		{
			if (bx > ax) // 在y轴的下边 90
			{
				degrees = 0;
			} else if (bx < ax) // 在y轴的上边 270
			{
				degrees = 180;
			}
		} else {
			if (bx > ax) // 在y轴的右边 270~90
			{
				if (by > ay) // 在y轴的下边 0 - 90
				{
					degrees = 0;
					degrees = degrees
							+ switchDegrees(Math.abs(by - ay),
									Math.abs(bx - ax));
				} else if (by < ay) // 在y轴的上边 270~0
				{
					degrees = 360;
					degrees = degrees
							- switchDegrees(Math.abs(by - ay),
									Math.abs(bx - ax));
				}

			} else if (bx < ax) // 在y轴的左边 90~270
			{
				if (by > ay) // 在y轴的下边 180 ~ 270
				{
					degrees = 90;
					degrees = degrees
							+ switchDegrees(Math.abs(bx - ax),
									Math.abs(by - ay));
				} else if (by < ay) // 在y轴的上边 90 ~ 180
				{
					degrees = 270;
					degrees = degrees
							- switchDegrees(Math.abs(bx - ax),
									Math.abs(by - ay));
				}

			}

		}
		return degrees;
	}

	/**
	 * 1=30度 2=45度 4=60度
	 * 
	 * @param x
	 *            ,y
	 * @return
	 */
	private float switchDegrees(float x, float y) {
		return (float) MathTools.pointTotoDegrees(x, y);
	}

	/**
	 * 取得数组下标
	 * 
	 * @param index
	 * @return
	 */
	public int[] getArrayIndex(int index) {
		int[] ai = new int[2];
		ai[0] = index / 3;
		ai[1] = index % 3;
		return ai;
	}

	/**
	 * 
	 * 检查
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private LocusPoint checkSelectPoint(float x, float y) {
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				LocusPoint p = mPoints[i][j];
				if (x > p.x && x < (p.x + p.w) && y > p.y && y < (p.y + p.h) && !sPoints.contains(p)) {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * 重置
	 */
	private void reset() {
		for (LocusPoint p : sPoints) {
			p.state = LocusPoint.STATE_NORMAL;
		}
		sPoints.clear();
		this.isTouch = true;
	}

	/**
	 * 判断点是否有交叉 返回 0,新点 ,1 与上一点重叠 2,与非最后一点重叠
	 * 
	 * @param p
	 * @return
	 */
	private int crossPoint(LocusPoint p) {
		// 重叠的不最后一个则 reset
		if (sPoints.contains(p)) {
			if (sPoints.size() > 2) {
				// 与非最后一点重叠
				if (sPoints.get(sPoints.size() - 1).equals(p)) {
					return 2;
				}
			}
			return 1; // 与最后一点重叠
		} else {
			return 0; // 新点
		}
	}

	/**
	 * 添加一个点
	 * 
	 * @param point
	 */
	private void addPoint(LocusPoint point) {
		this.sPoints.add(point);
	}

	/**
	 * 转换为String
	 * 
	 * @param points
	 * @return
	 */
	private String toPointString() {
		if (sPoints.size() >= pswMinLength) {
			StringBuffer sf = new StringBuffer();
			for (LocusPoint p : sPoints) {
				sf.append(",");
				sf.append(p.key);
			}
			return sf.deleteCharAt(0).toString();
		} else {
			return "";
		}
	}
	
	public void setTouchEnable(boolean isTouch) {
		this.isTouch = isTouch;
	}
	
	public void setOnLocusDrawListener(OnLocusDrawListener l) {
		this.mOnLocusDrawListener = l;
	}

	/**
	 * @param mCompleteListener
	 */
	public void setOnLocusCompleteListener(OnLocusCompleteListener mLocusCompleteListener) {
		this.mLocusCompleteListener = mLocusCompleteListener;
	}
	
	public interface OnLocusDrawListener{
		public void onLocusTop(int top);
	}

	/**
	 * 轨迹球画完成事件
	 * 
	 */
	public interface OnLocusCompleteListener {
		/**
		 * 画完了
		 * 
		 * @param str
		 */
		public void onLocusComplete(String password);

		public void onLocusError(int errno);
		
		public void onLocusStart();
		
	}
	
	static class MathTools {
		/**
		 * 两点间的距离
		 * 
		 * @param x1
		 * @param y1
		 * @param x2
		 * @param y2
		 * @return
		 */
		public static double distance(double x1, double y1, double x2, double y2) {
			return Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2)
					+ Math.abs(y1 - y2) * Math.abs(y1 - y2));
		}

		/**
		 * 计算点a(x,y)的角度
		 * 
		 * @param x
		 * @param y
		 * @return
		 */
		public static double pointTotoDegrees(double x, double y) {
			return Math.toDegrees(Math.atan2(x, y));
		}
	}

}
