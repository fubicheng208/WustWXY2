package com.wustwxy2.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wustwxy2.util.JwOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lqy on 2016/7/15.
 * 对数据库所有相关操作进行封装，以保证安全性
 */
public class JwInfoDB {
    private static final String DB_NAME="jwInfo";
    private static final int VERSION=1;
    private static JwInfoDB mJwInfoDB;
    private static SQLiteDatabase db;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private JwInfoDB(Context context){
        JwOpenHelper jwOpenHelper=new JwOpenHelper(context,DB_NAME,null,VERSION);
        db=jwOpenHelper.getWritableDatabase();
        mPreferences=context.getSharedPreferences("data", Context.MODE_PRIVATE);
        mEditor=mPreferences.edit();
    }

    //仅在此能获得JwInfoDB实例(synchronized)
    public synchronized static JwInfoDB getJwInfoDB(Context context){
        if (mJwInfoDB==null){
            mJwInfoDB=new JwInfoDB(context);
        }
        return mJwInfoDB;
    }

    /*
+ "id integer  primary key autoincrement,"
        +"kcmc text,"
        +"zcj text,"
        +"xf text,"
        +"jd text)";*/
    public void saveScore(Score score){
        if(score!=null){
            ContentValues values=new ContentValues();
            values.put("kcmc",score.getKcmc());
            values.put("kkxq",score.getKkxq());
            values.put("zcj",score.getZcj());
            values.put("xf",score.getXf());
            values.put("jd",score.getJd());
            db.insert("Score",null,values);
        }
    }

    public void deleteAllScore(){
        db.delete("Score",null,null);
    }

    //获取所有成绩数据
    public List<Score> loadScores(){
        double fz=0.0,fm=0.0;
        List<Score> list=new ArrayList<Score>();
        Cursor cursor=db.query("Score",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Score score=new Score();
                score.setId(cursor.getInt(cursor.getColumnIndex("id")));
                score.setKcmc(cursor.getString(cursor.getColumnIndex("kcmc")));
                score.setKkxq(cursor.getString(cursor.getColumnIndex("kkxq")));
                score.setZcj(cursor.getString(cursor.getColumnIndex("zcj")));
                String xf=cursor.getString(cursor.getColumnIndex("xf"));
                score.setXf(xf);
                String jd=cursor.getString(cursor.getColumnIndex("jd"));
                score.setJd(jd);
                list.add(score);
                fz+= Double.valueOf(xf)* Double.valueOf(jd);
                fm+= Double.valueOf(xf);
            }while (cursor.moveToNext());
        }
        mEditor.putString("averageJd", String.valueOf(fz/fm));
        mEditor.commit();
        cursor.close();
        return list;
    }

    //获取某一学期成绩数据
    public List<Score> loadOneTeamScores(String xq){
        double fz=0.0,fm=0.0;
        List<Score> list=new ArrayList<Score>();
        Cursor cursor=db.query("Score",null,"kkxq=?",new String[]{xq},null,null,null);
        if(cursor.moveToFirst()){
            do{
                Score score=new Score();
                score.setId(cursor.getInt(cursor.getColumnIndex("id")));
                score.setKcmc(cursor.getString(cursor.getColumnIndex("kcmc")));
                score.setKkxq(cursor.getString(cursor.getColumnIndex("kkxq")));
                score.setZcj(cursor.getString(cursor.getColumnIndex("zcj")));
                String xf=cursor.getString(cursor.getColumnIndex("xf"));
                score.setXf(xf);
                String jd=cursor.getString(cursor.getColumnIndex("jd"));
                score.setJd(jd);
                list.add(score);
                fz+= Double.valueOf(xf)* Double.valueOf(jd);
                fm+= Double.valueOf(xf);
            }while (cursor.moveToNext());
        }
        mEditor.putString("averageJd", String.valueOf(fz/fm));
        mEditor.commit();
        cursor.close();
        return list;
    }

    //获取某一学科成绩数据
    public List<Score> loadOneScore(String mc){
        double jd = 0.0;
        List<Score> list=new ArrayList<Score>();
        Cursor cursor=db.query("Score",null,"kcmc=?",new String[]{mc},null,null,null);
        if(cursor.moveToFirst()){
            do{
                Score score=new Score();
                score.setId(cursor.getInt(cursor.getColumnIndex("id")));
                score.setKcmc(cursor.getString(cursor.getColumnIndex("kcmc")));
                score.setKkxq(cursor.getString(cursor.getColumnIndex("kkxq")));
                score.setZcj(cursor.getString(cursor.getColumnIndex("zcj")));
                score.setXf(cursor.getString(cursor.getColumnIndex("xf")));
                String jdString=cursor.getString(cursor.getColumnIndex("jd"));
                score.setJd(jdString);
                jd= Double.valueOf(jdString);
                list.add(score);
            }while (cursor.moveToNext());
        }
        mEditor.putString("averageJd", String.valueOf(jd));
        mEditor.commit();
        cursor.close();
        return list;
    }

    public void saveCourse(Course course){
        if(course!=null){
            ContentValues values=new ContentValues();
            values.put("kcmc",course.getName());
            values.put("skdd",course.getRoom());
            values.put("skjs",course.getTeach());
            values.put("startZc",course.getStartZc());
            values.put("endZc",course.getEndZc());
            values.put("week",course.getWeek());
            values.put("classStart",course.getStart());
            values.put("classStep",course.getStep());
            values.put("classCode",course.getClassCode());
            db.insert("Course",null,values);
        }
    }

    public void deleteAllCourse(){
        db.delete("Course",null,null);
    }

    public List<Course> loadCourses(int currentZc, int week){
        List<Course> list=new ArrayList<Course>();
        Cursor cursor=db.query("Course",null,"week=? and ?>=startZc and ?<=endZc",new String[]{String.valueOf(week),
                String.valueOf(currentZc), String.valueOf(currentZc)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                Course course=new Course();
                course.setName(cursor.getString(cursor.getColumnIndex("kcmc")));
                course.setRoom(cursor.getString(cursor.getColumnIndex("skdd")));
                course.setTeach(cursor.getString(cursor.getColumnIndex("skjs")));
                course.setStartZc(cursor.getInt(cursor.getColumnIndex("startZc")));
                course.setEndZc(cursor.getInt(cursor.getColumnIndex("endZc")));
                course.setWeek(cursor.getInt(cursor.getColumnIndex("week")));
                course.setStart(cursor.getInt(cursor.getColumnIndex("classStart")));
                course.setStep(cursor.getInt(cursor.getColumnIndex("classStep")));
                course.setClassCode(cursor.getInt(cursor.getColumnIndex("classCode")));
                list.add(course);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

   /* + "id integer  primary key autoincrement,"
            +"kcmc text,"
            +"skdd text,"
            +"skjs text,"
            +"startZc integer,"
            +"endZc integer,"
            +"week integer,"
            +"classStart integer,"
            +"classStep integer,"
            +"classCode integer)";*/
}
