package com.ksfintech.grpc.web.util;

import com.ksfintech.grpc.web.exception.GrpcException;
import com.ksfintech.grpc.web.proto.ProtoEnum;
import com.ksfintech.grpc.web.proto.ProtoService;
import com.ksfintech.grpc.web.proto.method.ProtoMethod;
import com.ksfintech.grpc.web.proto.stub.ProtoStub;
import io.grpc.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date: 2020/8/17 15:45
 * @author: farui.yu
 */
@Slf4j
public class ProtoServiceUtil {

    public static Map<String, ProtoService> findClasses(String packageName, File dir) {

        Map<String, ProtoService> protoServiceMap = new HashMap<>();

        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("[{}]包中不存在类", dir.getName());
            return protoServiceMap;
        }

        File[] files = dir.listFiles((file) -> file.getName().endsWith("Grpc.class"));

        if (files == null || files.length == 0) {
            return protoServiceMap;
        }

        for (File file : files) {

            String className = file.getName().substring(0, file.getName().length() - 6);

            if (className.contains("$")) {
                continue;
            }

            Class<?> clazz;
            String serviceName;
            try {
                clazz = ClassUtils.forName(packageName + '.' + className,
                        Thread.currentThread().getContextClassLoader());
                Field field = clazz.getField("SERVICE_NAME");
                serviceName = (String) field.get(new Object());

            } catch (Exception e) {
                throw new GrpcException("反射失败,packageName:{},className:{}", packageName, className, e);
            }

            String protoSimpleName = className.substring(0, className.length() - "Grpc".length());

            ProtoService protoService = createProtoService(clazz, serviceName);

            protoServiceMap.put(protoSimpleName, protoService);
        }

        return protoServiceMap;
    }

    public static ProtoService createProtoService(Class<?> clazz, String serviceName) {
        ProtoService protoService;
        try {
            protoService = new ProtoService();
            protoService.setProtoClass(clazz);
            protoService.setProtoClassName(clazz.getSimpleName());
            protoService.setServiceName(serviceName);

            Map<ProtoEnum, ProtoStub> protoStubMap = new ConcurrentHashMap<>(16);

            protoService.setProtoStubMap(protoStubMap);

            protoStubMap.put(ProtoEnum.BLOCKING, createProtoStub(clazz, protoService, ProtoEnum.BLOCKING));
            protoStubMap.put(ProtoEnum.FUTURE, createProtoStub(clazz, protoService, ProtoEnum.FUTURE));
            protoStubMap.put(ProtoEnum.REACTIVE, createProtoStub(clazz, protoService, ProtoEnum.REACTIVE));

        } catch (Exception e) {
            throw new GrpcException("clazz[{}],serviceName[{}]创建blockingStubChannel异常", clazz, serviceName, e);
        }
        return protoService;
    }

    private static ProtoStub createProtoStub(Class<?> clazz, ProtoService protoService, ProtoEnum protoEnum) {

        ProtoStub protoStub = ProtoEnum.chooseStub(protoEnum);
        protoStub.setParent(protoService);
        protoStub.setServiceName(protoService.getServiceName());
        Method stubMethod;
        try {
            stubMethod = clazz.getMethod(ProtoEnum.chooseStubString(protoEnum), Channel.class);
        } catch (Exception e) {
            throw new GrpcException("无法加载方法serviceName[{}],类型[{}]",
                    protoService.getServiceName(), protoEnum.name());
        }

        Class<?> stubReturnType = stubMethod.getReturnType();
        Method[] declaredMethods = stubReturnType.getDeclaredMethods();

        Map<String, ProtoMethod> protoMethodMap = new ConcurrentHashMap<>(16);

        for (Method method : declaredMethods) {

            if (!StringUtils.equals(method.getName(), "build")) {
                ProtoMethod protoMethod = createProtoMethod(protoEnum, protoStub, method);
                protoMethodMap.put(method.getName(), protoMethod);
            }
        }
        protoStub.setStubMethodMap(protoMethodMap);
        return protoStub;
    }

    private static ProtoMethod createProtoMethod(ProtoEnum protoEnum, ProtoStub protoStub, Method method) {
        ProtoMethod protoMethod = ProtoEnum.chooseMethod(protoEnum);

        protoMethod.setMethod(method);
        protoMethod.setMethodName(method.getName());

        if (protoEnum.equals(ProtoEnum.BLOCKING)) {
            protoMethod.setArgType(method.getParameters()[0].getType());
            protoMethod.setReturnType(method.getReturnType());
        } else {
            ProtoMethod blockingMethod = protoStub.getParent()
                    .getProtoStubMap(ProtoEnum.BLOCKING)
                    .getStubMethodMap(method.getName());

            protoMethod.setArgType(blockingMethod.getArgType());
            protoMethod.setReturnType(blockingMethod.getReturnType());
        }

        protoMethod.setParent(protoStub);
        return protoMethod;
    }
}
