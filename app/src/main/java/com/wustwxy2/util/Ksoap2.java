package com.wustwxy2.util;


import com.wustwxy2.models.Md5Util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lqy on 2016/7/15.
 * 此类通过android提供的Ksoap2包调用webservice接口
 * 其中接口不可泄露！！！
 */
public class Ksoap2 {

    private static final String key="webservice_whkdapp";
    // 命名空间
    private static final String nameSpace = "http://webservices.qzdatasoft.com";
    // EndPoint
    private static final String endPoint = "http://jwxt.wust.edu.cn/whkjdx/services/whkdapp";



    public static String getScoreInfo(String xh) {

        // 调用的方法名称
        String methodName = "getxscj";

        // SOAP Action
        final String soapAction = "http://webservices.qzdatasoft.com/getxscj";

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        //相关参数
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time=format.format(date);
        String chkvalue=key+time;
        chkvalue= Md5Util.MD5(chkvalue);
        chkvalue=chkvalue.substring(2).toLowerCase();

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("in0", xh);
        rpc.addProperty("in1", time);
        rpc.addProperty("in2", chkvalue);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);


        HttpTransportSE transport = new HttpTransportSE(endPoint);
        try {
            // 调用WebService
            transport.call(soapAction, envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        String result = object.getProperty("out").toString();

        return result;
    }

    public static String getCourseInfo(String xh, String xq) {

        // 调用的方法名称
        String methodName = "getyxkclb";

        // SOAP Action
        final String soapAction = "http://webservices.qzdatasoft.com/getyxkclb";

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        //相关参数
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time=format.format(date);
        String chkvalue=key+time;
        chkvalue= Md5Util.MD5(chkvalue);
        chkvalue=chkvalue.substring(2).toLowerCase();

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("in0", xh);
        rpc.addProperty("in1",xq);
        rpc.addProperty("in2", time);
        rpc.addProperty("in3", chkvalue);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);


        HttpTransportSE transport = new HttpTransportSE(endPoint);
        try {
            // 调用WebService
            transport.call(soapAction, envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        String result = object.getProperty("out").toString();

        return result;
    }

    public static String getLoginInfo(String xh, String pwd) {

        // 调用的方法名称
        String methodName = "xslogin";

        // SOAP Action
        final String soapAction = "http://webservices.qzdatasoft.com/xslogin";

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        //相关参数
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time=format.format(date);
        String chkvalue=key+time;
        chkvalue= Md5Util.MD5(chkvalue);
        chkvalue=chkvalue.substring(2).toLowerCase();

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("in0", xh);
        rpc.addProperty("in1", pwd);
        rpc.addProperty("in2", time);
        rpc.addProperty("in3", chkvalue);

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);


        HttpTransportSE transport = new HttpTransportSE(endPoint);
        try {
            // 调用WebService
            transport.call(soapAction, envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        String result = object.toString();

        return result;
    }
}
