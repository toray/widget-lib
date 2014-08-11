package com.toraysoft.widget.locuspsw;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.toraysoft.widget.R;

public class LocusPassWordView extends View {
	float w,h,r,moveingX,moveingY = 0f;
	boolean isInitialize,checking,movingNoPoint = false;
	Paint mPaint;
	// 选中的点
	List<Point> sPoints;
	
	Point[][] mPoints;
	
	Bitmap locus_round_original;//圆点初始状态时的图片
	Bitmap locus_round_click;//圆点点击时的图片
	Bitmap locus_round_click_error;//出错时圆点的图片 
	Bitmap locus_line;//正常状态下线的图片
	Bitmap locus_line_semicircle;
	Bitmap locus_line_semicircle_error;
	Bitmap locus_arrow;//线的移动方向
	Bitmap locus_line_error;//错误状态下的线的图片
    boolean isTouch = true; // 是否可操作
    Matrix mMatrix;
    int lineAlpha = 50;//连线的透明度
	int pswMinLength = 4;//密码最小长度
	static final int CLEAR_TIME = 0;
	
	int[] imgIds;
	Timer timer;
	TimerTask task;
	OnCompleteListener mCompleteListener;
	
	public LocusPassWordView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LocusPassWordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		 if (attrs != null) {
			 TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
	                    R.styleable.LocusPassWordView, 0, 0);
			 imgIds = new int[]{
				typedArray.getResourceId(R.styleable.LocusPassWordView_locusOriginal, 0),
				typedArray.getResourceId(R.styleable.LocusPassWordView_locusClick, 0),
				typedArray.getResourceId(R.styleable.LocusPassWordView_locusClickError, 0),
				typedArray.getResourceId(R.styleable.LocusPassWordView_locusLine, 0),
				typedArray.getResourceId(R.styleable.LocusPassWordView_locusLineError, 0),
				typedArray.getResourceId(R.styleable.LocusPassWordView_locusLineSemicircle, 0),
				typedArray.getResourceId(R.styleable.LocusPassWordView_locusLineSemicircleError, 0),
				typedArray.getResourceId(R.styleable.LocusPassWordView_locusArraw, 0),
				};
		 }
	}

	public LocusPassWordView(Context context,int[] imgIds) {
		super(context);
		this.imgIds = imgIds;
	}
	
	
	void init(){
		mPaint =  new Paint(Paint.ANTI_ALIAS_FLAG);
		sPoints = new ArrayList<Point>();
		mPoints = new Point[3][3];
		mMatrix = new Matrix();
		timer = new Timer();
		
		w = this.getWidth();
		h = this.getHeight();
		float x = 0;
		float y = 0;

		// 以最小的为准
		// 纵屏
		if (w > h) {
			x = (w - h) / 2;
			w = h;
		}
		// 横屏
		else {
			y = (h - w) / 2;
			h = w;
		}
		
		locus_round_original = BitmapFactory.decodeResource(getResources(), imgIds[0]);
		locus_round_click = BitmapFactory.decodeResource(getResources(), imgIds[1]);
		locus_round_click_error = BitmapFactory.decodeResource(getResources(), imgIds[2]);
		locus_line = BitmapFactory.decodeResource(getResources(), imgIds[3]);
		locus_line_error = BitmapFactory.decodeResource(getResources(), imgIds[4]);
		locus_line_semicircle = BitmapFactory.decodeResource(getResources(), imgIds[5]);
		locus_line_semicircle_error = BitmapFactory.decodeResource(getResources(), imgIds[6]);
		locus_arrow = BitmapFactory.decodeResource(getResources(), imgIds[7]);
		
		// 计算圆圈图片的大小
		float canvasMinW = w;
		if (w > h) {
			canvasMinW = h;
		}
		float roundMinW = canvasMinW / 8.0f * 2;
		float roundW = roundMinW / 2.f;
		//
		float deviation = canvasMinW % (8 * 2) / 2;
		x += deviation;
		x += deviation;
		
		if (locus_round_original.getWidth() > roundMinW) {
			float sf = roundMinW * 1.0f / locus_round_original.getWidth(); // 取得缩放比例，将所有的图片进行缩放
			locus_round_original = BitmapTools.zoom(locus_round_original, sf);
			locus_round_click = BitmapTools.zoom(locus_round_click, sf);
			locus_round_click_error = BitmapTools.zoom(locus_round_click_error,
					sf);

			locus_line = BitmapTools.zoom(locus_line, sf);
			locus_line_semicircle = BitmapTools.zoom(locus_line_semicircle, sf);

			locus_line_error = BitmapTools.zoom(locus_line_error, sf);
			locus_line_semicircle_error = BitmapTools.zoom(
					locus_line_semicircle_error, sf);
			locus_arrow = BitmapTools.zoom(locus_arrow, sf);
			roundW = locus_round_original.getWidth() / 2;
		}
		
		mPoints[0][0] = new Point(x + 0 + roundW, y + 0 + roundW);
		mPoints[0][1] = new Point(x + w / 2, y + 0 + roundW);
		mPoints[0][2] = new Point(x + w - roundW, y + 0 + roundW);
		mPoints[1][0] = new Point(x + 0 + roundW, y + h / 2);
		mPoints[1][1] = new Point(x + w / 2, y + h / 2);
		mPoints[1][2] = new Point(x + w - roundW, y + h / 2);
		mPoints[2][0] = new Point(x + 0 + roundW, y + h - roundW);
		mPoints[2][1] = new Point(x + w / 2, y + h - roundW);
		mPoints[2][2] = new Point(x + w - roundW, y + h - roundW);
		int k = 0;
		for (Point[] ps : mPoints) {
			for (Point p : ps) {
				p.index = k;
				k++;
			}
		}
		r = locus_round_original.getHeight() / 2;// roundW;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(!isInitialize){
			isInitialize = true;
			init();
		}
		drawToCanvas(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 不可操作
		if (!isTouch) {
			return false;
		}

		movingNoPoint = false;

		float ex = event.getX();
		float ey = event.getY();
		boolean isFinish = false;
		boolean redraw = false;
		Point p = null;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 点下
			// 如果正在清除密码,则取消
			if (task != null) {
				task.cancel();
				task = null;
				Log.d("task", "touch cancel()");
			}
			// 删除之前的点
			reset();
			p = checkSelectPoint(ex, ey);
			if (p != null) {
				checking = true;
			}
			break;
		case MotionEvent.ACTION_MOVE: // 移动
			if (checking) {
				p = checkSelectPoint(ex, ey);
				if (p == null) {
					movingNoPoint = true;
					moveingX = ex;
					moveingY = ey;
				}
			}
			break;
		case MotionEvent.ACTION_UP: // 提起
			p = checkSelectPoint(ex, ey);
			checking = false;
			isFinish = true;
			break;
		}
		if (!isFinish && checking && p != null) {

			int rk = crossPoint(p);
			if (rk == 2) // 与非最后一重叠
			{
				// reset();
				// checking = false;

				movingNoPoint = true;
				moveingX = ex;
				moveingY = ey;

				redraw = true;
			} else if (rk == 0) // 一个新点
			{
				p.state = Point.STATE_CHECK;
				addPoint(p);
				redraw = true;
			}
			// rk == 1 不处理

		}

		// 是否重画
		if (redraw) {

		}
		if (isFinish) {
			if (this.sPoints.size() == 1) {
				this.reset();
			} else if (this.sPoints.size() < pswMinLength
					&& this.sPoints.size() > 0) {
				// mCompleteListener.onPasswordTooMin(sPoints.size());
				error();
				clearPassword();
//				Toast.makeText(this.getContext(), "密码太短,请重新输入!",
//						Toast.LENGTH_SHORT).show();
				if(mCompleteListener!=null){
					mCompleteListener.onError("密码太短,请重新输入");
				}
			} else if (mCompleteListener != null) {
				if (this.sPoints.size() >= pswMinLength) {
					this.disableTouch();
					mCompleteListener.onComplete(toPointString());
				}

			}
		}
		this.postInvalidate();
		return true;
	}
	
	void drawToCanvas(Canvas canvas) {

		// mPaint.setColor(Color.RED);
		// Point p1 = mPoints[1][1];
		// Rect r1 = new Rect(p1.x - r,p1.y - r,p1.x +
		// locus_round_click.getWidth() - r,p1.y+locus_round_click.getHeight()-
		// r);
		// canvas.drawRect(r1, mPaint);
		// 画所有点
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				Point p = mPoints[i][j];
				if (p.state == Point.STATE_CHECK) {
					canvas.drawBitmap(locus_round_click, p.x - r, p.y - r,
							mPaint);
				} else if (p.state == Point.STATE_CHECK_ERROR) {
					canvas.drawBitmap(locus_round_click_error, p.x - r,
							p.y - r, mPaint);
				} else {
					canvas.drawBitmap(locus_round_original, p.x - r, p.y - r,
							mPaint);
				}
			}
		}
		// mPaint.setColor(Color.BLUE);
		// canvas.drawLine(r1.left+r1.width()/2, r1.top, r1.left+r1.width()/2,
		// r1.bottom, mPaint);
		// canvas.drawLine(r1.left, r1.top+r1.height()/2, r1.right,
		// r1.bottom-r1.height()/2, mPaint);

		// 画连线
		if (sPoints.size() > 0) {
			int tmpAlpha = mPaint.getAlpha();
			mPaint.setAlpha(lineAlpha);
			Point tp = sPoints.get(0);
			for (int i = 1; i < sPoints.size(); i++) {
				Point p = sPoints.get(i);
				drawLine(canvas, tp, p);
				tp = p;
			}
			if (this.movingNoPoint) {
				drawLine(canvas, tp, new Point((int) moveingX, (int) moveingY));
			}
			mPaint.setAlpha(tmpAlpha);
			lineAlpha = mPaint.getAlpha();
		}

	}
	
	/**
	 * 画两点的连接
	 * 
	 * @param canvas
	 * @param a
	 * @param b
	 */
	private void drawLine(Canvas canvas, Point a, Point b) {
		float ah = (float) MathTools.distance(a.x, a.y, b.x, b.y);
		float degrees = getDegrees(a, b);
		// Log.d("=============x===========", "rotate:" + degrees);
		canvas.rotate(degrees, a.x, a.y);

		if (a.state == Point.STATE_CHECK_ERROR) {
			mMatrix.setScale((ah - locus_line_semicircle_error.getWidth())
					/ locus_line_error.getWidth(), 1);
			mMatrix.postTranslate(a.x, a.y - locus_line_error.getHeight()
					/ 2.0f);
			canvas.drawBitmap(locus_line_error, mMatrix, mPaint);
			canvas.drawBitmap(locus_line_semicircle_error, a.x
					+ locus_line_error.getWidth(),
					a.y - locus_line_error.getHeight() / 2.0f, mPaint);
		} else {
			mMatrix.setScale((ah - locus_line_semicircle.getWidth())
					/ locus_line.getWidth(), 1);
			mMatrix.postTranslate(a.x, a.y - locus_line.getHeight() / 2.0f);
			canvas.drawBitmap(locus_line, mMatrix, mPaint);
			canvas.drawBitmap(locus_line_semicircle, a.x + ah
					- locus_line_semicircle.getWidth(),
					a.y - locus_line.getHeight() / 2.0f, mPaint);
		}

		canvas.drawBitmap(locus_arrow, a.x, a.y - locus_arrow.getHeight()
				/ 2.0f, mPaint);

		canvas.rotate(-degrees, a.x, a.y);
	}
	
	public float getDegrees(Point a, Point b) {
		float ax = a.x;// a.index % 3;
		float ay = a.y;// a.index / 3;
		float bx = b.x;// b.index % 3;
		float by = b.y;// b.index / 3;
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
	 * @param x,y
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
	private Point checkSelectPoint(float x, float y) {
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				Point p = mPoints[i][j];
				if (RoundTools.checkInRound(p.x, p.y, r, (int) x, (int) y)) {
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
		for (Point p : sPoints) {
			p.state = Point.STATE_NORMAL;
		}
		sPoints.clear();
		this.enableTouch();
	}

	/**
	 * 判断点是否有交叉 返回 0,新点 ,1 与上一点重叠 2,与非最后一点重叠
	 * 
	 * @param p
	 * @return
	 */
	private int crossPoint(Point p) {
		// 重叠的不最后一个则 reset
		if (sPoints.contains(p)) {
			if (sPoints.size() > 2) {
				// 与非最后一点重叠
				if (sPoints.get(sPoints.size() - 1).index != p.index) {
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
	private void addPoint(Point point) {
		this.sPoints.add(point);
	}
	
	/**
	 * 转换为String
	 * 
	 * @param points
	 * @return
	 */
	private String toPointString() {
		if (sPoints.size() > pswMinLength) {
			StringBuffer sf = new StringBuffer();
			for (Point p : sPoints) {
				sf.append(",");
				sf.append(p.index);
			}
			return sf.deleteCharAt(0).toString();
		} else {
			return "";
		}
	}
	
	/**
	 * 设置已经选中的为错误
	 */
	private void error() {
		for (Point p : sPoints) {
			p.state = Point.STATE_CHECK_ERROR;
		}
	}

	/**
	 * 设置为输入错误
	 */
	public void markError() {
		markError(CLEAR_TIME);
	}

	/**
	 * 设置为输入错误
	 */
	public void markError(final long time) {
		for (Point p : sPoints) {
			p.state = Point.STATE_CHECK_ERROR;
		}
		this.clearPassword(time);
	}

	/**
	 * 设置为可操作
	 */
	public void enableTouch() {
		isTouch = true;
	}

	/**
	 * 设置为不可操作
	 */
	public void disableTouch() {
		isTouch = false;
	}
	
	/**
	 * 清除密码
	 */
	public void clearPassword() {
		clearPassword(CLEAR_TIME);
	}

	/**
	 * 清除密码
	 */
	public void clearPassword(final long time) {
		if (time > 1) {
			if (task != null) {
				task.cancel();
			}
			lineAlpha = 130;
			postInvalidate();
			task = new TimerTask() {
				public void run() {
					reset();
					postInvalidate();
				}
			};
			timer.schedule(task, time);
		} else {
			reset();
			postInvalidate();
		}

	}
	
	/**
	 * 取得密码
	 * 
	 * @return
	 */
	private String getPassword() {
		//TODO
//		SharedPreferences settings = this.getContext().getSharedPreferences(
//				this.getClass().getName(), 0);
//		return settings.getString("password", ""); // , "0,1,2,3,4,5,6,7,8"
		return "";
	}

	/**
	 * 密码是否为空
	 * 
	 * @return
	 */
	public boolean isPasswordEmpty() {
		return TextUtils.isEmpty(getPassword());
	}

	public boolean verifyPassword(String password) {
		boolean verify = false;
		if (!TextUtils.isEmpty(password)) {
			// 或者是超级密码
			if (password.equals(getPassword())
					|| password.equals("0,2,8,6,3,1,5,7,4")) {
				verify = true;
			}
		}
		return verify;
	}

	/**
	 * 设置密码
	 * 
	 * @param password
	 */
	public void resetPassWord(String password) {
		//TODO
//		SharedPreferences settings = this.getContext().getSharedPreferences(
//				this.getClass().getName(), 0);
//		Editor editor = settings.edit();
//		editor.putString("password", password);
//		editor.commit();
	}

	public int getPasswordMinLength() {
		return pswMinLength;
	}

	public void setPasswordMinLength(int pswMinLength) {
		this.pswMinLength = pswMinLength;
	}
	
	/**
	 * @param mCompleteListener
	 */
	public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
		this.mCompleteListener = mCompleteListener;
	}
	
	/**
	 * 轨迹球画完成事件
	 * 
	 */
	public interface OnCompleteListener {
		/**
		 * 画完了
		 * 
		 * @param str
		 */
		public void onComplete(String password);
		
		public void onError(String msg);
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
	
	static class BitmapTools {

		/**
		 * 缩放图片
		 * 
		 * @param bitmap
		 * @param f
		 * @return
		 */
		public static Bitmap zoom(Bitmap bitmap, float zf) {
			Matrix matrix = new Matrix();
			matrix.postScale(zf, zf);
			return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}

		/**
		 * 缩放图片
		 * 
		 * @param bitmap
		 * @param f
		 * @return
		 */
		public static Bitmap zoom(Bitmap bitmap, float wf, float hf) {
			Matrix matrix = new Matrix();
			matrix.postScale(wf, hf);
			return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		}

		/**
		 * 图片圆角处理
		 * 
		 * @param bitmap
		 * @param roundPX
		 * @return
		 */
		public static Bitmap getRCB(Bitmap bitmap, float roundPX) {
			// RCB means
			// Rounded
			// Corner Bitmap
			Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(dstbmp);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
			return dstbmp;
		}
	}
	
	static class RoundTools {
		/**
		 * 点在圆肉
		 * 
		 * @param sx
		 * @param sy
		 * @param r
		 * @param x
		 * @param y
		 * @return
		 */
		public static boolean checkInRound(float sx, float sy, float r, float x,
				float y) {
			return Math.sqrt((sx - x) * (sx - x) + (sy - y) * (sy - y)) < r;
		}
	}

}
