package com.toraysoft.widget.locuspsw;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class LocusPoint {
	Context mContext;
	Drawable mDrawableDefault;
	Drawable mDrawableClick;
	float w,h;
	float x,y;
	int key;
	
	public static int STATE_NORMAL = 0; 
	public static int STATE_CHECK = 1; 
	
	int state = STATE_NORMAL;
	
	public LocusPoint(Context context,int defaultId,
						int clickId,int width,int height,int key){
		this.mContext = context;
		this.w = width;
		this.h = height;
		this.key = key;
		
		mDrawableDefault = mContext.getResources().getDrawable(defaultId);
		mDrawableDefault.setBounds(0, 0, width, height);
		mDrawableClick = mContext.getResources().getDrawable(clickId);
		mDrawableClick.setBounds(0, 0, width, height);
	}
	
}
