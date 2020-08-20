package com.ksfintech.grpc.controller.autoconfig;

import com.ksfintech.grpc.controller.grpcmapping.GrpcController;
import com.ksfintech.grpc.controller.grpcmapping.GrpcMapping;
import com.ksfintech.grpc.controller.proto.ProtoEnum;
import com.ksfintech.grpc.controller.proto.ProtoServiceContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理,调用 protoMethod的invoke方法
 *
 * @date: 2020/8/4 15:45
 * @author: farui.yu
 */
@Slf4j
public class GrpcControllerFactoryBean<T> implements FactoryBean<T> {

    private Class<T> grpcClass;

    @Autowired
    private ProtoServiceContainer protoServiceContainer;

    public GrpcControllerFactoryBean() {
    }

    public GrpcControllerFactoryBean(Class<T> grpcClass) {
        this.grpcClass = grpcClass;
    }

    @Override
    public T getObject() throws Exception {
        return createGrpcInvoker(grpcClass);
    }

    @Override
    public Class<?> getObjectType() {
        return grpcClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private T createGrpcInvoker(Class<T> grpcClass) {
        Object instance = Proxy.newProxyInstance(grpcClass.getClassLoader(), new Class[]{grpcClass},
                new GrpcMappingInvocationHandler()
        );
        return (T) instance;
    }

    private class GrpcMappingInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            String methodName = method.getName();

            if (StringUtils.equals(methodName, "equals")) {
                return false;
            }

            GrpcMapping grpcMapping = AnnotationUtils.findAnnotation(method, GrpcMapping.class);
            if (grpcMapping == null) {
                return null;
            }
            ProtoEnum protoEnum = grpcMapping.protoEnum();

            String protoMethodName = grpcMapping.protoMethodName();
            if (!StringUtils.isBlank(protoMethodName)) {
                methodName = protoMethodName;
            }

            GrpcController grpcController = AnnotationUtils.getAnnotation(grpcClass, GrpcController.class);
            String grpcService = grpcController.grpcService();
            String standardId = grpcService.substring(0, 1).toUpperCase() + grpcService.substring(1);

            Object arg0 = args.length == 0 ? null : args[0];

            return protoServiceContainer.getProtoServiceMap(standardId)
                    .getProtoStubMap(protoEnum)
                    .getStubMethodMap(methodName)
                    .invoke(method, arg0);
        }
    }
}

