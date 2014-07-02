package com.toraysoft.widget.drawableverticalcenteredittext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

public class DrawableVerticalCenterEditText extends EditText {

	public DrawableVerticalCenterEditText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public DrawableVerticalCenterEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawableVerticalCenterEditText(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable drawableLeft = drawables[0];
			if (drawableLeft != null) {
				float textHeight = getHeight();
				int drawableHeight = 0;
				drawableHeight = drawableLeft.getIntrinsicHeight();
				float bodyHeight = textHeight - drawableHeight;
				canvas.translate(0, bodyHeight / 4);
			}
		}
		super.onDraw(canvas);
	}

}
