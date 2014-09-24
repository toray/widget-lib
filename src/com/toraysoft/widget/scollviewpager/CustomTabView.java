package com.toraysoft.widget.scollviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toraysoft.widget.R;

public class CustomTabView extends LinearLayout implements
		android.view.View.OnClickListener {
	LinearLayout layout_tabs;
	LinearLayout layout_underline;
	View line_current;
	String tabs[];
	int unreads[];
	int count;
	int height;
	int width;
	int currentItem;

	int textColorDefault;
	int textColorSelect;
	int lineColor;
	int lineCurrentColor;
	float textSize;
	float textViewPadding;

	boolean isDrawLine = false;

	int offset;

	int under_line_height;
	int under_line_margin = 0;

	OnItemChangeListener mOnItemChangeListener;

	int mask_resbg = -1;
	boolean has_mask = false;
	
	public CustomTabView(Context context) {
		super(context);
		init();
	}

	public CustomTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		init();
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
				R.styleable.CustomTabView);
		textSize = typedArray.getDimension(
				R.styleable.CustomTabView_tabTextSize, 8);
		textViewPadding = typedArray.getDimension(
				R.styleable.CustomTabView_tabTextPadding, 5);
		textColorDefault = typedArray.getColor(
				R.styleable.CustomTabView_tabTextDefaultColor,
				Color.parseColor("#333333"));
		textColorSelect = typedArray.getColor(
				R.styleable.CustomTabView_tabTextSelectColor,
				Color.parseColor("#37BC9B"));
		lineCurrentColor = typedArray.getColor(
				R.styleable.CustomTabView_tabLineSelectColor,
				Color.parseColor("#37BC9B"));
		lineColor = typedArray.getColor(
				R.styleable.CustomTabView_tabLineDefaultColor,
				Color.parseColor("#dedede"));
		typedArray.recycle();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		textColorDefault = Color.parseColor("#333333");
		textColorSelect = Color.parseColor("#37BC9B");
		lineCurrentColor = Color.parseColor("#37BC9B");
		lineColor = Color.parseColor("#dedede");

		textSize = 18;
		textViewPadding = 8;
	}

	public void setLineCurrentColor(int lineCurrentColor) {
		this.lineCurrentColor = lineCurrentColor;
	}

	public void setTextColorDefault(int textColorDefault) {
		this.textColorDefault = textColorDefault;
	}

	public void setTextColorSelect(int textColorSelect) {
		this.textColorSelect = textColorSelect;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public void setTextViewPadding(float textViewPadding) {
		this.textViewPadding = textViewPadding;
	}

	protected void setTabs(String[] tabs) {
		this.tabs = tabs;
		this.unreads = new int[tabs.length];
		count = tabs.length;
		if (has_mask) {
			setLabelsMark();
		} else {
			setLabels();
		}
		setUnderLine();
	}

	private void setLabels() {
		layout_tabs = new LinearLayout(getContext());
		layout_tabs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		for (int i = 0; i < tabs.length; i++) {
			String tab = tabs[i];
			TextView textview = new TextView(getContext());
			textview.setText(tab);
			if (i == currentItem)
				textview.setTextColor(textColorSelect);
			else
				textview.setTextColor(textColorDefault);
			textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
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

	private void setLabelsMark() {
		layout_tabs = new LinearLayout(getContext());
		layout_tabs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		for (int i = 0; i < tabs.length; i++) {
			String tab = tabs[i];
			RelativeLayout rl_tab = new RelativeLayout(getContext());
			LayoutParams rl_lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, 1);
			rl_tab.setLayoutParams(rl_lp);
			TextView tv_title = new TextView(getContext());
			RelativeLayout.LayoutParams tv_lpt = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			tv_lpt.addRule(RelativeLayout.CENTER_IN_PARENT);
			tv_title.setLayoutParams(tv_lpt);
			tv_title.setText(tab);
			if (i == currentItem) {
				tv_title.setTextColor(textColorSelect);
			} else {
				tv_title.setTextColor(textColorDefault);
			}
			tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
			tv_title.setPadding(0, (int) textViewPadding, 0,
					(int) textViewPadding);
			tv_title.setTag(i);
//			switch (i) {
//			case 0:
//				tv_title.setId(R.id.custom_tab_one);
//				break;
//			case 1:
//				tv_title.setId(R.id.custom_tab_two);
//				break;
//			case 2:
//				tv_title.setId(R.id.custom_tab_third);
//				break;
//			case 3:
//				tv_title.setId(R.id.custom_tab_four);
//				break;
//			default:
//				break;
//			}
			tv_title.setId(i+1);
			tv_title.setOnClickListener(this);

			// mask
			TextView tv_mask = new TextView(getContext());
			RelativeLayout.LayoutParams tv_lpm = new RelativeLayout.LayoutParams(
					(int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 16, getContext()
									.getResources().getDisplayMetrics()),
					(int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 16, getContext()
									.getResources().getDisplayMetrics()));
			tv_lpm.addRule(RelativeLayout.CENTER_VERTICAL);
//			switch (i) {
//			case 0:
//				tv_lpm.addRule(RelativeLayout.RIGHT_OF, R.id.custom_tab_one);
//				tv_mask.setId(R.id.custom_mask_one);
//				break;
//			case 1:
//				tv_lpm.addRule(RelativeLayout.RIGHT_OF, R.id.custom_tab_two);
//				tv_mask.setId(R.id.custom_mask_two);
//				break;
//			case 2:
//				tv_lpm.addRule(RelativeLayout.RIGHT_OF, R.id.custom_tab_third);
//				tv_mask.setId(R.id.custom_mask_third);
//				break;
//			case 3:
//				tv_lpm.addRule(RelativeLayout.RIGHT_OF, R.id.custom_tab_four);
//				tv_mask.setId(R.id.custom_mask_four);
//				break;
//			default:
//				break;
//			}
			tv_lpm.addRule(RelativeLayout.RIGHT_OF, tv_title.getId());
			if(unreads[i]==0){
				tv_mask.setVisibility(View.GONE);
			}else{
				tv_mask.setVisibility(View.VISIBLE);
				tv_mask.setText(""+unreads[i]);
			}
			tv_mask.setGravity(Gravity.CENTER);
			tv_mask.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
			tv_mask.setBackgroundResource(mask_resbg);
			tv_mask.setTextColor(Color.WHITE);
			tv_mask.setSingleLine();
			tv_mask.setLayoutParams(tv_lpm);

			rl_tab.addView(tv_title);
			rl_tab.addView(tv_mask);
			layout_tabs.addView(rl_tab);
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
				((width / count) - under_line_margin), under_line_height);
		line_current.setLayoutParams(params);
		line_current.setBackgroundColor(lineCurrentColor);
		layout_underline.addView(line_current);

		View line = new View(getContext());
		line.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
		line.setBackgroundColor(lineColor);
		layout_underline.addView(line);
		addView(layout_underline);
	}

	public void setCurrentItem(int currentItem) {
		if (currentItem < 0)
			currentItem = 0;
		if (currentItem > count)
			currentItem = count;
		this.currentItem = currentItem;
		for (int i = 0; i < count; i++) {
			TextView textview = (TextView) layout_tabs.findViewWithTag(i);
			if (textview != null) {
				if (currentItem == i) {
					textview.setTextColor(textColorSelect);
					// LayoutParams params =
					// ((LayoutParams)line_current.getLayoutParams());
					// params.leftMargin = line_current.getWidth()*currentItem;
					// line_current.setLayoutParams(params);
				} else {
					textview.setTextColor(textColorDefault);
				}
			}
		}
		int offset = (line_current.getWidth() + (under_line_margin))
				* currentItem;
		setLineOffset(offset);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		width = getMeasuredWidth();
		height = getMeasuredHeight();
		setUnderLine();
		if (line_current != null)
			setLineOffset(offset);
	}

	protected void setOnItemChangeListener(OnItemChangeListener l) {
		this.mOnItemChangeListener = l;
	}

	protected void onPageScrollStateChanged(int state) {

	}

	protected void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (line_current == null)
			return;
		// LayoutParams params = ((LayoutParams)line_current.getLayoutParams());
		// params.leftMargin =
		// (int)(line_current.getWidth()*(position+positionOffset));
		// line_current.setLayoutParams(params);
		// line_current.offsetLeftAndRight(offset);
		//
		// int offset = (int)
		// (line_current.getWidth()*(position+positionOffset));
		// int left = line_current.getLeft();
		// int top = line_current.getTop();
		// int right = line_current.getRight();
		// int bottom = line_current.getBottom();
		// line_current.layout(offset, top, right+(offset-left), bottom);

		int offset = (int) ((line_current.getWidth() + under_line_margin) * (position + positionOffset));
		setLineOffset(offset);
	}

	private void setLineOffset(int offset) {
		int left = line_current.getLeft();
		int top = line_current.getTop();
		int right = line_current.getRight();
		int bottom = line_current.getBottom();
		this.offset = offset;
		line_current.layout(offset + (under_line_margin / 2), top, right
				+ (offset + (under_line_margin / 2) - left), bottom);
	}

	protected void onPageSelected(int position) {
		setCurrentItem(position);
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

	public interface OnItemChangeListener {
		public void onItemChange(int position);
	}

	public void setTabBackground(int bgColor) {
		this.setBackgroundColor(bgColor);
	}

	public void setCurrentUnderLineHeight(int height) {
		this.under_line_height = height;
	}

	public void setCurrentUnderLineMargin(int width) {
		this.under_line_margin = width;
	}

	public void setMaskResBg(int mask_resbg) {
		this.mask_resbg = mask_resbg;
	}

	public void setHasMask(boolean has_mask) {
		this.has_mask = has_mask;
	}
	
	public void setUnread(int count,int position){
		if(unreads!=null && unreads.length>position){
			unreads[position] = count;
		}
		if(layout_tabs!=null && layout_tabs.getChildCount()>position){
			RelativeLayout rl_tab = (RelativeLayout) layout_tabs.getChildAt(position);
			if(rl_tab.getChildCount()>=2){
				TextView tv_mask = (TextView) rl_tab.getChildAt(1);
				if(count>0){
					tv_mask.setText(""+count);
					tv_mask.setVisibility(View.VISIBLE);
				}else{
					tv_mask.setVisibility(View.GONE);
				}
			}
		}
	}

	public void setMessageUnread(String count) {
//		TextView textView = (TextView) findViewById(R.id.custom_mask_one);
//		if (textView != null) {
//			if (count == null) {
//				textView.setVisibility(View.INVISIBLE);
//			} else {
//				textView.setText(count);
//				textView.setVisibility(View.VISIBLE);
//			}
//		}
		
	}

	public void setUserUnread(String count) {
//		TextView textView = (TextView) findViewById(R.id.custom_mask_two);
//		if (textView != null) {
//			if (count == null) {
//				textView.setVisibility(View.INVISIBLE);
//			} else {
//				textView.setText(count);
//				textView.setVisibility(View.VISIBLE);
//			}
//		}
	}
}
