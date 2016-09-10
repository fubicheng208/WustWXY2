package com.wustwxy2.i;

import android.net.Uri;

import java.util.List;

/**
 * @author fubicheng
 * @ClassName:
 * @Description: TODO
 * @date 2016/8/3 22:41
 */
public interface IMainPresenter {
    void compressImage(Uri uri);
    void uploadImage(List<String> files);
}
