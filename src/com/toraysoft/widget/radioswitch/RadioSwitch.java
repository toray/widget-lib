package com.toraysoft.widget.radioswitch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RadioSwitch extends View{
	
	Drawable drawableButton;
	Drawable drawablePoint;
	float mx,my,mr = 0f;
	int pointTop;
	float degrees = 0f;
	float deta_degree = 0f;
	
	boolean inRound = false;
	
	OnSwitchListener mSwitchListener;
	
	public RadioSwitch(Context context) {
		super(context);
		init();
	}

	public RadioSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RadioSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	void init() {
		
	}
	
	public void setRadioButton(int resId,int width,int heigth) {
		drawableButton = getContext().getResources().getDrawable(resId);
		drawableButton.setBounds(0,0,width,heigth);
	}
	
	public void setRadioPoint(int resId,int width,int heigth,int top) {
		drawablePoint = getContext().getResources().getDrawable(resId);
		this.pointTop = top;
		drawablePoint.setBounds(0,0,width,heigth);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int w = getWidth();
		Rect bounds = drawableButton.getBounds();
		int bw = bounds.right-bounds.left;
		int dbw = (w-(bounds.right-bounds.left))/2;
		canvas.save();
		canvas.translate(dbw, 0);
		
		mx = dbw +bw/2;
		my = bw/2;
		mr = bw/2;
		
		drawableButton.draw(canvas);
		canvas.restore();
		canvas.save();
		bounds = drawablePoint.getBounds();
		dbw = (w-(bounds.right-bounds.left))/2;
		int dbh = pointTop;
		float pw = bounds.right-bounds.left;
		float ph = bounds.bottom-bounds.top;
		canvas.translate(dbw, dbh);
		canvas.rotate(deta_degree,pw/2,ph/2);
		drawablePoint.draw(canvas);
		canvas.restore();
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		float down_x = 0f;
		float down_y = 0f;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
            	down_x = event.getX();
            	down_y = event.getY();
            	inRound = RoundTools.checkInRound(mx, my, mr, down_x, down_y);
            	degrees = 0f;
            	deta_degree = 0f;
                break;

            }
            case MotionEvent.ACTION_MOVE: {
            	if(inRound){
            		 float target_x = event.getX();
                     float target_y = event.getY();
                     float degree = getDegrees(mx, my, target_x, target_y);
                     float dete = degree - degrees;
                     if (dete < -270) {
                         dete = dete + 360;
                     } else if (dete > 270) {
                         dete = dete - 360;
                     }
                     deta_degree += dete;
                     if (deta_degree > 360 || deta_degree < -360) {
                         deta_degree = deta_degree % 360;
                     }
                     if(deta_degree>90) deta_degree = 90;
                     if(deta_degree<-90) deta_degree = -90;
                     degrees = degree;
                     this.postInvalidate();
            	}
                break;
            }
            case MotionEvent.ACTION_UP: {
            	if(inRound){
            		if(mSwitchListener!=null){
	            		if(deta_degree>45){
	            			mSwitchListener.onNext();
	            		}else if(deta_degree<-45){
	            			mSwitchListener.onPrev();
	            		}else{
	            			
	            		}
            		}
            		post(degreeTask);
            	}
                break;
            }
        }
        return true;
    }
	
	Runnable degreeTask = new Runnable() {
		
		@Override
		public void run() {
			if(deta_degree>0){
				deta_degree -= 5;
				if(deta_degree<0)
					deta_degree=0;
				postDelayed(this, 1);
				postInvalidate();
			}else if(deta_degree<0){
				deta_degree += 5;
				if(deta_degree>0)
					deta_degree=0;
				postDelayed(this, 1);
				postInvalidate();
			}
		}
	};
	
	public float getDegrees(float ax,float ay, float bx, float by) {
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
	
	float detaDegree(float src_x, float src_y, float target_x, float target_y) {

        float detaX = target_x - src_x;
        float detaY = target_y - src_y;
        double d;
        if (detaX != 0) {
            float tan = Math.abs(detaY / detaX);

            if (detaX > 0) {

                if (detaY >= 0) {
                    d = Math.atan(tan);

                } else {
                    d = 2 * Math.PI - Math.atan(tan);
                }

            } else {
                if (detaY >= 0) {

                    d = Math.PI - Math.atan(tan);
                } else {
                    d = Math.PI + Math.atan(tan);
                }
            }

        } else {
            if (detaY > 0) {
                d = Math.PI / 2;
            } else {

                d = -Math.PI / 2;
            }
        }

        return (float)((d * 180) / Math.PI);
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
	
	public void setOnSwitchListener(OnSwitchListener l) {
		this.mSwitchListener = l;
	}

	public interface OnSwitchListener{
		void onPrev();
		void onNext();
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
	
	static class RoundTools {
		/**
		 * 点在圆内
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
