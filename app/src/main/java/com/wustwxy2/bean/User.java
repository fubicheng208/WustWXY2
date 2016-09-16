package com.wustwxy2.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author fubicheng
 * @ClassName: User
 * @Description: TODO
 * @date 2016/7/29 16:41
 */
public class User extends BmobUser {


    private String libpassword;//图书馆密码
    private String cardpassword;//卡务密码
    private String nickname;//昵称
    private BmobFile head;//头像（暂不支持）

    public User(String username, String password){
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getLibpassword() {
        return libpassword;
    }

    public void setLibpassword(String libpassword) {
        this.libpassword = libpassword;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BmobFile getHead() {
        return head;
    }

    public void setHead(BmobFile head) {
        this.head = head;
    }
}
