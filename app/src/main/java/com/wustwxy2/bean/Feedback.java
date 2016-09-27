package com.wustwxy2.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author fubicheng
 * @ClassName:
 * @Description: TODO
 * @date 2016/9/15 00:25
 */
public class Feedback extends BmobObject{
    private String back;
    private User author;//反馈人外键

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
