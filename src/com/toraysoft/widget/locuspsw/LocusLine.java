package com.toraysoft.widget.locuspsw;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class LocusLine{
	Context mContext;
	Drawable mDrawable;
	
	int h;
	
	public LocusLine(Context context,int resId,int height){
		this.mContext = context;
		this.h = height;
		
		mDrawable = mContext.getResources().getDrawable(resId);
	}
}
