package com.toraysoft.widget.showcase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;


public class ShowcaseView extends RelativeLayout {
	
	int showcaseX = -1;
    int showcaseY = -1;
    float scaleMultiplier = 1f;

	Bitmap bitmapBuffer;
	
	boolean isShowing;
	boolean hasNoTarget;

	protected ShowcaseView(Context context) {
		this(context, null, 0);
	}

	protected ShowcaseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		getViewTreeObserver().addOnGlobalLayoutListener(new UpdateOnGlobalLayout());
		
//		showcaseDrawer = new NewShowcaseDrawer(getResources());
		
		init();
	}
	
	void init() {
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
//	@Override
//    public boolean onTouchEvent(MotionEvent event) {
//    	return true;
//    }
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
        if (showcaseX < 0 || showcaseY < 0 || bitmapBuffer == null) {
            super.dispatchDraw(canvas);
            return;
        }

        erase(bitmapBuffer);
        if (!hasNoTarget) {
        	drawShowcase(bitmapBuffer, showcaseX, showcaseY, scaleMultiplier);
        	drawToCanvas(canvas, bitmapBuffer);
        }
        
//        //Draw background color
//        showcaseDrawer.erase(bitmapBuffer);
//
//        // Draw the showcase drawable
//        if (!hasNoTarget) {
//            showcaseDrawer.drawShowcase(bitmapBuffer, showcaseX, showcaseY, scaleMultiplier);
//            showcaseDrawer.drawToCanvas(canvas, bitmapBuffer);
//        }
//
//        // Draw the text on the screen, recalculating its position if necessary
//        textDrawer.draw(canvas);

        super.dispatchDraw(canvas);

    }
	
	void erase(Bitmap bitmapBuffer){
		bitmapBuffer.eraseColor(Color.argb(128, 80, 80, 80));
	}
	
	void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
		PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
		float outerRadius = 128;
		float innerRadius = 96;
        Canvas bufferCanvas = new Canvas(buffer);
        Paint eraserPaint = new Paint();
        eraserPaint.setColor(0xFFFFFF);
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(xfermode);
        eraserPaint.setAntiAlias(true);
        eraserPaint.setAlpha(153);
        bufferCanvas.drawCircle(x, y, outerRadius, eraserPaint);
        eraserPaint.setAlpha(0);
        bufferCanvas.drawCircle(x, y, innerRadius, eraserPaint);
    }
	
	void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
		Paint basicPaint = new Paint();
        canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
    }


	public void show() {
		isShowing = true;
		// mEventListener.onShowcaseViewShow(this);
		// fadeInShowcase();
		setVisibility(View.VISIBLE);
	}

	private void hideImmediate() {
		isShowing = false;
		setVisibility(GONE);
	}

	public void setTarget(final Target target) {
		setShowcase(target, false);
	}
	
	void setShowcase(final Target target, final boolean animate) {
        postDelayed(new Runnable() {
            @Override
            public void run() {

                    updateBitmap();
                    Point targetPoint = target.getPoint();
                    if (targetPoint != null) {
                        hasNoTarget = false;
//                        if (animate) {
//                            animationFactory.animateTargetToPoint(ShowcaseView.this, targetPoint);
//                        } else {
                            setShowcasePosition(targetPoint);
//                        }
                    } else {
                        hasNoTarget = true;
                        invalidate();
                    }

            }
        }, 100);
    }
	
	void setShowcasePosition(Point point) {
        setShowcasePosition(point.x, point.y);
    }

    void setShowcasePosition(int x, int y) {
//        if (shotStateStore.hasShot()) {
//            return;
//        }
        showcaseX = x;
        showcaseY = y;
        //init();
        invalidate();
    }
	
	void updateBitmap() {
        if (bitmapBuffer == null || haveBoundsChanged()) {
            if(bitmapBuffer != null)
        		bitmapBuffer.recycle();
            bitmapBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        }
    }
	
	boolean haveBoundsChanged() {
        return getMeasuredWidth() != bitmapBuffer.getWidth() ||
                getMeasuredHeight() != bitmapBuffer.getHeight();
    }

	public static class Builder {
		final ShowcaseView showcaseView;
		private final Activity activity;

		public Builder(Activity activity) {
			this.activity = activity;
			this.showcaseView = new ShowcaseView(activity);
			this.showcaseView.setTarget(Target.NONE);
		}

		public ShowcaseView build() {
			insertShowcaseView(showcaseView, activity);
			return showcaseView;
		}

		void insertShowcaseView(ShowcaseView showcaseView, Activity activity) {
			((ViewGroup) activity.getWindow().getDecorView())
					.addView(showcaseView);
			showcaseView.show();
		}

		public Builder setTarget(Target target) {
			showcaseView.setTarget(target);
			return this;
		}
	}
	
	class UpdateOnGlobalLayout implements  ViewTreeObserver.OnGlobalLayoutListener{
		
        @Override
        public void onGlobalLayout() {
//            if (!shotStateStore.hasShot()) {
                updateBitmap();
//            }
        }
    }
}
