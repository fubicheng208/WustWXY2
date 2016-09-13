package com.wustwxy2.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import com.wustwxy2.i.IMainPresenter;
import com.wustwxy2.i.IMainView;

import java.io.File;
import java.util.List;

/**
 * @author fubicheng
 * @ClassName:
 * @Description: TODO
 * @date 2016/8/3 22:42
 */
public class MainPresenter implements IMainPresenter {

    private Activity mContext;
    private IMainView mMainView;

    public MainPresenter(Activity context, IMainView mainView) {
        this.mContext = context;
        this.mMainView = mainView;
    }

    @Override
    public void compressImage(final Uri uri) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                File file = new File(FileSizeUtil.getImageAbsolutePath(mContext, uri));
                BitmapFactory.Options options = FileSizeUtil.getBitmapOptions(file.getPath());
                int screenMax = Math.max(DensityUtils.getWindowWidth(mContext),
                        DensityUtils.getWindowHeight(mContext));
                int imgMax = Math.max(options.outWidth, options.outHeight);
                int inSimpleSize = 1;
                if (screenMax <= imgMax) {
                    inSimpleSize = Math.max(screenMax, imgMax) / Math.min(screenMax, imgMax);
                }
                return FileSizeUtil.compressBitmap(mContext,
                        file.getAbsolutePath(),
                        Bitmap.CompressFormat.JPEG,
                        options.outWidth / inSimpleSize,
                        options.outHeight / inSimpleSize,
                        false);
            }

            @Override
            protected void onPostExecute(final String fileName) {
                Bitmap bitmap = BitmapFactory.decodeFile(fileName);
                mMainView.showImageView(bitmap,fileName);
            }
        }.execute();
    }

    @Override
    public void uploadImage(List<String> files) {
        //retrofit 2.0上传多张图片
//        Map<String, RequestBody> map = new HashMap<>();
//        for(String fileName:files) {
//            final File imageBytes = new File(fileName);
//            if (imageBytes != null) {
//                RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), imageBytes);
//                map.put("file\"; filename=\"" + imageBytes.getName() + "", requestBody);
//            }
//        }
//        ReqOperater.instance().uploadImage(new ReqDataCallBack<HeaderImgUrl>() {
//
//            @Override
//            public void onNext(HeaderImgUrl result) {
//                super.onNext(result);
//                if (result.isSuccess()) {
//                    mRefundView.showImageView(result.getUrl());
//                }
//            }
//        }, map);
    }
}
