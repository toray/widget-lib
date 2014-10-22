package com.toraysoft.widget.viptextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.rockerhieu.emojicon.EmojiconTextView;
import com.toraysoft.widget.R;

public class VipTextView extends RelativeLayout{

	EmojiconTextView mTextViewNormal;
	EmojiconTextView mTextViewVip;
	EmojiconTextView mTextViewStroke;
	int mNormalTextColor;
	int mVipLightTextColor;
	int mVipDeepTextColor;
	int mVipStrokeTextColor;
	boolean isVip;
	
	
	public VipTextView(Context context) {
		super(context);
		init(null);
	}

	public VipTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public VipTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	void init(AttributeSet attrs){
		Context context = getContext();
		mTextViewNormal = new EmojiconTextView(context, attrs);
		mTextViewVip = new EmojiconTextView(context, attrs);
		mTextViewStroke = new EmojiconTextView(context, attrs);
		
		if(attrs!=null){
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VipTextView);
			mNormalTextColor = a.getColor(R.styleable.VipTextView_normalTextColor, 0);
			mVipLightTextColor = a.getColor(R.styleable.VipTextView_vipLightTextColor, Color.parseColor("#fff99d"));
			mVipDeepTextColor = a.getColor(R.styleable.VipTextView_vipDeepTextColor, Color.parseColor("#f2b510"));
			mVipStrokeTextColor = a.getColor(R.styleable.VipTextView_vipStrokeTextColor,  Color.parseColor("#753a06"));
			
			if(mNormalTextColor!=0){
				mTextViewNormal.setTextColor(mNormalTextColor);
			}
			a.recycle();
		}
		
		Paint mPaintStroke = mTextViewStroke.getPaint();
		mPaintStroke.setStrokeWidth(2);
		mPaintStroke.setStyle(Style.FILL_AND_STROKE);
		mPaintStroke.setFakeBoldText(true);
		mTextViewStroke.setTextColor(mVipStrokeTextColor);
		
		Paint mPaintVip = mTextViewVip.getPaint();
		LinearGradient mLinearGradient = new LinearGradient(0, 0, 0, mTextViewVip.getTextSize(),new int[]{mVipLightTextColor,mVipDeepTextColor} , new float[]{0,0.6f}, TileMode.CLAMP);
		mPaintVip.setShader(mLinearGradient);
		mPaintStroke.setStyle(Style.FILL_AND_STROKE);
		mPaintStroke.setFakeBoldText(false);
		
		setVip(false);
		
		
		addView(mTextViewNormal);
		addView(mTextViewStroke);
		addView(mTextViewVip);
	}
	
	public void setText(CharSequence text){
		mTextViewNormal.setText(text);
		mTextViewVip.setText(text);
		mTextViewStroke.setText(text);
	}
	
	public void setText(int resid){
		mTextViewNormal.setText(resid);
		mTextViewVip.setText(resid);
		mTextViewStroke.setText(resid);
	}
	
	public void setVip(boolean isVip){
		this.isVip = isVip;
		if(isVip){
			mTextViewNormal.setVisibility(View.GONE);
			mTextViewVip.setVisibility(View.VISIBLE);
			mTextViewStroke.setVisibility(View.VISIBLE);
		}else{
			mTextViewNormal.setVisibility(View.VISIBLE);
			mTextViewVip.setVisibility(View.GONE);
			mTextViewStroke.setVisibility(View.GONE);
		}
		
	}
}
