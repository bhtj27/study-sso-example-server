package com.itao.study.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description :本地令牌管理类
 * @Author JunTao
 * @Date : 2017/11/29
 */
@Component
public class LocalTokenManager extends TokenManager {

    private static Logger logger = LoggerFactory.getLogger(LocalTokenManager.class);

    // 令牌存储结构
    private final ConcurrentMap<String, DummyUser> tokenMap = new ConcurrentHashMap<>();

    //检查令牌是否失效
    @Override
    public void verifyExpired() {
        Date now = new Date();
        for (Map.Entry<String, DummyUser> entry : tokenMap.entrySet()) {
            String token = entry.getKey();
            DummyUser dummyUser = entry.getValue();
            // 当前时间大于过期时间
            if (now.compareTo(dummyUser.expired) > 0) {
                // 已过期，清除对应token
                tokenMap.remove(token);
                logger.debug("token : " + token + "已失效");
            }
        }
    }

    //添加令牌到缓存中
    @Override
    public void addToken(String token, LoginUser loginUser) {
        DummyUser dummyUser = new DummyUser(loginUser);
        tokenMap.putIfAbsent(token,dummyUser);
    }

    //验证token是否有效
    @Override
    public LoginUser validate(String token) {
        DummyUser dummyUser = tokenMap.get(token);
        if (dummyUser == null) {
            return null;
        }
        dummyUser.refreshExpired();//刷新缓存token的时间
        return dummyUser.loginUser;
    }

    @Override
    public void remove(String token) {
        tokenMap.remove(token);
    }

    // 复合结构体，含loginUser与过期时间expried两个成员
    private class DummyUser {
        private LoginUser loginUser;
        private Date expired; // 过期时间

        public DummyUser(LoginUser loginUser) {
            this.loginUser = loginUser;
            this.expired = new Date(System.currentTimeMillis() + tokenTimeout * 1000);
        }

        public void refreshExpired(){
            this.expired = new Date(System.currentTimeMillis() + tokenTimeout * 1000);
        }
    }
}
