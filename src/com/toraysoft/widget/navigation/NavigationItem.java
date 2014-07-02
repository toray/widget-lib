package com.toraysoft.widget.navigation;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class NavigationItem {
	private int layoutResID;
	private int width;
	private int height;
	private Bitmap icon;
	private int icon_width;
	private int icon_height;
	private ImageView iv_icon;
	private Object tag;
	private int bgColor;

	public NavigationItem(int layoutResID, int width, int height) {
		this.layoutResID = layoutResID;
		this.width = width;
		this.height = height;
	}

	public void setIcon(Bitmap icon, int width, int height) {
		this.icon = icon;
		this.icon_width = width;
		this.icon_height = height;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public Object getTag() {
		return tag;
	}

	public int getLayoutResID() {
		return layoutResID;
	}

	public void setImageIcon(ImageView iv_icon) {
		this.iv_icon = iv_icon;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setIcon(Bitmap bitmap) {
		this.icon = bitmap;
		if (iv_icon != null)
			iv_icon.setImageBitmap(bitmap);
	}

	public Bitmap getIcon() {
		return icon;
	}

	public int getIcon_width() {
		return icon_width;
	}

	public int getIcon_height() {
		return icon_height;
	}

	public int getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}

}
