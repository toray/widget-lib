package com.toraysoft.widget.pinnedsection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.MeasureSpec;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;
import android.widget.AbsListView.LayoutParams;

import com.toraysoft.widget.pinnedsection.PinnedSectionListConnector.PositionMetadata;
import com.toraysoft.widget.pinnedsection.PinnedSectionListView.PinnedSection;

public class CustomExpandableListView extends ExpandableListView {

	private final Rect mTouchRect = new Rect();
	private final PointF mTouchPoint = new PointF();
	private int mTouchSlop;
	private View mTouchTarget;
	private MotionEvent mDownEvent;

	private GradientDrawable mShadowDrawable;
	private int mSectionsDistanceY;
	private int mShadowHeight;

	
	OnScrollListener mDelegateOnScrollListener;
	PinnedSection mRecycleSection;
	PinnedSection mPinnedSection;

	int mTranslateY;

	public CustomExpandableListView(Context context) {
		super(context);
		initView();
	}

	public CustomExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public CustomExpandableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	
	private void initView() {
		setOnScrollListener(mOnScrollListener);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		initShadow(true);
	}
	
	private final OnScrollListener mOnScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mDelegateOnScrollListener != null) { // delegate
				mDelegateOnScrollListener.onScrollStateChanged(view,
						scrollState);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			if (mDelegateOnScrollListener != null) { // delegate
				mDelegateOnScrollListener.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);
			}

			// get expected adapter or fail fast
			ListAdapter adapter = getAdapter();
			if (adapter == null || visibleItemCount == 0)
				return; // nothing to do
			
			//TODO section isExpanded
//			final boolean isFirstVisibleItemSection = isItemViewTypePinned(
//					adapter, adapter.getItemViewType(firstVisibleItem));
			
			final boolean isFirstVisibleItemSection = isItemViewTypePinned(firstVisibleItem);
			if (isFirstVisibleItemSection) {
				View sectionView = getChildAt(0);
				if (sectionView.getTop() == getPaddingTop()) { // view sticks to
																// the top, no
																// need for
																// pinned shadow
					destroyPinnedShadow();
				} else {    // section doesn't stick to the top, make sure we have
							// a pinned shadow
					ensureShadowForPosition(firstVisibleItem, firstVisibleItem,
							visibleItemCount);
				}

			} else { // section is not at the first visible position
				int sectionPosition = findCurrentSectionPosition(firstVisibleItem);
				if (sectionPosition > -1) { // we have section position
					ensureShadowForPosition(sectionPosition, firstVisibleItem,
							visibleItemCount);
				} else { // there is no section for the first visible item,
							// destroy shadow
					destroyPinnedShadow();
				}
			}
		};

	};
	
	private boolean isItemViewTypePinned(int position){
		int type = getPackedPositionType(position);
		
		if (type == PACKED_POSITION_TYPE_GROUP && isGroupExpanded(position)) {
			return true;
		}else{
			return false;
		}
	}
	
	void createPinnedShadow(int position) {

		// try to recycle shadow
		PinnedSection pinnedShadow = mRecycleSection;
		mRecycleSection = null;

		// create new shadow, if needed
		if (pinnedShadow == null)
			pinnedShadow = new PinnedSection();
		// request new view using recycled view, if such
		View pinnedView = getAdapter().getView(position, pinnedShadow.view,
				CustomExpandableListView.this);

		// read layout parameters
		LayoutParams layoutParams = (LayoutParams) pinnedView.getLayoutParams();
		if (layoutParams == null) { // create default layout params
			layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
		}

		int heightMode = MeasureSpec.getMode(layoutParams.height);
		int heightSize = MeasureSpec.getSize(layoutParams.height);

		if (heightMode == MeasureSpec.UNSPECIFIED)
			heightMode = MeasureSpec.EXACTLY;

		int maxHeight = getHeight() - getListPaddingTop()
				- getListPaddingBottom();
		if (heightSize > maxHeight)
			heightSize = maxHeight;

		// measure & layout
		int ws = MeasureSpec.makeMeasureSpec(getWidth() - getListPaddingLeft()
				- getListPaddingRight(), MeasureSpec.EXACTLY);
		int hs = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
		pinnedView.measure(ws, hs);

		pinnedView.layout(0, 0, pinnedView.getMeasuredWidth(),
				pinnedView.getMeasuredHeight());
		mTranslateY = 0;

		// initialize pinned shadow
		pinnedShadow.view = pinnedView;
		pinnedShadow.position = position;
		pinnedShadow.id = getAdapter().getItemId(position);

		// store pinned shadow
		mPinnedSection = pinnedShadow;
	}

	
	void destroyPinnedShadow() {
		if (mPinnedSection != null) {
			// keep shadow for being recycled later
			mRecycleSection = mPinnedSection;
			mPinnedSection = null;
		}
	}
	
	void ensureShadowForPosition(int sectionPosition, int firstVisibleItem,
			int visibleItemCount) {
		if (visibleItemCount < 2) { // no need for creating shadow at all, we
									// have a single visible item
			destroyPinnedShadow();
			return;
		}

		if (mPinnedSection != null
				&& mPinnedSection.position != sectionPosition) { // invalidate
																	// shadow,
																	// if
																	// required
			destroyPinnedShadow();
		}

		if (mPinnedSection == null) { // create shadow, if empty
			createPinnedShadow(sectionPosition);
		}

		// align shadow according to next section position, if needed
		int nextPosition = sectionPosition + 1;
		if (nextPosition < getCount()) {
			int nextSectionPosition = findFirstVisibleSectionPosition(
					nextPosition, visibleItemCount
							- (nextPosition - firstVisibleItem));
			if (nextSectionPosition-firstVisibleItem > -1) {
				View nextSectionView = getChildAt(nextSectionPosition
						- firstVisibleItem);
//				if(nextSectionView==null) return;
				final int bottom = mPinnedSection.view.getBottom()
						+ getPaddingTop();
				mSectionsDistanceY = nextSectionView.getTop() - bottom;
				if (mSectionsDistanceY < 0) {
					// next section overlaps pinned shadow, move it up
					mTranslateY = mSectionsDistanceY;
				} else {
					// next section does not overlap with pinned, stick to top
					mTranslateY = 0;
				}
			} else {
				// no other sections are visible, stick to top
				mTranslateY = 0;
				mSectionsDistanceY = Integer.MAX_VALUE;
			}
		}

	}
	
	int findFirstVisibleSectionPosition(int firstVisibleItem,
			int visibleItemCount) {
		for (int childIndex = 0; childIndex < visibleItemCount; childIndex++) {
			int position = firstVisibleItem + childIndex;
			if(getPackedPositionType(position)==PACKED_POSITION_TYPE_GROUP)
				return position;
		}
		return -1;
	}
	
	int findCurrentSectionPosition(int fromPosition) {
		ListAdapter adapter = getAdapter();
		
		if (adapter instanceof SectionIndexer) {
			
			// try fast way by asking section indexer
			SectionIndexer indexer = (SectionIndexer) adapter;
			int sectionPosition = indexer.getSectionForPosition(fromPosition);
			int itemPosition = indexer.getPositionForSection(sectionPosition);
			//TODO section isExpanded
//			int typeView = adapter.getItemViewType(itemPosition);
//			if (isItemViewTypePinned(adapter, typeView)) {
//				return itemPosition;
//			} // else, no luck
			if(isItemViewTypePinned(itemPosition)) 
				return itemPosition;
		}

		// try slow way by looking through to the next section item above
		for (int position = fromPosition; position >= 0; position--) {
			//TODO section isExpanded
//			int viewType = adapter.getItemViewType(position);
//			if (isItemViewTypePinned(adapter, viewType))
//				return position;
			if(isItemViewTypePinned(position)) 
				return position;
		}
		return -1; // no candidate found
	}

	void recreatePinnedShadow() {
		destroyPinnedShadow();
		ListAdapter adapter = getAdapter();
		if (adapter != null && adapter.getCount() > 0) {
			int firstVisiblePosition = getFirstVisiblePosition();
			int sectionPosition = findCurrentSectionPosition(firstVisiblePosition);
			if (sectionPosition == -1)
				return; // no views to pin, exit
			ensureShadowForPosition(sectionPosition, firstVisiblePosition,
					getLastVisiblePosition() - firstVisiblePosition);
		}
	}
	
	
	public void initShadow(boolean visible) {
		if (visible) {
			if (mShadowDrawable == null) {
				mShadowDrawable = new GradientDrawable(Orientation.TOP_BOTTOM,
						new int[] { Color.parseColor("#ffa0a0a0"),
								Color.parseColor("#50a0a0a0"),
								Color.parseColor("#00a0a0a0") });
				mShadowHeight = (int) (8 * getResources().getDisplayMetrics().density);
			}
		} else {
			if (mShadowDrawable != null) {
				mShadowDrawable = null;
				mShadowHeight = 0;
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mPinnedSection != null) {

			// prepare variables
			int pLeft = getListPaddingLeft();
			int pTop = getListPaddingTop();
			View view = mPinnedSection.view;

			// draw child
			canvas.save();

			int clipHeight = view.getHeight()
					+ (mShadowDrawable == null ? 0 : Math.min(mShadowHeight,
							mSectionsDistanceY));
			canvas.clipRect(pLeft, pTop, pLeft + view.getWidth(), pTop
					+ clipHeight);

			canvas.translate(pLeft, pTop + mTranslateY);
			drawChild(canvas, mPinnedSection.view, getDrawingTime());

			if (mShadowDrawable != null && mSectionsDistanceY > 0) {
				mShadowDrawable.setBounds(mPinnedSection.view.getLeft(),
						mPinnedSection.view.getBottom(),
						mPinnedSection.view.getRight(),
						mPinnedSection.view.getBottom() + mShadowHeight);
				mShadowDrawable.draw(canvas);
			}

			canvas.restore();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		final float x = ev.getX();
		final float y = ev.getY();
		final int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN && mTouchTarget == null
				&& mPinnedSection != null
				&& isPinnedViewTouched(mPinnedSection.view, x, y)) { // create
																		// touch
																		// target

			// user touched pinned view
			mTouchTarget = mPinnedSection.view;
			mTouchPoint.x = x;
			mTouchPoint.y = y;

			// copy down event for eventually be used later
			mDownEvent = MotionEvent.obtain(ev);
		}

		if (mTouchTarget != null) {
			if (isPinnedViewTouched(mTouchTarget, x, y)) { // forward event to
															// pinned view
				mTouchTarget.dispatchTouchEvent(ev);
			}

			if (action == MotionEvent.ACTION_UP) { // perform onClick on pinned
													// view
				super.dispatchTouchEvent(ev);
				performPinnedItemClick();
				clearTouchTarget();

			} else if (action == MotionEvent.ACTION_CANCEL) { // cancel
				clearTouchTarget();

			} else if (action == MotionEvent.ACTION_MOVE) {
				if (Math.abs(y - mTouchPoint.y) > mTouchSlop) {

					// cancel sequence on touch target
					MotionEvent event = MotionEvent.obtain(ev);
					event.setAction(MotionEvent.ACTION_CANCEL);
					mTouchTarget.dispatchTouchEvent(event);
					event.recycle();

					// provide correct sequence to super class for further
					// handling
					super.dispatchTouchEvent(mDownEvent);
					super.dispatchTouchEvent(ev);
					clearTouchTarget();

				}
			}

			return true;
		}

		// call super if this was not our pinned view
		return super.dispatchTouchEvent(ev);
	}

	private boolean isPinnedViewTouched(View view, float x, float y) {
		view.getHitRect(mTouchRect);

		// by taping top or bottom padding, the list performs on click on a
		// border item.
		// we don't add top padding here to keep behavior consistent.
		mTouchRect.top += mTranslateY;

		mTouchRect.bottom += mTranslateY + getPaddingTop();
		mTouchRect.left += getPaddingLeft();
		mTouchRect.right -= getPaddingRight();
		return mTouchRect.contains((int) x, (int) y);
	}

	private void clearTouchTarget() {
		mTouchTarget = null;
		if (mDownEvent != null) {
			mDownEvent.recycle();
			mDownEvent = null;
		}
	}

	private boolean performPinnedItemClick() {
		if (mPinnedSection == null)
			return false;
		if (!(getAdapter() instanceof PinnedSectionListConnector)) {
			return false;
		}
		PinnedSectionListConnector mConnector = (PinnedSectionListConnector) getAdapter();
		final PositionMetadata posMetadata = mConnector
				.getUnflattenedPos(mPinnedSection.position);
		if (posMetadata.isExpanded()) {
			/* Collapse it */
			mConnector.collapseGroup(posMetadata);

			playSoundEffect(SoundEffectConstants.CLICK);

			// if (mOnSectionExpandListener != null) {
			// mOnSectionExpandListener
			// .onSectionCollapse(posMetadata.position.groupPos);
			// }
		} else {
			/* Expand it */
			mConnector.expandGroup(posMetadata);

			playSoundEffect(SoundEffectConstants.CLICK);

			// if (mOnSectionExpandListener != null) {
			// mOnSectionExpandListener
			// .onSectionExpand(posMetadata.position.groupPos);
			// }

			final int groupPos = posMetadata.position.groupPos;
			final int groupFlatPos = posMetadata.position.flatListPos;

			final int shiftedGroupPosition = groupFlatPos
					+ getHeaderViewsCount();
			smoothScrollToPosition(
					shiftedGroupPosition
							+ ((ExpandableListAdapter) getAdapter())
									.getChildrenCount(groupPos),
					shiftedGroupPosition);
		}
		posMetadata.recycle();
		return false;
	}

	static class PinnedSection {
		public View view;
		public int position;
		public long id;
	}

}
