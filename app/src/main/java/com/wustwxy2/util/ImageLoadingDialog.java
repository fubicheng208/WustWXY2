package com.wustwxy2.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.wustwxy2.R;

public class ImageLoadingDialog extends Dialog {

	public ImageLoadingDialog(Context context) {
		super(context, R.style.ImageloadingDialogStyle);
		//setOwnerActivity((Activity) context);// // 设置dialog全屏显示
	}

	private ImageLoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_imageloading);
	}

}
