package com.wustwxy2.bean;

/**
 * Created by wsasus on 2016/7/15.
 */
public class Score {
    private int id;
    private String kcmc;
    private String kkxq;
    private String zcj;
    private String xf;
    private String jd;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setKcmc(String kcmc) {
        this.kcmc = kcmc;
    }

    public String getKcmc() {
        return kcmc;
    }

    public void setKkxq(String kkxq) {
        this.kkxq = kkxq;
    }

    public String getKkxq() {
        return kkxq;
    }

    public void setZcj(String zcj) {
        this.zcj = zcj;
    }

    public String getZcj() {
        return zcj;
    }

    public void setXf(String xf) {
        this.xf = xf;
    }

    public String getXf() {
        return xf;
    }

    public void setJd(String jd) {
        this.jd = jd;
    }

    public String getJd() {
        return jd;
    }
}

/*
+ "id integer  primary key autoincrement,"
        +"kcmc text,"
        +"zcj text,"
        +"xf text,"
        +"jd text)";*/
