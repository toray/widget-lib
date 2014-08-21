package com.toraysoft.widget.locuspsw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class LocusLine{
	Resources res;
	Drawable mDrawable;
	
	int h;
	
//	public LocusLine(Context context,int resId,int height){
//		this.mContext = context;
//		this.h = height;
//		
//		mDrawable = mContext.getResources().getDrawable(resId);
//	}
	
	public LocusLine(Resources res,int resId,int height){
		this.res = res;
		this.h = height;
		
		mDrawable = res.getDrawable(resId);
	}
}
