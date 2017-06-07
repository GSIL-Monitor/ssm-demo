package com.fc.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by fangcong on 2016/12/5.
 * 缓存方法拦截器代码
 */
public class MethodCacheInterceptor implements MethodInterceptor,InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MethodCacheInterceptor.class);
    private Cache cache;

    public void setCache(Cache cache){
        this.cache = cache;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info(cache + " A cache is required. Use setCache(Cache) to provide one.");
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String targetName = invocation.getThis().getClass().getName();
        String methodName = invocation.getMethod().getName();
        Object[] arguments = invocation.getArguments();
        Object result;
        String cacheKey = getCacheKey(targetName,methodName,arguments);
        Element element;
        synchronized (this){
            element = cache.get(cacheKey);
            if (element == null){
                logger.info(cacheKey + "加入到缓存" + cache.getName());
                //调用实际的方法
                result = invocation.proceed();
                element = new Element(cacheKey,result);
                cache.put(element);
            } else {
                logger.info(cacheKey + "使用缓存" + cache.getName());
            }
        }
        return element.getObjectValue();
    }

    /**
     * 返回具体的方法 全路径名称 参数
     * @param targetName 全路径
     * @param methodName 方法名称
     * @param arguments  参数
     * @return 完整方法名称
     */
    private String getCacheKey(String targetName,String methodName,Object[] arguments){
        StringBuffer sb = new StringBuffer();
        sb.append(targetName).append(".").append(methodName);
        if (arguments != null && arguments.length != 0){
            for (int i = 0;i < arguments.length;i++){
                sb.append(".").append(arguments[i]);
            }
        }
        return sb.toString();
    }
}
