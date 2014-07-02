package com.toraysoft.widget.emotionpanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class EmotionPanel extends RelativeLayout implements
		OnPageChangeListener {

	private Context mContext;
	private ViewPager vp_face;
	private ArrayList<View> pageViews = new ArrayList<View>();;
	private PageIndicatorView mPageIndicatorView;
	private List<Map<String, Object>> emojis;
	private int current = 0;
	private EmojiPanelAdapter mAdapter;
	private int PAGE_ITEMS = 28;
	private int emojiBgRes = -1;
	private int delRes = -1;
	private int TMP_COUNT = 4;
	private OnIEmojiPanelItemClickListener l = null;

	public EmotionPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public EmotionPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public EmotionPanel(Context context) {
		super(context);
		this.mContext = context;
	}

	public void initDatas(List<Map<String, Object>> e) {
		this.emojis = e;
		onCreate();
	}

	private void onCreate() {
		initViews();
		initViewPager();
	}

	private void initViews() {
		vp_face = new ViewPager(mContext);
		mPageIndicatorView = new PageIndicatorView(mContext);
		this.addView(vp_face);
		this.addView(mPageIndicatorView);
	}

	private void initViewPager() {
		mAdapter = new EmojiPanelAdapter();
		vp_face.setAdapter(mAdapter);
		vp_face.setOnPageChangeListener(this);
		mPageIndicatorView.setTotalPage(mAdapter.getCount());
		mPageIndicatorView.setCurrentPage(false, current);
	}

	class EmojiPanelAdapter extends PagerAdapter {

		public EmojiPanelAdapter() {
			getView();
		}

		@Override
		public int getCount() {
			if (emojis.size() == 0) {
				return 1;
			} else {
				return ((emojis.size() + TMP_COUNT) % PAGE_ITEMS) > 0 ? ((emojis
						.size() + TMP_COUNT) / PAGE_ITEMS + 1)
						: ((emojis.size() + TMP_COUNT) / PAGE_ITEMS);
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(pageViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(pageViews.get(position));
			return pageViews.get(position);
		}

		private void getView() {
			pageViews.clear();
			for (int i = 0; i < getCount(); i++) {
				EmojiItemLayout eil = new EmojiItemLayout(mContext, emojis, i
						* PAGE_ITEMS, emojiBgRes, delRes);
				if (l != null) {
					eil.setOnMyClick(l);
				}
				pageViews.add(eil);
			}
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		current = arg0;
		mPageIndicatorView.setCurrentPage(false, current);
	}

	public void setEmojiBgRes(int emojiBgRes) {
		this.emojiBgRes = emojiBgRes;
	}

	public void setEmojiDelIcon(int delRes) {
		this.delRes = delRes;
	}

	public void setEmojiItemClickListener(OnIEmojiPanelItemClickListener ol) {
		this.l = ol;
	}

	public int getCurrent() {
		return current;
	}

}
