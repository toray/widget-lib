package com.toraysoft.widget.expand;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public abstract class BaseExpandAdapter extends BaseAdapter implements OnExpandListener{
	int mLcdWidth = 0;
	float mDensity = 0;
	int expandIndex = -1;
	Map<Integer,View> expands = new HashMap<Integer, View>();
	boolean isTask;
	
	public BaseExpandAdapter(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		mLcdWidth = dm.widthPixels;
		mDensity = dm.density;
	}
	
	protected void initExpand(View view,int position){
		int widthSpec = MeasureSpec.makeMeasureSpec((int) (mLcdWidth - 10 * mDensity), MeasureSpec.EXACTLY);
		view.measure(widthSpec, 0);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
		if (position == expandIndex) {
			params.bottomMargin = 0;
			view.setVisibility(View.VISIBLE);
			Object tag = view.getTag();
			if (tag != null && tag instanceof View) {
				((View) tag).setVisibility(View.VISIBLE);
			}
		} else {
			params.bottomMargin = -view.getMeasuredHeight();
			view.setVisibility(View.GONE);
			Object tag = view.getTag();
			if (tag != null && tag instanceof View) {
				((View) tag).setVisibility(View.INVISIBLE);
			}
		}
		expands.put(position, view);
	}
	
	protected void expand(View view,View tag,int position){
		if (isTask) {
			return;
		}
		isTask = true;
		if (expandIndex == -1) {
			expandIndex = position;
			tag.setVisibility(View.VISIBLE);
			view.setTag(tag);
		} else if (expandIndex == position) {
			expandIndex = -1;
			tag.setVisibility(View.INVISIBLE);
			view.setTag(null);
		} else {
			if (expandIndex != -1 && expands.containsKey(expandIndex)
					&& expands.get(expandIndex) != null) {
				View v = expands.get(expandIndex);
//				int widthSpec = MeasureSpec.makeMeasureSpec((int) (mLcdWidth - 10 * mDensity), MeasureSpec.EXACTLY);
//				v.measure(widthSpec, 0);
//				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
//				params.bottomMargin = -v.getMeasuredHeight();
//				v.setVisibility(View.GONE);
				if (v != null) {
					Object t = v.getTag();
					if (t != null && t instanceof View) {
						((View) t).setVisibility(View.INVISIBLE);
					}
					v.startAnimation(new ViewExpandAnimation(v, null));
				}
			}
			tag.setVisibility(View.VISIBLE);
			view.setTag(tag);
			expandIndex = position;
		}
		view.startAnimation(new ViewExpandAnimation(view,this));
	}
	
	protected void expand(View view,int position){
		expand(view, null, position);
	}
	
	protected void hide(){
		if(expandIndex != -1 && expands.containsKey(expandIndex) && expands.get(expandIndex)!=null){
			View v = expands.get(expandIndex);
//			int widthSpec = MeasureSpec.makeMeasureSpec((int) (mLcdWidth - 10 * mDensity), MeasureSpec.EXACTLY);
//			v.measure(widthSpec, 0);
//			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
//			params.bottomMargin = -v.getMeasuredHeight();
//			v.setVisibility(View.GONE);
			if(v !=null){
				Object t = v.getTag();
				if(t !=null && t instanceof View){
					((View)t).setVisibility(View.INVISIBLE);
				}
				v.startAnimation(new ViewExpandAnimation(v,null));
			}
		}
		expandIndex = -1;
	}
	
	@Override
	public void onExpandFinish() {
		isTask = false;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		hide();
	}

	public boolean isTask() {
		return isTask;
	}
	
}
