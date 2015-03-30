package com.toraysoft.widget.adapter.expand;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseExpandGroupAdapter extends BaseExpandAdapter {

	public BaseExpandGroupAdapter(Context context) {
		super(context);
	}

	@Override
	public int getCount() {
		int size = 0;
//		size += getGroupCount();
		for (int i = 0; i < getGroupCount(); i++) {
			int count = getChildrenCount(i);
			size += count;
			if(isGroupViewShow(i) && count>0){		
				size+=1;
			}
			
		}
		return size;
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	protected int getItemPosition(int groupPosition,int childPosition){
		int size = 0;
		if(isGroupViewShow(groupPosition)){		
			size+=1;
		}
		for (int i = 0; i < groupPosition; i++) {
			int count = getChildrenCount(i);
			size += count;
			if(isGroupViewShow(i) && count>0){		
				size+=1;
			}
		}
		size += childPosition;
		return size;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int groupPosition = -1;
		int childPosition = -1;
		int size = 0;
		for (int i = 0; i < getGroupCount(); i++) {
			int lastSize = size;
			int count =  getChildrenCount(i);
			size += count;
			if(isGroupViewShow(i) && count>0){
				size +=1;
			}
			if(position == lastSize){
				if(count==0){
					continue;
				}
				groupPosition = i;
				if(!isGroupViewShow(i)){
					childPosition = 0;
				}
				break;
			} else if (position < size) {
				groupPosition = i;
				childPosition = position - lastSize;
				if(isGroupViewShow(i)){
					childPosition -= 1;
				}
				break;
			}
		}
		if(childPosition==-1){
			View view = getGroupView(groupPosition, convertView, parent);
//			if(isGroupViewShow(groupPosition)){
//				view.setVisibility(View.VISIBLE);
//			}else{
//				view.setVisibility(View.GONE);
//			}
			return  view;
		}else{
			return getChildView(groupPosition, childPosition, convertView, parent);
		}
	}
	
	protected abstract boolean isGroupViewShow(int position);
	
	protected abstract View getChildView(int groupPosition, int childPosition,
			View convertView, ViewGroup parent);
	
	protected abstract View getGroupView(int groupPosition,
			View convertView, ViewGroup parent);

	protected abstract int getChildrenCount(int groupPosition);

	protected abstract int getGroupCount();
}
