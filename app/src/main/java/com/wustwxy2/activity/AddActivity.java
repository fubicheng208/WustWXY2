package com.wustwxy2.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wustwxy2.R;
import com.wustwxy2.bean.Found;
import com.wustwxy2.bean.Lost;
import com.wustwxy2.bean.User;
import com.wustwxy2.i.IMainPresenter;
import com.wustwxy2.i.IMainView;
import com.wustwxy2.util.Compressor;
import com.wustwxy2.util.MainPresenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AddActivity extends BaseActivity implements View.OnClickListener, IMainView {

    private static final int SELECT_PIC_KITKAT = 0x11;
    private static final int SELECT_PIC= 0x12;
    private SystemBarTintManager tintManager;
    private IMainPresenter mMainPresenter;

    //压缩后的图片保存的地址
    private static String IMG_PATH = getSDPath() + java.io.File.separator
            + "wustwxy2";

    //需填写的各项
    EditText edit_title, edit_phone, edit_describe;
    //返回按钮和确定按钮
    Button btn_back, btn_true;
    //需要上传的图片
    ImageView iv_photo;
    private static Bitmap bitmapSelected;
    TextView tv_add;
    //声明进度条对话框对象
    private ProgressDialog dialog;

    String from = "";

    String old_title = "";
    String old_describe = "";
    String old_phone = "";

    String path="";

    @Override
    public void setContentView() {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_add);
    }

    @Override
    public void initViews() {
        // TODO Auto-generated method stub
        tv_add = (TextView) findViewById(R.id.tv_add);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_true = (Button) findViewById(R.id.btn_true);
        edit_phone = (EditText) findViewById(R.id.losing_add_phone);
        edit_describe = (EditText) findViewById(R.id.losing_add_describe);
        edit_title = (EditText) findViewById(R.id.losing_add_title);
        iv_photo = (ImageView) findViewById(R.id.losing_photo);
        initWindow();
    }

    @Override
    public void initListeners() {
        // TODO Auto-generated method stub
        btn_back.setOnClickListener(this);
        btn_true.setOnClickListener(this);
        iv_photo.setOnClickListener(this);
    }

    @Override
    public void initData() {

        File path = new File(IMG_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }

        // TODO Auto-generated method stub
        from = getIntent().getStringExtra("from");
        old_title = getIntent().getStringExtra("title");
        old_phone = getIntent().getStringExtra("phone");
        old_describe = getIntent().getStringExtra("describe");

        edit_title.setText(old_title);
        edit_describe.setText(old_describe);
        edit_phone.setText(old_phone);


        if (from.equals(getResources().getText(R.string.lost))) {
            tv_add.setText(getResources().getText(R.string.add_losing));
        } else {
            tv_add.setText(getResources().getText(R.string.add_found));
        }

        mMainPresenter = new MainPresenter(this, this);

        //设置进度条
        dialog = new ProgressDialog(this);
        //设置进度条样式
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        //失去焦点的时候，不是去对话框
        dialog.setCancelable(false);
        dialog.setMessage("正在上传...");
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btn_true) {
            addByType();
        }else if(v == iv_photo){
            /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image*//*");
            startActivityForResult(Intent.createChooser(intent,"选择图片"),SELECT_PICTURE);*/
            requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionHandler() {
                @Override
                public void onGranted() {
                    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/jpeg");
                    if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.KITKAT){
                        startActivityForResult(intent, SELECT_PIC_KITKAT);
                    }else{
                        startActivityForResult(intent, SELECT_PIC);
                    }
                }

                @Override
                public void onDenied() {
                    Toast.makeText(AddActivity.this, "由于您拒绝了权限申请，无法正常使用该功能", Toast.LENGTH_LONG).show();
                }

                @Override
                public boolean onNeverAsk() {
                    new AlertDialog.Builder(AddActivity.this)
                            .setTitle(R.string.permission_ask_title)
                            .setMessage(R.string.permission_mes)
                            .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);

                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .setCancelable(false)
                            .show();
                    return  true;
                }
            });
        } else if (v == btn_back) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;
        /*if (requestCode == PHOTO_RESULT) {
            bitmapSelected = decodeUriAsBitmap(Uri.fromFile(new File(IMG_PATH,
                    "temp.jpg")));
            losing_photo.setText("");
            Drawable drawable =new BitmapDrawable(bitmapSelected);
            losing_photo.setBackground(drawable);
        }*/
        if(resultCode == RESULT_OK){
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try{
                if(bitmapSelected!=null)//如果不施放的话，不断读取图片，将会内存不够
                    bitmapSelected.recycle();
                bitmapSelected = BitmapFactory.decodeStream(cr.openInputStream(uri));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if(DocumentsContract.isDocumentUri(this, uri))
                        path = getPath(this,uri);
                    else
                        path = selectImage(this, data);
                }
                else{
                    path = getPath(this,uri);
                }

                /*String[] proj = {MediaStore.Images.Media.DATA};
                //好像是android多媒体数据库的封装接口，具体的看Android文档
                Cursor cursor = getContentResolver().query(uri,proj, null, null, null);
                //按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                path = cursor.getString(column_index);
                Log.i(TAG,""+path);*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //losing_photo.setText("");
            //Drawable drawable =new BitmapDrawable(bitmapSelected);
            iv_photo.setImageBitmap(bitmapSelected);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    String title = "";
    String describe = "";
    String phone="";

    /**根据类型添加失物/招领
     * addByType
     * @Title: addByType
     * @throws
     */
    private void addByType()  {
        title = edit_title.getText().toString();
        describe = edit_describe.getText().toString();
        phone = edit_phone.getText().toString();

        if(TextUtils.isEmpty(title)){
            ShowToast("请填写标题");
            return;
        }
        if(TextUtils.isEmpty(describe)){
            ShowToast("请填写描述");
            return;
        }
        if(TextUtils.isEmpty(phone)){
            ShowToast("请填写手机");
            return;
        }
        if(from.equals(getResources().getText(R.string.lost))){
            addLost();
        }else{
            addFound();
        }
    }

    private void addLost()  {
        if(!path.equals("")){
            //Bitmap bitmap = ImageUtils.getInstant().getCompressedBitmap(path);
            File before_compressed = new File(path);
            File file = Compressor.getDefault(this).compressToFile(before_compressed);
            Log.i(TAG, "压缩前" + String.format("Size : %s", getReadableFileSize(before_compressed.length())));
            Log.i(TAG, "压缩后" + String.format("Size : %s", getReadableFileSize(file.length())));
            final BmobFile bmobFile = new BmobFile(file);
            dialog.show();
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        Log.i(TAG, "图片上传成功:"+bmobFile.getFileUrl());
                        User user = BmobUser.getCurrentUser(User.class);
                        String username = user.getUsername();
                        Log.i(TAG, username);
                        Lost lost = new Lost(title,phone,describe,bmobFile);
                        lost.setPhotoUrl(bmobFile.getFileUrl());
                        lost.setAuthor(user);
                        insertObject(lost);
                    }
                    else{
                        Log.i(TAG,"上传失败"+e.getMessage()+","+e.getErrorCode());
                        ShowToast("网络异常，上传失败");
                    }
                }
            });
        }
        else
        {
            dialog.setTitle("正在提交");
            dialog.show();
            Lost lost = new Lost(title, phone,  describe);
            User user = BmobUser.getCurrentUser(User.class);
            lost.setAuthor(user);
            insertObject(lost);
        }
    }

    private void addFound(){
        if(!path.equals("")){
            File before_compressed = new File(path);
            //文件压缩
            File file = Compressor.getDefault(this).compressToFile(before_compressed);
            Log.i(TAG, "压缩前" + String.format("Size : %s", getReadableFileSize(before_compressed.length())));
            Log.i(TAG, "压缩后" + String.format("Size : %s", getReadableFileSize(file.length())));
            final BmobFile bmobFile = new BmobFile(file);
            dialog.show();
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        Log.i(TAG, "图片上传成功:"+bmobFile.getFileUrl());
                        User user = BmobUser.getCurrentUser(User.class);
                        String username = user.getUsername();
                        Log.i(TAG, username);
                        Found found = new Found(title,phone,describe,bmobFile);
                        found.setPhotoUrl(bmobFile.getFileUrl());
                        found.setAuthor(user);
                        insertObject(found);
                    }
                    else{
                        Log.i(TAG,"上传失败"+e.getMessage()+","+e.getErrorCode());
                        ShowToast("网络异常，上传失败");
                    }
                }
            });
        }
        else
        {
            dialog.setTitle("正在提交");
            dialog.show();
            Found found = new Found(title,phone,describe);
            User user = BmobUser.getCurrentUser(User.class);
            found.setAuthor(user);
            insertObject(found);
        }
    }


    private void insertObject(final BmobObject obj){
        obj.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    if(from.equals(getResources().getText(R.string.lost))) {
                        ShowToast(getString(R.string.toast_losing) );
                        Log.i(TAG, getString(R.string.toast_losing) + obj.getObjectId());
                    }else{
                        ShowToast(getString(R.string.toast_found));
                        Log.i(TAG, getString(R.string.toast_found) + obj.getObjectId());
                    }
                    dialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }
                else{
                    dialog.dismiss();
                    if(from.equals(getResources().getText(R.string.lost))){
                        Log.i(TAG,getString(R.string.toast_losing_fail));
                        ShowToast(getString(R.string.toast_losing_fail));
                    }else{
                        Log.i(TAG,getString(R.string.toast_found_fail));
                        ShowToast(getString(R.string.toast_found_fail));
                    }
                }
            }
        });
    }

    //设置沉浸式状态栏和导航栏
    private void initWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimary));
            tintManager.setStatusBarTintEnabled(true);
        }
    }


    /**
     * 获取sd卡的路径
     *
     * @return 路径的字符串
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取外存目录
        }
        return sdDir.toString();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String selectImage(Context context,Intent data){
        Uri selectedImage = data.getData();
//      Log.e(TAG, selectedImage.toString());
        if(selectedImage!=null){
            String uriStr=selectedImage.toString();
            String path=uriStr.substring(10,uriStr.length());
            if(path.startsWith("com.sec.android.gallery3d")){
                Log.e(TAG, "It's auto backup pic path:"+selectedImage.toString());
                return null;
            }
        }
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    @Override
    public void showImageView(Bitmap bitmap, String fileName) {

    }

    //文件大小转换
    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}