package com.wustwxy2.models;

/**
 * Created by Administrator on 2016/7/15.
 */
public class News {
    private String title,href;

    public News(String title, String href){
        setTitle(title);
        setHref(href);
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
    public void setHref(String href){
        this.href=href;
    }
    public String getHref(){
        return href;
    }
}
