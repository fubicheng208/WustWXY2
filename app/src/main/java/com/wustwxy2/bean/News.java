package com.wustwxy2.bean;

/**
 * Created by Administrator on 2016/7/15.
 */
public class News {
    private String title;

    private String dateOrCollege;

    private String href;
    public News(String title, String href){
        setTitle(title);
        setHref(href);
    }

    public News(String title, String dateOrCollege, String href){
        setTitle(title);
        setDateOrCollege(dateOrCollege);
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

    public String getDateOrCollege() {
        return dateOrCollege;
    }

    public void setDateOrCollege(String dateOrCollege) {
        this.dateOrCollege = dateOrCollege;
    }
}
