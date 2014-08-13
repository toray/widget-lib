package com.toraysoft.widget.locuspsw;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class LocusLine{
	Context mContext;
	Drawable mDrawable;
	
	int w;
	
	public LocusLine(Context context,int resId,int width){
		this.mContext = context;
		this.w = width;
		
		mDrawable = mContext.getResources().getDrawable(resId);
	}
}
