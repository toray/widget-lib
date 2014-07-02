package com.toraysoft.widget.emotionpanel;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class EmojiItemLayout extends LinearLayout implements OnClickListener,
		OnTouchListener {

	private List<Map<String, Object>> list;
	private int start;
	private OnIEmojiPanelItemClickListener onIEmojiPanelItemClickListener = null;
	private int LINE = 4;
	private int LINE_ITEMS = 7;
	private int screenWidth = 0;
	private int screenWidthDefault = 480;
	private int MARGIN_HORIZONTAL = 15;
	private int emojiBgRes = -1;
	private int delRes = -1;
	private int REALY_COUNT = 124;
	private int LAST_POSITION = 139;

	public EmojiItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmojiItemLayout(Context context) {
		super(context);
	}

	public EmojiItemLayout(Context context, List<Map<String, Object>> list,
			int start, int bgres, int delres) {
		super(context);
		this.setOrientation(VERTICAL);
		this.list = list;
		this.start = start;
		this.emojiBgRes = bgres;
		this.delRes = delres;
		initLayout(context);
	}

	private void initLayout(Context context) {
		for (int l = 0; l < LINE; l++) {
			LayoutParams relativeParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.setLayoutParams(relativeParams);
			for (int i = 0; i < LINE_ITEMS; i++) {
				int position = start + i + LINE_ITEMS * l;
				RelativeLayout parent = new RelativeLayout(context);
				ImageView iv = new ImageView(context);
				ImageView iv_mask = new ImageView(context);
				iv.setScaleType(ScaleType.FIT_CENTER);
				iv_mask.setScaleType(ScaleType.FIT_XY);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						(getScreenWidth() / LINE_ITEMS),
						(getScreenWidth() / LINE_ITEMS));
				lp.setMargins(0, getScaleLength(15), 0, 0);
				parent.setLayoutParams(lp);
				parent.setBackgroundResource(emojiBgRes);
				parent.setBackgroundColor(Color.TRANSPARENT);
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
						(getScreenWidth() / LINE_ITEMS)
								- getScaleLength(MARGIN_HORIZONTAL) * 2,
						(getScreenWidth() / LINE_ITEMS)
								- getScaleLength(MARGIN_HORIZONTAL) * 2);
				iv.setEnabled(false);
				iv.setClickable(false);
				if ((position) % (LINE * LINE_ITEMS) == (LINE * LINE_ITEMS) - 1) {
					iv.setImageResource(delRes);
				} else if (position < REALY_COUNT) {
					if (position < (LINE * LINE_ITEMS - 1)) {
						iv.setImageResource((Integer) list.get(position).get(
								"resId"));
					} else if (position < (LINE * LINE_ITEMS * 2 - 1)) {
						iv.setImageResource((Integer) list.get(position - 1)
								.get("resId"));
					} else if (position < (LINE * LINE_ITEMS * 3 - 1)) {
						iv.setImageResource((Integer) list.get(position - 2)
								.get("resId"));
					} else if (position < (LINE * LINE_ITEMS * 4 - 1)) {
						iv.setImageResource((Integer) list.get(position - 3)
								.get("resId"));
					} else {
						iv.setImageResource((Integer) list.get(position - 4)
								.get("resId"));
					}
				}
				iv.setLayoutParams(lp2);
				iv_mask.setLayoutParams(lp2);
				iv_mask.setVisibility(View.GONE);
				parent.addView(iv_mask);
				parent.addView(iv);
				parent.setTag(position);
				if (position < REALY_COUNT || position == LAST_POSITION) {
					parent.setOnTouchListener(this);
					parent.setOnClickListener(this);
				}
				linearLayout.addView(parent);
			}
			this.addView(linearLayout);
		}
	}

	public void maskReset() {
		int c = getChildCount();
		for (int i = 0; i < c; i++) {
			View v = getChildAt(i);
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				int count = ((ViewGroup) getChildAt(0)).getChildCount();
				for (int j = 0; j < count; j++) {
					if (((ViewGroup) vg.getChildAt(j)) == null)
						return;
					((ViewGroup) vg.getChildAt(j))
							.setBackgroundColor(Color.TRANSPARENT);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (onIEmojiPanelItemClickListener != null && v.getTag() != null) {
			// maskReset();
			onIEmojiPanelItemClickListener.onIEmojiPanelItemClick((Integer) v
					.getTag());
			// ((ViewGroup) v).getChildAt(0).setVisibility(View.VISIBLE);
		}
	}

	public void setOnMyClick(OnIEmojiPanelItemClickListener onMyClick) {
		this.onIEmojiPanelItemClickListener = onMyClick;
	}

	public int getScreenWidth() {
		if (screenWidth == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			dm = getContext().getResources().getDisplayMetrics();
			screenWidth = dm.widthPixels;
		}
		return screenWidth;
	}

	public int getScaleLength(int length) {
		return length * getScreenWidth() / screenWidthDefault;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v instanceof RelativeLayout) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(emojiBgRes);
			} else {
				v.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		return false;
	}

}
