
package com.ksfintech.grpc.web.mapper;


import com.google.protobuf.MessageOrBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 映射转换处理,只对 protobuf相关的转换进行处理
 *
 * @author farui.yu
 */
public class MapperBeanPostProcessor implements BeanPostProcessor {

    private static Map<String, MapperMethod> mapperMethodMap = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();
        if (beanClass.getInterfaces().length != 1) {
            return bean;
        }

        Method[] methods = beanClass.getMethods();

        for (Method method : methods) {

            Class<?> returnType = method.getReturnType();

            if (method.getParameters() != null && method.getParameters().length == 1) {
                Class<?> argType = method.getParameters()[0].getType();
                if (ClassUtils.isAssignable(MessageOrBuilder.class, argType)) {
                    MapperMethod mapperMethod = new MapperMethod();
                    String id = argType.getSimpleName() + "_" + returnType.getSimpleName();
                    mapperMethod.setId(id);
                    mapperMethod.setBean(bean);
                    mapperMethod.setMethod(method);
                    mapperMethodMap.put(id, mapperMethod);
                }
            }

            if (ClassUtils.isAssignable(MessageOrBuilder.class, returnType)) {
                MapperMethod mapperMethod = new MapperMethod();
                String id = method.getParameters()[0].getType().getSimpleName() + "_" + returnType.getSimpleName();
                mapperMethod.setId(id);
                mapperMethod.setBean(bean);
                mapperMethod.setMethod(method);
                mapperMethodMap.put(id, mapperMethod);
            }
        }
        return bean;
    }

    public static Map<String, MapperMethod> getMapperMethodMap() {
        return mapperMethodMap;
    }
}