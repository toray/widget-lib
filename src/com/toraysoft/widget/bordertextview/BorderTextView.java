package com.toraysoft.widget.bordertextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class BorderTextView extends TextView {

	private int position;

	public BorderTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BorderTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BorderTextView(Context context) {
		super(context);
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		// 将边框设为黑色
		paint.setColor(Color.parseColor("#d4d4d7"));
		// 画TextView的4个边
		// canvas.drawLine(0, 0, this.getWidth() - 0.4f, 0, paint);//上
		if (position == 0) {
			canvas.drawLine(0, 0, 0, this.getHeight() - 0.4f, paint);// 左
		}
		if (position == 1) {
			canvas.drawLine(this.getWidth() - 0.4f, 0, this.getWidth() - 0.4f,
					this.getHeight() - 0.4f, paint);// 右
		}
		canvas.drawLine(0, this.getHeight() - 0.4f, this.getWidth() - 0.4f,
				this.getHeight() - 0.4f, paint);// 下
	}

}
