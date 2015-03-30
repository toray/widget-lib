package com.toraysoft.widget.adapter.expand;

import android.content.Context;
import android.view.View;

public abstract class BaseExpandTabAdapter extends  BaseExpandAdapter{
	int expandChildIndex;

	public BaseExpandTabAdapter(Context context) {
		super(context);
	}
	
	
	protected void expand(View view, View tag, int groupPosition, int childPosition) {
		if(isTask){
			return;
		}
		isTask = true;
		if (expandIndex == -1) {
			expandIndex = groupPosition;
			expandChildIndex = childPosition;
			tag.setVisibility(View.VISIBLE);
			view.setTag(tag);
		} else if (expandIndex == groupPosition) {
			if (expandChildIndex == childPosition) {
				expandIndex = -1;
				tag.setVisibility(View.INVISIBLE);
				view.setTag(null);
			}else{
				Object t = view.getTag();
				if (t != null && t instanceof View) {
					((View) t).setVisibility(View.INVISIBLE);
				}
				tag.setVisibility(View.VISIBLE);
				view.setTag(tag);
				expandChildIndex = childPosition;
				isTask = false;
				return;
			}
		}else{
			if (expandIndex != -1 && expands.containsKey(expandIndex)
					&& expands.get(expandIndex) != null) {
				View v = expands.get(expandIndex);
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
			expandIndex = groupPosition;
			expandChildIndex = childPosition;
		}
		view.startAnimation(new ViewExpandAnimation(view,this));
	}
}
