package com.wustwxy2.util;


import com.wustwxy2.bean.Course;
import com.wustwxy2.models.JwInfoDB;
import com.wustwxy2.bean.Score;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lqy on 2016/7/15.
 * 解析接口返回数据并储存
 */
public class Utility {

    public static String currentTeam="",xh="",xm="";

    public synchronized static String getName(String responce){
        String name="";
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(responce);
            JSONObject object = null;
            try {
                object = jsonArray.getJSONObject(0);
                name =object.getString("xm");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return name;
    }

    public synchronized static boolean handleScores(JwInfoDB jwInfoDB, String response) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
            jwInfoDB.deleteAllScore();///
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = null;
                Score score = new Score();
                try {
                    object = jsonArray.getJSONObject(i);
                    if(i==0){
                        xh=object.getString("xh");
                        xm=object.getString("xm");
                    }
                    score.setKcmc(object.getString("kcmc"));
                    String team=object.getString("kkxq");
                    if(currentTeam.compareTo(team)<0){
                        currentTeam=team;
                    }
                    score.setKkxq(team);
                    score.setZcj(object.getString("zcj"));
                    score.setXf(object.getString("xf"));
                    score.setJd(object.getString("jd"));
                    jwInfoDB.saveScore(score);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public synchronized static boolean handleCourses(JwInfoDB jwInfoDB,String response) {
        String[] arr=response.split("kcmc");
        response="";
        for(int i=0;i<arr.length-1;i++){
            response+=arr[i]+",kcmc";
        }
        response+=arr[arr.length-1];
        try {
            JSONArray jsonArray=new JSONArray(response);
            jwInfoDB.deleteAllCourse();
            for(int j=0;j<jsonArray.length();j++){
                JSONObject jsonObject=jsonArray.getJSONObject(j);
                if(!jsonObject.getString("skzc").equals("")) {
                    String[] zc = jsonObject.getString("skzc").split(",");
                    String[] sksj = jsonObject.getString("sksj").split(",");
                    String[] skdd = jsonObject.getString("skdd").split(",");
                    if (zc != null && zc.length > 0) {
                        for (int i = 0; i < zc.length&&i<sksj.length; i++) {
                            String s = zc[i];
                            int startZc, endZc;
                            if (s.contains("-")) {
                                String[] array = s.split("-");
                                startZc = Integer.valueOf(array[0]);
                                endZc = Integer.valueOf(array[1]);
                            } else {
                                startZc = endZc = Integer.valueOf(s);
                            }
                            int sj = Integer.valueOf(sksj[i]);
                            int end = sj % 100;
                            int start = sj / 100 % 100;
                            int week = sj / 10000;
                            Course course = new Course();
                            course.setName(jsonObject.getString("kcmc"));
                            course.setRoom(skdd[i]);
                            course.setTeach(jsonObject.getString("skjs"));
                            course.setStartZc(startZc);
                            course.setEndZc(endZc);
                            course.setWeek(week);
                            course.setStart(start);
                            course.setStep(end - start + 1);
                            course.setClassCode(j);
                            jwInfoDB.saveCourse(course);
                        }
                    }
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static int getWeekOfDate() {
        int[] weekDays = {7,1,2,3,4,5,6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    public static String getDate(){
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static int getYear(){
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy");
        return Integer.valueOf(format.format(date));
    }

    public static int getDays(String d1, String d2){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long to = 0,from=0;
        try {
            to = df.parse(d2).getTime();
            from = df.parse(d1).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) ((to - from) / (1000 * 60 * 60 * 24));
    }
}
