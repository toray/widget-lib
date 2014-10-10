package com.toraysoft.widget.simple;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.toraysoft.widget.R;

public class SpeechButton extends Button {
	
	String textDefault;
	String textSelect;
	String textCancel;

	int l = 0;
	int r = 0;
	int t = 0;
	int b = 0;
	int height = 0;
	int width = 0;
	boolean isLongClick = false;
	boolean isCancel = false;
	
	OnSpeechListener mOnSpeechListener;

	public SpeechButton(Context context) {
		super(context);
		init();
	}

	public SpeechButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public SpeechButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	private void init(AttributeSet attrs){
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.SpeechButton);  
		textDefault = typedArray.getString(R.styleable.SpeechButton_textDefault);
		textSelect = typedArray.getString(R.styleable.SpeechButton_textSelect);
		textCancel = typedArray.getString(R.styleable.SpeechButton_textCancel);
		typedArray.recycle();   
		init();
	}

	private void init() {
		if(textDefault!=null)
			setText(textDefault);
		
		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				isLongClick = true;
				isCancel = false;
				if(textSelect!=null)
					setText(textSelect);
				if(mOnSpeechListener!=null){
					mOnSpeechListener.onSpeechStart();
				}
				return true;
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isLongClick) {
			if(isCancel) {
				if(textDefault!=null)
					setText(textDefault);
				event.setAction(MotionEvent.ACTION_CANCEL);
				isLongClick = false;
				return super.onTouchEvent(event);
			}
			float x = event.getX()+l;
			float y = event.getY()+t;
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				isLongClick = false;
				if(textDefault!=null)
					setText(textDefault);
				if(mOnSpeechListener!=null){
					if(x>=l && x<=r && y>=t-height*3 && y<=b){
						mOnSpeechListener.onSpeechFinish();
					}else{
						mOnSpeechListener.onSpeechCancel();
					}
					
				}
				return super.onTouchEvent(event);
			case MotionEvent.ACTION_MOVE:
				if(x>=l && x<=r && y>=t-height*3 && y<=b){
					if(textSelect!=null)
						setText(textSelect);
				}else{
					if(textCancel!=null)
						setText(textCancel);
				}
				break;
			default:
				break;
			}
			return false;
		}
		return super.onTouchEvent(event);
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		l = left;
		t = top;
		r = right;
		b = bottom;
		width = right - left;
		height = bottom - top;
	}
	
	public void cancel(){
		this.isCancel = true;
	}
	
	public void setOnSpeechListener(OnSpeechListener mOnSpeechListener) {
		this.mOnSpeechListener = mOnSpeechListener;
	}
	
	public interface OnSpeechListener{
		void onSpeechStart();
		void onSpeechCancel();
		void onSpeechFinish();
	}

}
