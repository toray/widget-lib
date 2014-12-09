package com.toraysoft.widget.speech;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.toraysoft.widget.R;

public class SpeechButton extends Button{
	
	boolean isLongClick = false;
	
	String textDefault;
	String textSelect;
	
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
	
	void init(){
		if(textDefault!=null)
			setText(textDefault);
		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				isLongClick = true;
				if(textSelect!=null)
					setText(textSelect);
				if(mOnSpeechListener!=null){
					mOnSpeechListener.onSpeechStart();
				}
				return true;
			}
		});
	}
	
	void init(AttributeSet attrs){
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.SpeechButton);  
		textDefault = typedArray.getString(R.styleable.SpeechButton_textDefault);
		textSelect = typedArray.getString(R.styleable.SpeechButton_textSelect);
		typedArray.recycle();   
		init();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isLongClick) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				isLongClick = false;
				if(textDefault!=null)
					setText(textDefault);
				if(mOnSpeechListener!=null){
					mOnSpeechListener.onSpeechFinish();
				}
				return super.onTouchEvent(event);
			case MotionEvent.ACTION_MOVE:
				break;
			default:
				break;
			}
			return false;
		}
		return super.onTouchEvent(event);
	}
	
	public void cancel(){
		isLongClick = false;
		if(textDefault!=null)
			setText(textDefault);
	}
	
	public void setOnSpeechListener(OnSpeechListener l){
		mOnSpeechListener = l;
	}
	
	public interface OnSpeechListener{
		void onSpeechStart();
		void onSpeechFinish();
	}
}
