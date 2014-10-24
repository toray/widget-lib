package com.toraysoft.widget.alwaysmarqueetextview;

import android.content.Context;
import android.util.AttributeSet;

import com.rockerhieu.emojicon.EmojiconTextView;

public class AlwaysMarqueeEmojiTextView extends EmojiconTextView {

	public AlwaysMarqueeEmojiTextView(Context context) {
		super(context);
	}

	public AlwaysMarqueeEmojiTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlwaysMarqueeEmojiTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
