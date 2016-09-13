package com.wustwxy2.bean;

import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author fubicheng
 * @ClassName: Found
 * @Description: TODO
 * @date 2016/7/29 16:12
 */

public class Found extends BmobObject {

    private String title;//标题
    private String describe;//描述
    private String phone;//联系手机
    private String photoUrl;//上传图片后返回得URL地址
    private BmobFile photo;//图片
    private User author;//发帖人外键

    public Found(){
    }

    public Found(String title, String phone, String describe) {
        this.title = title;
        this.phone = phone;
        this.describe = describe;
    }

    public Found(String title, String phone, String describe, BmobFile photo) {
        this.title = title;
        this.phone = phone;
        this.describe = describe;
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescribe() {
        return describe;
    }
    public void setDescribe(String describe) {
        this.describe = describe;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public BmobFile getPhoto() {
        return photo;
    }
    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }
    public User getAuthor() {
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
