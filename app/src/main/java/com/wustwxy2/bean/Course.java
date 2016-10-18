package com.wustwxy2.bean;

import java.io.Serializable;

/**
 * Created by wsasus on 2016/7/18.
 */

public class Course implements Serializable {
    private String name,room,teach;//课程名称、上课教室，教师
    int id,start,step,startZc,endZc,week,classCode;	//开始上课节次， 一共几节课

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getTeach() {
        return teach;
    }

    public void setTeach(String teach) {
        this.teach = teach;
    }

    public int getStartZc() {
        return startZc;
    }

    public void setStartZc(int startZc) {
        this.startZc = startZc;
    }

    public int getEndZc() {
        return endZc;
    }

    public void setEndZc(int endZc) {
        this.endZc = endZc;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassCode() {
        return classCode;
    }

    public void setClassCode(int classCode) {
        this.classCode = classCode;
    }


}
