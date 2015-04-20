package com.toraysoft.widget.imageselector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

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
	
	public HashMap<String, ViewHolder> vhs;

	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	public ImageSelectorAdapter(Context context, List<String> mDatas,
			int itemLayoutId, String dirPath, boolean isMuti) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.isMuti = isMuti;
		
		vhs = new HashMap<String, ViewHolder>();
	}
	
	void reset(){
		for (int i = 0; i < mSelectedImage.size(); i++) {
			String key = mSelectedImage.get(i);
			if(vhs.containsKey(key)){
				ViewHolder vh = vhs.get(key);
				final ImageView mSelect = vh.getView(R.id.id_item_select);
				final TextView tvNum = vh.getView(R.id.id_item_num);
				if(key.equals(mSelect.getTag())){
					mSelect.setVisibility(View.VISIBLE);
					tvNum.setVisibility(View.VISIBLE);
					tvNum.setText(String.valueOf(i+1));
				}
			}
			
		}
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
		final TextView tvNum = helper.getView(R.id.id_item_num);
		mSelect.setImageResource(R.drawable.imageselector_icon_pictures_selected);
		mSelect.setVisibility(View.GONE);
		tvNum.setVisibility(View.GONE);
		final String imagePath = mDirPath + "/" + item;
		mSelect.setTag(imagePath);
		if (mSelectedImage.contains(imagePath)) {
			mSelect.setVisibility(View.VISIBLE);
			tvNum.setVisibility(View.VISIBLE);
			tvNum.setText(String.valueOf(mSelectedImage.indexOf(imagePath)+1));
			vhs.put(imagePath, helper);
		} else {
			vhs.remove(imagePath);
			mSelect.setVisibility(View.GONE);
			tvNum.setVisibility(View.GONE);
		}
		
		
		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择
			@Override
			public void onClick(View v) {
				if (!isMuti) {
					if (iv_last != null) {
						iv_last.setVisibility(View.GONE);
					}
					iv_last = mSelect;
					if (mSelectedImage.contains(imagePath)) {
						mSelectedImage.clear();
						mSelect.setVisibility(View.GONE);
					} else {
						mSelectedImage.clear();
						mSelectedImage.add(imagePath);
						mSelect.setVisibility(View.VISIBLE);
					}
				} else {
					if (iv_last != mSelect) {
						iv_last = mSelect;
					}
					if (mSelectedImage.contains(imagePath)) {
						mSelectedImage.remove(imagePath);
						vhs.remove(imagePath);
						mSelect.setVisibility(View.GONE);
						tvNum.setVisibility(View.GONE);
						reset();
					} else {
						mSelectedImage.add(imagePath);
						vhs.put(imagePath, helper);
						tvNum.setText(String.valueOf(mSelectedImage.indexOf(imagePath)+1));
						mSelect.setVisibility(View.VISIBLE);
						tvNum.setVisibility(View.VISIBLE);
					}
				}
			}
		});

	}

}
