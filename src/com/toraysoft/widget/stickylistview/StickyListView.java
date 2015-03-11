package com.toraysoft.widget.stickylistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class StickyListView extends ListView{
	
//	private OnStickyHeadItemChangeListener onStickyHeadItemChangeListener;
	
	private int lastVisibleItem = 0;
	private int marginTop = 0;
	private int stickyIndex = 1;
	private View sticky;
	private OnScrollListener mOnScrollListener;
	private int lastTop = 0;
	
	public StickyListView(Context context) {
		super(context);
		init();
	}

	public StickyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public StickyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}	
	
	private void init(){
		super.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(mOnScrollListener!=null){
					mOnScrollListener.onScrollStateChanged(view, scrollState);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(marginTop==0){
					if(lastVisibleItem!=firstVisibleItem && lastVisibleItem<=(stickyIndex-1)
							&& firstVisibleItem>=stickyIndex){
						if(sticky!=null)
							sticky.setVisibility(View.VISIBLE);
					}
					if(lastVisibleItem!=firstVisibleItem 
									&& lastVisibleItem>=stickyIndex 
											&& firstVisibleItem<=(stickyIndex-1)){
						if(sticky!=null)
							sticky.setVisibility(View.GONE);
					}
				}else if(marginTop<0){
					if(getChildCount()>stickyIndex-1){
						int point = -getChildAt(0).getMeasuredHeight()-marginTop;
						if((lastTop>=point || lastVisibleItem!=firstVisibleItem) 
								&& firstVisibleItem>=(stickyIndex-1)){
							if(sticky!=null)
								sticky.setVisibility(View.VISIBLE);
						}
						if(firstVisibleItem<=(stickyIndex-1) && getChildAt(stickyIndex-1).getTop()>=point
								){
							if(sticky!=null)
								sticky.setVisibility(View.GONE);
						}
						lastTop = getChildAt(stickyIndex-1).getTop();
					}
				}
				lastVisibleItem = firstVisibleItem;
				if(mOnScrollListener!=null){
					mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				}
			}
		});
		/*this.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(lastVisibleItem!=firstVisibleItem && lastVisibleItem<=(stickyIndex-1)
												&& firstVisibleItem>=stickyIndex){
//					if(onStickyHeadItemChangeListener!=null){
//						onStickyHeadItemChangeListener.OnStickyHeadItemChange(true);
//					}
					if(sticky!=null)
						sticky.setVisibility(View.VISIBLE);
				}
				if(lastVisibleItem!=firstVisibleItem && lastVisibleItem>=stickyIndex 
												&& firstVisibleItem<=(stickyIndex-1)){
//					if(onStickyHeadItemChangeListener!=null){
//						onStickyHeadItemChangeListener.OnStickyHeadItemChange(false);
//					}
					if(sticky!=null)
						sticky.setVisibility(View.GONE);
				}
				lastVisibleItem = firstVisibleItem;
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
		});*/
	}
	
	public void resume(){
//		int firstVisibleItem = getFirstVisiblePosition();
//		Log.v("resume firstVisibleItem",""+firstVisibleItem);
//		if(marginTop==0){
//			if(lastVisibleItem!=firstVisibleItem && lastVisibleItem<=(stickyIndex-1)
//					&& firstVisibleItem>=stickyIndex){
//				if(sticky!=null)
//					sticky.setVisibility(View.VISIBLE);
//			}
//			if(lastVisibleItem!=firstVisibleItem 
//							&& lastVisibleItem>=stickyIndex 
//									&& firstVisibleItem<=(stickyIndex-1)){
//				if(sticky!=null)
//					sticky.setVisibility(View.GONE);
//			}
//		}else if(marginTop<0){
//			if(getChildCount()>stickyIndex-1){
//				int point = -getChildAt(0).getMeasuredHeight()-marginTop;
//				if((lastTop>=point || lastVisibleItem!=firstVisibleItem) 
//						&& firstVisibleItem>=(stickyIndex-1)){
//					if(sticky!=null)
//						sticky.setVisibility(View.VISIBLE);
//				}
//				if(firstVisibleItem<=(stickyIndex-1) && getChildAt(stickyIndex-1).getTop()>=point
//						){
//					if(sticky!=null)
//						sticky.setVisibility(View.GONE);
//				}
//				lastTop = getChildAt(stickyIndex-1).getTop();
//			}
//		}
	}
	
	public void setStickyView(View sticky){
		this.sticky = sticky;
	}
	
	public void setStickyIndex(int stickyIndex){
		this.stickyIndex = stickyIndex;
	}

	public void setMarginTop(int marginTop) {
		this.marginTop = marginTop;
	}

	@Override
	public void setOnScrollListener(final OnScrollListener onScrollListener) {
//		super.setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				if(onScrollListener!=null){
//					onScrollListener.onScrollStateChanged(view, scrollState);
//				}
//				mOnScrollListener.onScrollStateChanged(view, scrollState);
//			}
//
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				if(onScrollListener!=null){
//					onScrollListener.onScroll(view, firstVisibleItem,
//							visibleItemCount, totalItemCount);
//				}
//				mOnScrollListener.onScroll(view, firstVisibleItem, 
//						visibleItemCount, totalItemCount);
//			}
//		});
		this.mOnScrollListener = onScrollListener;
	}
	
	

	
//	public void setOnStickyHeadItemChangeListener(
//			OnStickyHeadItemChangeListener onStickyHeadItemChangeListener) {
//		this.onStickyHeadItemChangeListener = onStickyHeadItemChangeListener;
//	}
//	
//	
//
//	public interface OnStickyHeadItemChangeListener{
//		public void OnStickyHeadItemChange(boolean showHeader);
//	}
}
