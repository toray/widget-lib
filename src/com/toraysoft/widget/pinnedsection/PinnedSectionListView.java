/*
 * Copyright (C) 2013 Sergej Shafarenka, halfbit.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file kt in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.toraysoft.widget.pinnedsection;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.toraysoft.widget.pinnedsection.PinnedSectionListConnector.PositionMetadata;

/**
 * ListView, which is capable to pin section views at its top while the rest is
 * still scrolled.
 */
public class PinnedSectionListView extends ListView {

	public static final int PACKED_POSITION_TYPE_GROUP = 0;
	public static final int PACKED_POSITION_TYPE_CHILD = 1;
	public static final int PACKED_POSITION_TYPE_NULL = 2;
	public static final long PACKED_POSITION_VALUE_NULL = 0x00000000FFFFFFFFL;
	private static final long PACKED_POSITION_MASK_CHILD = 0x00000000FFFFFFFFL;
	private static final long PACKED_POSITION_MASK_TYPE = 0x8000000000000000L;
	private static final long PACKED_POSITION_MASK_GROUP = 0x7FFFFFFF00000000L;
	private static final long PACKED_POSITION_SHIFT_GROUP = 32;
	private static final long PACKED_POSITION_SHIFT_TYPE = 63;
	private static final long PACKED_POSITION_INT_MASK_CHILD = 0xFFFFFFFF;
	private static final long PACKED_POSITION_INT_MASK_GROUP = 0x7FFFFFFF;

	public static final int SYNC_MAX_DURATION_MILLIS = 100;

	private PinnedSectionListConnector mConnector;

	private PinnedSectionListAdapter mAdapter;

	OnSectionExpandListener mOnSectionExpandListener;

	// -- inner classes

	/**
	 * List adapter to be implemented for being used with PinnedSectionListView
	 * adapter.
	 */
	public static interface PinnedSectionListAdapter2 extends ListAdapter {
		/**
		 * This method shall return 'true' if views of given type has to be
		 * pinned.
		 */
		boolean isItemViewTypePinned(int viewType);
	}

	/** Wrapper class for pinned section view and its position in the list. */
	static class PinnedSection {
		public View view;
		public int position;
		public long id;
	}

	// -- class fields

	// fields used for handling touch events
	private final Rect mTouchRect = new Rect();
	private final PointF mTouchPoint = new PointF();
	private int mTouchSlop;
	private View mTouchTarget;
	private MotionEvent mDownEvent;

	// fields used for drawing shadow under a pinned section
	private GradientDrawable mShadowDrawable;
	private int mSectionsDistanceY;
	private int mShadowHeight;

	/** Delegating listener, can be null. */
	OnScrollListener mDelegateOnScrollListener;

	/** Shadow for being recycled, can be null. */
	PinnedSection mRecycleSection;

	/** shadow instance with a pinned view, can be null. */
	PinnedSection mPinnedSection;

	/**
	 * Pinned view Y-translation. We use it to stick pinned view to the next
	 * section.
	 */
	int mTranslateY;

	/** Scroll listener which does the magic */
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
			
			final boolean isFirstVisibleItemSection = isItemViewTypePinned(
					firstVisibleItem);
			
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
		position = position-getHeaderViewsCount();
		final PositionMetadata posMetadata = mConnector
				.getUnflattenedPos(position);
		if (posMetadata.position.type 
				== PinnedSectionListPosition.GROUP && posMetadata.isExpanded()) {
			return true;
		}else{
			return false;
		}
	}
	
	private boolean isItemViewTypeUnPinned(int position){
		position = position-getHeaderViewsCount();
		final PositionMetadata posMetadata = mConnector
				.getUnflattenedPos(position);
		if (posMetadata.position.type 
				== PinnedSectionListPosition.GROUP && !posMetadata.isExpanded()) {
			return true;
		}else{
			return false;
		}
	}

	/** Default change observer. */
	private final DataSetObserver mDataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			recreatePinnedShadow();
		};

		@Override
		public void onInvalidated() {
			recreatePinnedShadow();
		}
	};

	// -- constructors

	public PinnedSectionListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public PinnedSectionListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		setOnScrollListener(mOnScrollListener);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		initShadow(true);
	}

	// -- public API methods

	public void setShadowVisible(boolean visible) {
		initShadow(visible);
		if (mPinnedSection != null) {
			View v = mPinnedSection.view;
			invalidate(v.getLeft(), v.getTop(), v.getRight(), v.getBottom()
					+ mShadowHeight);
		}
	}

	// -- pinned section drawing methods

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

	/** Create shadow wrapper with a pinned view for a view at given position */
	void createPinnedShadow(int position) {

		// try to recycle shadow
		PinnedSection pinnedShadow = mRecycleSection;
		mRecycleSection = null;

		// create new shadow, if needed
		if (pinnedShadow == null)
			pinnedShadow = new PinnedSection();
		// request new view using recycled view, if such
		View pinnedView = getAdapter().getView(position, pinnedShadow.view,
				PinnedSectionListView.this);

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

	/** Destroy shadow wrapper for currently pinned view */
	void destroyPinnedShadow() {
		if (mPinnedSection != null) {
			// keep shadow for being recycled later
			mRecycleSection = mPinnedSection;
			mPinnedSection = null;
		}
	}

	/** Makes sure we have an actual pinned shadow for given position. */
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
//		ListAdapter adapter = getAdapter();
		for (int childIndex = 0; childIndex < visibleItemCount; childIndex++) {
			int position = firstVisibleItem + childIndex;
			//TODO section 
//			int viewType = adapter.getItemViewType(position);
//			if (isItemViewTypePinned(adapter, viewType))
//				return position;
			if(isItemViewTypePinned2(position))
				return position;
		}
		return -1;
	}
	
	boolean isItemViewTypePinned2(int position){
		position = position-getHeaderViewsCount();
		final PositionMetadata posMetadata = mConnector
				.getUnflattenedPos(position);
		if (posMetadata.position.type 
				== PinnedSectionListPosition.GROUP) {
			return true;
		}else{
			return false;
		}
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
			if(isItemViewTypeUnPinned(itemPosition))
				return -1;
			if(isItemViewTypePinned(itemPosition)) 
				return itemPosition;
		}

		// try slow way by looking through to the next section item above
		for (int position = fromPosition; position >= 0; position--) {
			//TODO section isExpanded
//			int viewType = adapter.getItemViewType(position);
//			if (isItemViewTypePinned(adapter, viewType))
//				return position;
			if(isItemViewTypeUnPinned(position))
				return -1;
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

	@Override
	public void setOnScrollListener(OnScrollListener listener) {
		if (listener == mOnScrollListener) {
			super.setOnScrollListener(listener);
		} else {
			mDelegateOnScrollListener = listener;
		}
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		super.onRestoreInstanceState(state);
		post(new Runnable() {
			@Override
			public void run() { // restore pinned view after configuration
								// change
				recreatePinnedShadow();
			}
		});
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// unregister observer at old adapter and register on new one
		ListAdapter oldAdapter = getAdapter();
		if (oldAdapter != null)
			oldAdapter.unregisterDataSetObserver(mDataSetObserver);
		if (adapter != null)
			adapter.registerDataSetObserver(mDataSetObserver);

		// destroy pinned shadow, if new adapter is not same as old one
		if (oldAdapter != adapter)
			destroyPinnedShadow();

		super.setAdapter(adapter);

	}
	
	
	public void setAdapter(PinnedSectionListAdapter adapter) {
        // Set member variable
        mAdapter = adapter;
        
        if (adapter != null) {
            // Create the connector
            mConnector = new PinnedSectionListConnector(adapter);
        } else {
            mConnector = null;
        }
        
        // Link the ListView (superclass) to the expandable list data through the connector
        super.setAdapter(mConnector);
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mPinnedSection != null) {
			int parentWidth = r - l - getPaddingLeft() - getPaddingRight();
			int shadowWidth = mPinnedSection.view.getWidth();
			if (parentWidth != shadowWidth) {
				recreatePinnedShadow();
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

	// -- touch handling methods

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

//		OnItemClickListener listener = getOnItemClickListener();
//		if (listener != null) {
//			View view = mPinnedSection.view;
//			playSoundEffect(SoundEffectConstants.CLICK);
//			if (view != null) {
//				view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
//			}
//			listener.onItemClick(this, view, mPinnedSection.position,
//					mPinnedSection.id);
//			return true;
//		}
		final PositionMetadata posMetadata = mConnector
				.getUnflattenedPos(mPinnedSection.position);
		if (posMetadata.isExpanded()) {
			/* Collapse it */
			mConnector.collapseGroup(posMetadata);

			playSoundEffect(SoundEffectConstants.CLICK);

			if (mOnSectionExpandListener != null) {
				mOnSectionExpandListener
						.onSectionCollapse(posMetadata.position.groupPos);
			}
		} else {
			/* Expand it */
			mConnector.expandGroup(posMetadata);

			playSoundEffect(SoundEffectConstants.CLICK);

			if (mOnSectionExpandListener != null) {
				mOnSectionExpandListener
						.onSectionExpand(posMetadata.position.groupPos);
			}

			final int groupPos = posMetadata.position.groupPos;
			final int groupFlatPos = posMetadata.position.flatListPos;

			final int shiftedGroupPosition = groupFlatPos
					+ getHeaderViewsCount();
			smoothScrollToPosition(
					shiftedGroupPosition
							+ mAdapter.getChildrenCount(groupPos),
					shiftedGroupPosition);
		}
		posMetadata.recycle();
		return false;
	}

	public static boolean isItemViewTypePinned(ListAdapter adapter, int viewType) {
		if (adapter instanceof HeaderViewListAdapter) {
			adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
		}
		if(adapter instanceof PinnedSectionListAdapter2){
			return ((PinnedSectionListAdapter2)adapter).isItemViewTypePinned(viewType);
		}
		return ((PinnedSectionListAdapter)((PinnedSectionListConnector)adapter).getAdapter())
				.isItemViewTypePinned(viewType);
	}

	public static long getPackedPositionForChild(int groupPosition,
			int childPosition) {
		return (((long) PACKED_POSITION_TYPE_CHILD) << PACKED_POSITION_SHIFT_TYPE)
				| ((((long) groupPosition) & PACKED_POSITION_INT_MASK_GROUP) << PACKED_POSITION_SHIFT_GROUP)
				| (childPosition & PACKED_POSITION_INT_MASK_CHILD);
	}

	public static long getPackedPositionForGroup(int groupPosition) {
		return ((((long) groupPosition) & PACKED_POSITION_INT_MASK_GROUP) << PACKED_POSITION_SHIFT_GROUP);
	}

	public static int getPackedPositionType(long packedPosition) {
		if (packedPosition == PACKED_POSITION_VALUE_NULL) {
			return PACKED_POSITION_TYPE_NULL;
		}

		return (packedPosition & PACKED_POSITION_MASK_TYPE) == PACKED_POSITION_MASK_TYPE ? PACKED_POSITION_TYPE_CHILD
				: PACKED_POSITION_TYPE_GROUP;
	}

	public static int getPackedPositionGroup(long packedPosition) {

		if (packedPosition == PACKED_POSITION_VALUE_NULL)
			return -1;

		return (int) ((packedPosition & PACKED_POSITION_MASK_GROUP) >> PACKED_POSITION_SHIFT_GROUP);
	}

	public static int getPackedPositionChild(long packedPosition) {
		// Null
		if (packedPosition == PACKED_POSITION_VALUE_NULL)
			return -1;

		// Group since a group type clears this bit
		if ((packedPosition & PACKED_POSITION_MASK_TYPE) != PACKED_POSITION_MASK_TYPE)
			return -1;

		return (int) (packedPosition & PACKED_POSITION_MASK_CHILD);
	}

	private boolean isHeaderOrFooterPosition(int position) {
		int mItemCount = getCount();
		final int footerViewsStart = mItemCount - getFooterViewsCount();
		return (position < getHeaderViewsCount() || position >= footerViewsStart);
	}

	private int getFlatPositionForConnector(int flatListPosition) {
		return flatListPosition - getHeaderViewsCount();
	}

	@Override
	public boolean performItemClick(View view, int position, long id) {
		if (isHeaderOrFooterPosition(position)) {
			return super.performItemClick(view, position, id);
		}
		final int adjustedPosition = getFlatPositionForConnector(position);
//		super.performItemClick(view, position, id);
		return handleItemClick(view, adjustedPosition, id);
		// return super.performItemClick(view, position, id);
	}

	boolean handleItemClick(View v, int position, long id) {
		final PositionMetadata posMetadata = mConnector
				.getUnflattenedPos(position);
		id = getChildOrGroupId(posMetadata.position);
		boolean returnValue;
		if (posMetadata.position.type == PinnedSectionListPosition.GROUP) {
			// TODO section click
			// if (mOnSectionExpandListener != null) {
			// if (mOnSectionExpandListener.onSectionClick(this, v,
			// posMetadata.position.groupPos, id)) {
			// posMetadata.recycle();
			// return true;
			// }
			// }

			if (posMetadata.isExpanded()) {
				/* Collapse it */
				mConnector.collapseGroup(posMetadata);

				playSoundEffect(SoundEffectConstants.CLICK);

				if (mOnSectionExpandListener != null) {
					mOnSectionExpandListener
							.onSectionCollapse(posMetadata.position.groupPos);
				}
			} else {
				/* Expand it */
				mConnector.expandGroup(posMetadata);

				playSoundEffect(SoundEffectConstants.CLICK);

				if (mOnSectionExpandListener != null) {
					mOnSectionExpandListener
							.onSectionExpand(posMetadata.position.groupPos);
				}

				final int groupPos = posMetadata.position.groupPos;
				final int groupFlatPos = posMetadata.position.flatListPos;

				final int shiftedGroupPosition = groupFlatPos
						+ getHeaderViewsCount();
				smoothScrollToPosition(
						shiftedGroupPosition
								+ mAdapter.getChildrenCount(groupPos),
						shiftedGroupPosition);
			}
			returnValue = true;
		} else {
			if (mOnSectionExpandListener != null) {
                playSoundEffect(SoundEffectConstants.CLICK);
                return mOnSectionExpandListener.onChildClick(this, v, posMetadata.position.groupPos,
                        posMetadata.position.childPos, id);
            }
			returnValue = false;
		}
		posMetadata.recycle();
		return returnValue;
	}

	private long getChildOrGroupId(PinnedSectionListPosition position) {
		if (position.type == PinnedSectionListPosition.CHILD) {
			return mAdapter.getChildId(position.groupPos, position.childPos);
		} else {
			return mAdapter.getGroupId(position.groupPos);
		}
	}

	public void setOnSectionExpandListener(OnSectionExpandListener l) {
		this.mOnSectionExpandListener = l;
	}
	
	public void notifyDataSetChanged(){
		if(mConnector!=null)
			mConnector.notifyDataSetChanged();
	}
}
