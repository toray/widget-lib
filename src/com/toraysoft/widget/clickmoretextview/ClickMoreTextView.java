package com.toraysoft.widget.clickmoretextview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ClickMoreTextView extends LinearLayout implements OnClickListener {

	/** default text show max lines */
	private static final int DEFAULT_MAX_LINE_COUNT = 3;

	private static final int COLLAPSIBLE_STATE_NONE = 0;
	private static final int COLLAPSIBLE_STATE_SHRINKUP = 1;
	private static final int COLLAPSIBLE_STATE_SPREAD = 2;

	private TextView desc;
	private TextView descOp;

	private String shrinkup;
	private String spread;
	private int mState;
	private boolean flag;

	public ClickMoreTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ClickMoreTextView(Context context) {
		super(context);
	}

	public void init(int textNorColor, int textMorColor, int textNorSize,
			int textMorSize) {
		this.setOrientation(LinearLayout.VERTICAL);
		removeAllViews();
		desc = new TextView(getContext());
		desc.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		desc.setTextColor(textNorColor);
		desc.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textNorSize);
		descOp = new TextView(getContext());
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.RIGHT;
		descOp.setLayoutParams(lp);
		descOp.setTextColor(textMorColor);
		descOp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textMorSize);
		descOp.setOnClickListener(this);
		addView(desc);
		addView(descOp);
	}

	public void setTextTips(String open, String close) {
		spread = open;
		shrinkup = close;
	}

	@Override
	public void onClick(View v) {
		flag = false;
		requestLayout();
	}

	public final void setDesc(String txt) {
		if (desc != null)
			desc.setText(txt);
		mState = COLLAPSIBLE_STATE_SPREAD;
		flag = false;
		requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!flag) {
			flag = true;
			if (desc.getLineCount() <= DEFAULT_MAX_LINE_COUNT) {
				mState = COLLAPSIBLE_STATE_NONE;
				descOp.setVisibility(View.GONE);
				desc.setMaxLines(DEFAULT_MAX_LINE_COUNT + 1);
			} else {
				post(new InnerRunnable());
			}
		}
	}

	class InnerRunnable implements Runnable {
		@Override
		public void run() {
			if (mState == COLLAPSIBLE_STATE_SPREAD) {
				desc.setMaxLines(DEFAULT_MAX_LINE_COUNT);
				descOp.setVisibility(View.VISIBLE);
				descOp.setText(spread);
				mState = COLLAPSIBLE_STATE_SHRINKUP;
			} else if (mState == COLLAPSIBLE_STATE_SHRINKUP) {
				desc.setMaxLines(Integer.MAX_VALUE);
				descOp.setVisibility(View.VISIBLE);
				descOp.setText(shrinkup);
				mState = COLLAPSIBLE_STATE_SPREAD;
			}
		}
	}
}
