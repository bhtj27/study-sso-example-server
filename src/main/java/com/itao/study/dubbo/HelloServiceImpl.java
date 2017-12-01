package com.itao.study.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import stdu.dubbo.api.HelloService;

/**
 * @Description :dubbo服务提供方
 * @Author JunTao
 * @Date : 2017/12/1
 */
@Service(version = "1.0.0")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String str) {
        System.out.println("server say:"+str);
        return str;
    }
}
