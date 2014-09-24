package com.toraysoft.widget.tabbar;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

public class TabItem {
	int index = -1;
	int layoutResID;
	String title;
	Drawable icon;
	Bitmap thumb;
	ImageView iv_icon;
	TextView tv_title;
	boolean selected = false;
	boolean isTab = true;

	public TabItem(Drawable icon) {
		this.icon = icon;
	}
	
	public TabItem(Drawable icon,boolean isTab) {
		this.icon = icon;
		this.isTab = isTab;
	}
	
	public TabItem(String title, Drawable icon) {
		this.title = title;
		this.icon = icon;
	}

	public TabItem(String title, Drawable icon,boolean isTab) {
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
	
	public String getTitle() {
		return title;
	}
}
