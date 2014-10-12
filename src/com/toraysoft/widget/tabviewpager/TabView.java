package com.toraysoft.widget.tabviewpager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toraysoft.widget.R;

public class TabView extends LinearLayout implements OnClickListener {

	LinearLayout layout_tabs, layout_underline;
	View line_current;
	String tabs[];
	float textSize;
	float textViewPadding;
	ColorStateList textColor;
	int lineBottomColor;
	int lineColor;
	int tabBackground;
	int currentItem;
	int count;
	int width;
	boolean isDrawLine;
	int offset;
	
	OnItemChangeListener mOnItemChangeListener;

	public TabView(Context context) {
		super(context);
	}

	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		width = getResources().getDisplayMetrics().widthPixels;
		setOrientation(LinearLayout.VERTICAL);
	}

	private void init(AttributeSet attrs) {
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
				R.styleable.TabView);
		textSize = typedArray.getDimension(R.styleable.TabView_tabItemTextSize,
				14);
		textViewPadding = typedArray.getDimension(
				R.styleable.TabView_tabItemTextPadding, 10);
		textColor = typedArray.getColorStateList(R.styleable.TabView_tabItemTextColor);
		if(textColor==null)
			textColor = getResources().getColorStateList(R.color.tab_text_color_default);
		lineBottomColor = typedArray.getColor(
				R.styleable.TabView_tabItemLineBottomColor,
				Color.parseColor("#ffdadada"));
		lineColor = typedArray.getColor(R.styleable.TabView_tabItemLineColor,
				Color.parseColor("#ff9f3765"));
		tabBackground = typedArray.getColor(
				R.styleable.TabView_tabItemBackground,
				Color.parseColor("#00000000"));
		typedArray.recycle();
	}

	@Override
	public void onClick(View v) {
		if (v.getTag() == null)
			return;
		int position = (Integer) v.getTag();
		setCurrentItem(position);
		if (mOnItemChangeListener != null) {
			mOnItemChangeListener.onItemChange(position);
		}
	}
	
	protected void setTabs(String[] tabs) {
		this.tabs = tabs;
		count = tabs.length;
		setLabels();
		setUnderLine();
	}
	
	private void setLabels() {
		layout_tabs = new LinearLayout(getContext());
		layout_tabs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		for (int i = 0; i < tabs.length; i++) {
			String tab = tabs[i];
			TextView textview = new TextView(getContext());
			textview.setTextColor(textColor);
			textview.setText(tab);
			if (i == currentItem)
				textview.setSelected(true);
			else
				textview.setSelected(false);
			textview.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
			textview.setGravity(Gravity.CENTER);
			textview.setPadding(0, (int) textViewPadding, 0,
					(int) textViewPadding);
			textview.setTag(i);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, 1);
			textview.setLayoutParams(params);
			textview.setOnClickListener(this);
			layout_tabs.addView(textview);
		}
		addView(layout_tabs);
	}
	
	private void setUnderLine() {
		if (width == 0 || count == 0 || isDrawLine)
			return;
		isDrawLine = true;
		layout_underline = new LinearLayout(getContext());
		layout_underline.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		layout_underline.setOrientation(LinearLayout.VERTICAL);
		line_current = new View(getContext());
		LayoutParams params = new LayoutParams(
				width / count, 2);
		line_current.setLayoutParams(params);
		line_current.setBackgroundColor(lineColor);
		layout_underline.addView(line_current);

		View line = new View(getContext());
		line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
		line.setBackgroundColor(lineBottomColor);
		layout_underline.addView(line);
		addView(layout_underline);
	}
	
	void setCurrentItem(int currentItem) {
		if (currentItem < 0)
			currentItem = 0;
		if (currentItem > count)
			currentItem = count;
		this.currentItem = currentItem;
		for (int i = 0; i < count; i++) {
			TextView textview = (TextView) layout_tabs.findViewWithTag(i);
			if (textview != null) {
				if (currentItem == i) {
					textview.setSelected(true);
				} else {
					textview.setSelected(false);
				}
			}
		}
		int offset = line_current.getWidth()
				* currentItem;
		setLineOffset(offset);
	}
	
	private void setLineOffset(int offset) {
		int left = line_current.getLeft();
		int top = line_current.getTop();
		int right = line_current.getRight();
		int bottom = line_current.getBottom();
		this.offset = offset;
		line_current.layout(offset, top, right+ offset - left, bottom);
	}
	
	
	protected void onPageScrollStateChanged(int state) {

	}

	protected void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (line_current == null)
			return;
		int offset = (int) (line_current.getWidth() * (position + positionOffset));
		setLineOffset(offset);
	}

	protected void onPageSelected(int position) {
		setCurrentItem(position);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		setUnderLine();
		if (line_current != null)
			setLineOffset(offset);
	}
	
	public void setOnItemChangeListener(OnItemChangeListener l) {
		this.mOnItemChangeListener = l;
	}
	
	public interface OnItemChangeListener {
		public void onItemChange(int position);
	}
}
