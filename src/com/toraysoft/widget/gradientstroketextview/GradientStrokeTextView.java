package com.toraysoft.widget.gradientstroketextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.TextView;

public class GradientStrokeTextView extends TextView{
	
	Shader mShader;
	
	public GradientStrokeTextView(Context context) {
		super(context);
		init();
	}

	public GradientStrokeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GradientStrokeTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	void init(){
		mShader =new LinearGradient(0, 0, 0, (float) (getTextSize()*0.6),
				Color.parseColor("#fffff99d"), Color.parseColor("#fff2b510"), TileMode.CLAMP);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		Paint mStrokePaint = getPaint();
		mStrokePaint.setStrokeWidth(1);
		mStrokePaint.setStyle(Style.FILL_AND_STROKE);
		mStrokePaint.setFakeBoldText(true);
		mStrokePaint.setColor(Color.parseColor("#ff753a06"));
		super.onDraw(canvas);
		
		mStrokePaint = getPaint();
		mStrokePaint.setStrokeWidth(0);
		mStrokePaint.setStyle(Style.FILL);
		mStrokePaint.setFakeBoldText(false);
		mStrokePaint.setShader(mShader);
		super.onDraw(canvas);
	}

}
