package com.toraysoft.widget.anim;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class FrameImageView extends ImageView{

		public AnimationDrawable animationDrawable;
		private Field field; // 领域对象

		/**
		 * 初始化数据
		 * 
		 * @param context
		 * @param attrs
		 */
		public FrameImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

//		/**
//		 * 画一次图调用一次 功能：用于监听每帧动画的播放状态
//		 */
//		protected void onDraw(Canvas canvas) {
//			super.onDraw(canvas);
//			Log.i("msg", "onDraw()...");
//			try {
//				field = AnimationDrawable.class.getDeclaredField("mCurFrame");
//				field.setAccessible(true); // 设置可以访问
//				// 获取mCurFrame变量的当前值
//				int curFrame = field.getInt(animationDrawable);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		@Override
		protected void onFinishInflate() {
			super.onFinishInflate();
			animationDrawable = (AnimationDrawable)getBackground();
			if(animationDrawable!=null)
				animationDrawable.start();
		}
}	
