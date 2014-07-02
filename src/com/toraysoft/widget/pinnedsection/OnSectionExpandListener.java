package com.toraysoft.widget.pinnedsection;

import android.view.View;

public interface OnSectionExpandListener {
	void onSectionCollapse(int sectionPosition);

	void onSectionExpand(int sectionPosition);

	boolean onSectionClick(PinnedSectionListView parent, View v,
			int sectionPosition, long id);

	boolean onChildClick(PinnedSectionListView parent, View v,
			int sectionPosition, int childPosition, long id);
}
