package com.toraysoft.widget.tabbar;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TabItem {
	Object key;
	int index = -1;
	int layoutResID;
	String title;
	Drawable icon;
	Bitmap thumb;
	ImageView iv_icon;
	TextView tv_title,tv_unread;
	boolean selected = false;
	boolean isTab = true;

	public TabItem(Object key,Drawable icon) {
		this.key = key;
		this.icon = icon;
	}
	
	public TabItem(Object key,Drawable icon,boolean isTab) {
		this.key = key;
		this.icon = icon;
		this.isTab = isTab;
	}
	
	public TabItem(Object key,String title, Drawable icon) {
		this.key = key;
		this.title = title;
		this.icon = icon;
	}

	public TabItem(Object key,String title, Drawable icon,boolean isTab) {
		this.key = key;
		this.title = title;
		this.icon = icon;
		this.isTab = isTab;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	void setSelect(boolean selected) {
		this.selected = selected;
		if(iv_icon!=null){
			iv_icon.setSelected(selected);
			tv_title.setSelected(selected);
		}
	}
	
	public Object getKey() {
		return key;
	}
	
	public void showUnread(){
		if(tv_unread!=null){
			tv_unread.setVisibility(View.VISIBLE);
		}
	}
	
	public void hideUnread(){
		if(tv_unread!=null){
			tv_unread.setVisibility(View.GONE);
		}
	}
	
	public void showUnread(int count){
		if(tv_unread!=null){
			if(count==0){
				tv_unread.setVisibility(View.GONE);
				return;
			}
			String text = String.valueOf(count);
			if(count>99){
				text = "99+";
			}
			tv_unread.setText(text);
			tv_unread.setVisibility(View.VISIBLE);
		}
	}
}
