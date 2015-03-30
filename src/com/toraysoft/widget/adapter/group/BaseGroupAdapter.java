package com.toraysoft.widget.adapter.group;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BaseGroupAdapter extends BaseAdapter {

	@Override
	public int getCount() {
		int size = 0;
		for (int i = 0; i < getGroupCount(); i++) {
			int count = getChildrenCount(i);
			size += count;
			if (isGroupViewShow(i) && count > 0) {
				size += 1;
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

	protected int getItemPosition(int groupPosition, int childPosition) {
		int size = 0;
		if (isGroupViewShow(groupPosition)) {
			size += 1;
		}
		for (int i = 0; i < groupPosition; i++) {
			int count = getChildrenCount(i);
			size += count;
			if (isGroupViewShow(i) && count > 0) {
				size += 1;
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
			int count = getChildrenCount(i);
			size += count;
			if (isGroupViewShow(i) && count > 0) {
				size += 1;
			}
			if (position == lastSize) {
				if (count == 0) {
					continue;
				}
				groupPosition = i;
				if (!isGroupViewShow(i)) {
					childPosition = 0;
				}
				break;
			} else if (position < size) {
				groupPosition = i;
				childPosition = position - lastSize;
				if (isGroupViewShow(i)) {
					childPosition -= 1;
				}
				break;
			}
		}
		if (childPosition == -1) {
			View view = getGroupView(groupPosition, convertView, parent);
			return view;
		} else {
			return getChildView(groupPosition, childPosition, convertView,
					parent);
		}
	}

	protected boolean isGroupViewShow(int position) {
		return true;
	}

	protected abstract View getChildView(int groupPosition, int childPosition,
			View convertView, ViewGroup parent);

	protected abstract View getGroupView(int groupPosition, View convertView,
			ViewGroup parent);

	protected abstract int getChildrenCount(int groupPosition);

	protected abstract int getGroupCount();
}
