package com.toraysoft.widget.navigation;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;

public abstract class NavigationAdapter {
	
	DataSetObservable mDataSetObservable = new DataSetObservable();
	
	public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }
		 
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }
    
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

	public abstract int getCount();
	
	public abstract Object getItem(int position);
	
	public abstract View getView(int position);
	
}
