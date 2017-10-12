package com.fc.demo.reflect;

import java.lang.reflect.Method;

/**
 * Created by fangcong on 2017/4/18.
 */
public class HelloServiceMain {

    /**
     * 测试CGLIB代理
     * @param args
     */
    public static void main(String[] args) throws Exception{
        //1.直接new对象
        HelloService helloService = new HelloServiceImpl();
        helloService.sayHello("normal : hello world!");
        //2.反射
        Object service = Class.forName(HelloServiceImpl.class.getName()).newInstance();
        Method method = service.getClass().getMethod("sayHello", String.class);
        method.invoke(service, "reflect : hello world!");
        //3.JDK动态代理
        HelloServiceProxy helloServiceProxy = new HelloServiceProxy();
        HelloService jdkProxy = (HelloService) helloServiceProxy.bind(new HelloServiceImpl());
        jdkProxy.sayHello("hello world!");
        //4.CGLIB代理
        CGLIBHelloService cglibHelloService = new CGLIBHelloService();
        HelloService proxy = (HelloService) cglibHelloService.getInstance(new HelloServiceImpl());
        proxy.sayHello("hello world!");
    }
}
