package com.toraysoft.widget.imageselector;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.toraysoft.widget.R;
import com.toraysoft.widget.imageselector.util.CommonAdapter;
import com.toraysoft.widget.imageselector.util.ViewHolder;

public class ImageSelectorAdapter extends CommonAdapter<String> {

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public List<String> mSelectedImage = new ArrayList<String>();
	private ImageView iv_last;
	private boolean isMuti = false;

	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	public ImageSelectorAdapter(Context context, List<String> mDatas,
			int itemLayoutId, String dirPath, boolean isMuti) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.isMuti = isMuti;
	}

	@Override
	public void convert(final ViewHolder helper, final String item) {
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image,
				R.drawable.imageselector_icon_pictures_no);
		// 设置no_selected
		helper.setImageResource(R.id.id_item_select,
				R.drawable.imageselector_icon_picture_unselected);
		// 设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);
		mSelect.setImageResource(R.drawable.imageselector_icon_pictures_selected);
		mSelect.setVisibility(View.INVISIBLE);

		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择
			@Override
			public void onClick(View v) {
				if (!isMuti) {
					if (iv_last != null) {
						iv_last.setVisibility(View.INVISIBLE);
					}
					iv_last = mSelect;
					if (mSelectedImage.contains(item)) {
						mSelectedImage.clear();
						mSelect.setVisibility(View.INVISIBLE);
					} else {
						mSelectedImage.clear();
						mSelectedImage.add(item);
						mSelect.setVisibility(View.VISIBLE);
					}
				} else {
					if (iv_last != mSelect) {
						iv_last = mSelect;
					}
					if (mSelectedImage.contains(item)) {
						mSelectedImage.remove(item);
						mSelect.setVisibility(View.INVISIBLE);
					} else {
						mSelectedImage.add(item);
						mSelect.setVisibility(View.VISIBLE);
					}
				}
			}
		});

	}

}
