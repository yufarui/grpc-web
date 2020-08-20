package com.ksfintech.grpc.controller.mapper;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @date: 2020/8/14 18:45
 * @author: farui.yu
 */
@Data
public class MapperMethod {

    private String id;
    private Object bean;
    private Method method;

}
