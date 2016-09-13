package com.wustwxy2.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wsasus on 2016/7/14.
 */
public class JwOpenHelper extends SQLiteOpenHelper {

    private final String CREATE_SCORE="create table Score("
            + "id integer  primary key autoincrement,"
            +"kcmc text,"
            +"kkxq text,"
            +"zcj text,"
            +"xf text,"
            +"jd text)";

    private final String CREATE_COURSE="create table Course("
            + "id integer  primary key autoincrement,"
            +"kcmc text,"
            +"skdd text,"
            +"skjs text,"
            +"startZc integer,"
            +"endZc integer,"
            +"week integer,"
            +"classStart integer,"
            +"classStep integer,"
            +"classCode integer)";

    public JwOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /*xh学生学号
            xm学生姓名
    kkxq开课学期
            kcmc课程名称
    zcj总成绩
            cjbsmc成绩标志
    kcxzmc课程性质
            kclbmc课程属性
    zxs学时
            xf学分
    ksxzmc考试性质
            bcxq补重学期
    jd绩点*/


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SCORE);
        db.execSQL(CREATE_COURSE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
