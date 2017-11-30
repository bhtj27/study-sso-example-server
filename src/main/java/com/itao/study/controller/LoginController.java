package com.itao.study.controller;

import com.itao.study.common.LocalTokenManager;
import com.itao.study.common.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import study.common.factory.IdFactory;
import study.common.util.CookieUtils;
import study.sso.example.client.filter.SsoFilter;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * @Description :
 * @Author JunTao
 * @Date : 2017/11/29
 */
@Controller
@RequestMapping("/user")
public class LoginController {
    @Resource
    private LocalTokenManager localTokenManager;

    private static String LOGIN_PATH = "login";
    private static String BACK_PATH = "backUrl";
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);


    @RequestMapping("test")
    public String test(HttpServletRequest request){
        return "test";
    }

    //客户端系统接入认证中心
    @RequestMapping("index")
    public String login(HttpServletRequest request){
        String token = CookieUtils.getCookie(request, "token");
        String backUrl = request.getParameter(BACK_PATH);
        if(StringUtils.isEmpty(token)){
            return goLoginPath(backUrl, request);
        }else{
            LoginUser loginUser = localTokenManager.validate(token);
            if(loginUser!=null){
                return "redirect:" + authBackUrl(backUrl, token);
            }else{
                return goLoginPath(backUrl, request);
            }
        }
    }

    @RequestMapping("login")
    public String login(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String backUrl = request.getParameter("backUrl");
        logger.info("sso-server login start. target url={}",backUrl);

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        //这里简写，实际上是需要去数据库校验登录用户信息的
        if(!"abc".equals(username)){
            return goLoginPath(backUrl, request);
        }

        //用户登录成功
        LoginUser loginUser = new LoginUser(0,"abc");
        String token = CookieUtils.getCookie(request, "token");
        if (StringUtils.isEmpty(token) || localTokenManager.validate(token) == null) {//token失效或者token为空
            token = createToken(loginUser);
            addTokenInCookie(token, request, response);
        }

        // 跳转到原请求
        backUrl = URLDecoder.decode(backUrl, "utf-8");
        return "redirect:" + authBackUrl(backUrl, token);
    }

    //拼接token到原跳转url中
    private String authBackUrl(String backUrl,String token){
        StringBuilder url = new StringBuilder(backUrl);
        if (backUrl.indexOf("?") > 0) {
            url.append("&");
        }
        else {
            url.append("?");
        }
        url.append(SsoFilter.SSO_TOKEN_NAME).append("=").append(token);
        return url.toString();
    }

    //跳转到认证中心的登录页面
    private String goLoginPath(String backUrl,HttpServletRequest request){
        request.setAttribute(BACK_PATH, backUrl);
        return LOGIN_PATH;
    }

    //创建token并添加到缓存中
    private String createToken(LoginUser loginUser) {
        String token = IdFactory.createUUID();

        // 缓存中添加token对应User
        localTokenManager.addToken(token, loginUser);
        return token;
    }

    private void addTokenInCookie(String token, HttpServletRequest request, HttpServletResponse response) {
        // Cookie添加token
        Cookie cookie = new Cookie("token", token);
        CookieUtils.addCookie(request,response,cookie);
    }
}
