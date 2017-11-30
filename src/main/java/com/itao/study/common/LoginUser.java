package com.itao.study.common;

import java.io.Serializable;

/**
 * @Description :登录成功用户对象
 * @Author JunTao
 * @Date : 2017/11/29
 */
public class LoginUser implements Serializable {
    private Integer userid;
    private String username;

    public LoginUser(Integer userid, String username) {
        this.userid = userid;
        this.username = username;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
