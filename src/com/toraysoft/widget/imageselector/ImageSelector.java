package com.toraysoft.widget.imageselector;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toraysoft.widget.R;
import com.toraysoft.widget.imageselector.ListImageDirPopupWindow.OnImageDirSelected;

public class ImageSelector extends Fragment implements OnImageDirSelected {

	private static final String TAG = "ImageSelector";
	private View rootView;
	boolean isInit;
	private ProgressDialog mProgressDialog;
	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;

	private GridView mGirdView;
	private ImageSelectorAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;
	private Button btn_send;
	private View view_mask;
	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;
	
	private OnImageSelectorListener mOnImageSelectorListener;
	private boolean isMuti;
	
	public ImageSelector() {
	}
	
	public ImageSelector(boolean isMuti) {
		this.isMuti = isMuti;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mProgressDialog.dismiss();
			// 为View绑定数据
			data2View();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
			// 没有扫描到图片
			Toast.makeText(getActivity(), getActivity().getString(R.string.imageselector_text_load_fail), Toast.LENGTH_SHORT)
					.show();
			return;
		}

		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));

		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new ImageSelectorAdapter(getActivity(), mImgs,
				R.layout.imageselector_item, mImgDir.getAbsolutePath(), isMuti);
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(totalCount + getActivity().getString(R.string.imageselector_text_page));
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.from(getActivity()).inflate(
				R.layout.imageselector_fragment, null);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		initView();
		getImages();
		initEvent();
		return rootView;
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(getActivity(), getActivity().getString(R.string.imageselector_text_no_sdcard), Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(getActivity(), null, getActivity().getString(R.string.imageselector_text_loading));
		mProgressDialog.setCanceledOnTouchOutside(true);

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = getActivity()
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		mGirdView = (GridView) rootView.findViewById(R.id.id_gridView);
		mChooseDir = (TextView) rootView.findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) rootView.findViewById(R.id.id_total_count);

		mBottomLy = (RelativeLayout) rootView.findViewById(R.id.id_bottom_ly);
		view_mask = (View) rootView.findViewById(R.id.view_imgselect_mask);
		btn_send = (Button) rootView.findViewById(R.id.btn_imgselector_send);
	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
				// 设置背景颜色变暗
				view_mask.setVisibility(View.VISIBLE);
			}
		});
		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<String> imgPaths = mAdapter.mSelectedImage;
				if (imgPaths == null || imgPaths.size() == 0) {
					Toast.makeText(getActivity(), getActivity().getString(R.string.imageselector_text_choice_pic), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if(mOnImageSelectorListener != null) {
					mOnImageSelectorListener.onImageSelected(imgPaths);
				}
			}
		});
	}

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.4),
				mImageFloders, LayoutInflater.from(getActivity()).inflate(
						R.layout.imageselector_view_list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				view_mask.setVisibility(View.GONE);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	public void selected(ImageFloder floder) {

		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new ImageSelectorAdapter(getActivity(), mImgs,
				R.layout.imageselector_item, mImgDir.getAbsolutePath(), isMuti);
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + getActivity().getString(R.string.imageselector_text_page));
		mChooseDir.setText(getActivity().getString(R.string.imageselector_text_current_path) + floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	public OnImageSelectorListener getmOnImageSelectorListener() {
		return mOnImageSelectorListener;
	}

	public void setmOnImageSelectorListener(
			OnImageSelectorListener l) {
		this.mOnImageSelectorListener = l;
	}
	
}
