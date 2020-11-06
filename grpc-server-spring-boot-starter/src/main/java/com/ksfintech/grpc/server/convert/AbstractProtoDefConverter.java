package com.ksfintech.grpc.server.convert;

import com.google.protobuf.Message;
import com.ksfintech.grpc.server.config.GrpcProtoProperties;
import com.ksfintech.grpc.server.exception.GrpcException;
import com.ksfintech.grpc.server.proto.ProtoServerMethod;
import com.ksfintech.grpc.server.proto.ProtoServerService;
import com.ksfintech.grpc.server.servercall.ProtoServerCallFactory;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ServerCalls;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date: 2020/8/21 11:14
 * @author: farui.yu
 */
public abstract class AbstractProtoDefConverter implements ProtoDefConverter {

    @Autowired
    private GrpcProtoProperties grpcProtoProperties;

    /**
     * httpAddress 优先级
     * grpcProtoProperties.httpAddress (1)
     * proto File.httpAddress (2)
     * grpcProtoProperties.service.httpAddress (3)
     * (1)<(2)<(3)
     * urlPath 优先级 protoFile < grpcProtoProperties
     *
     * @param grpcProtoProperties
     * @param protoServerService
     */
    protected void changeService(GrpcProtoProperties grpcProtoProperties, ProtoServerService protoServerService) {

        if (StringUtils.isEmpty(protoServerService.getHttpAddress())) {
            protoServerService.setHttpAddress(grpcProtoProperties.getHttpAddress());
        }

        ProtoServerService serviceProperties = grpcProtoProperties.getService(protoServerService.getServiceName());

        if (serviceProperties == null) {
            return;
        }

        if (StringUtils.isNotEmpty(serviceProperties.getHttpAddress())) {
            protoServerService.setHttpAddress(serviceProperties.getHttpAddress());
        }

        protoServerService.getMethodMap().forEach((key, value) -> {
            ProtoServerMethod methodProperties = serviceProperties.getMethodMap(key);
            if (StringUtils.isNotEmpty(methodProperties.getUrlPath())) {
                value.setUrlPath(methodProperties.getUrlPath());
            }
        });
    }

    protected ServerServiceDefinition convertDirect(ProtoServerCallFactory protoServerCallFactory, ProtoServerService protoServerService) {

        Map<String, ProtoServerMethod> protoServerMethodMap = protoServerService.getMethodMap();
        Map<String, MethodDescriptor> methodDescriptorMap = new ConcurrentHashMap<>();

        String serviceName = serviceNameHandler(protoServerService);

        ServiceDescriptor.Builder serviceBuilder = ServiceDescriptor.newBuilder(serviceName);

        protoServerMethodMap.forEach((methodName, protoServerMethod) -> {

            Message argMessage = getMessage(protoServerMethod.getJavaArgType());
            Message returnMessage = getMessage(protoServerMethod.getJavaReturnType());

            MethodDescriptor<Message, Message> methodDescriptor = MethodDescriptor.<Message, Message>newBuilder()
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(
                            serviceName,
                            protoServerMethod.getMethodName()))
                    .setType(chooseType(protoServerMethod))
                    .setSampledToLocalTracing(true)
                    .setRequestMarshaller(ProtoUtils.marshaller(argMessage))
                    .setResponseMarshaller(ProtoUtils.marshaller(returnMessage))
                    .build();

            methodDescriptorMap.put(methodName, methodDescriptor);
            serviceBuilder.addMethod(methodDescriptor);
        });

        ServerServiceDefinition.Builder serverServiceBuilder = ServerServiceDefinition.builder(serviceBuilder.build());

        methodDescriptorMap.forEach((methodName, methodDescriptor) -> {
            serverServiceBuilder.addMethod(methodDescriptor, ServerCalls.asyncUnaryCall(
                    protoServerCallFactory.create(protoServerService, methodName)));
        });

        return serverServiceBuilder.build();
    }

    private String serviceNameHandler(ProtoServerService protoServerService) {
        String serviceName;
        if (grpcProtoProperties.isServiceNameSimplifyEnable()) {
            serviceName = protoServerService.getServiceName();
        } else {
            serviceName = protoServerService.getProtoPackage() + "." + protoServerService.getServiceName();
        }
        return serviceName;
    }

    protected MethodDescriptor.MethodType chooseType(ProtoServerMethod protoServerMethod) {

        boolean argStream = protoServerMethod.isArgStream();
        boolean returnStream = protoServerMethod.isReturnStream();
        if (!argStream && !returnStream) {
            return MethodDescriptor.MethodType.UNARY;
        } else if (argStream && !returnStream) {
            return MethodDescriptor.MethodType.CLIENT_STREAMING;
        } else if (!argStream) {
            return MethodDescriptor.MethodType.SERVER_STREAMING;
        } else {
            return MethodDescriptor.MethodType.BIDI_STREAMING;
        }
    }

    protected Message getMessage(String javaType) {
        Class<?> type;
        Message.Builder builder;
        try {
            type = ClassUtils.forName(javaType, getClass().getClassLoader());
            Method newBuilderMethod = type.getMethod("newBuilder");

            builder = (Message.Builder) newBuilderMethod.invoke(new Object());
        } catch (Exception e) {
            throw new GrpcException("无法获取到message,javaType[{}]", javaType, e);
        }
        return builder.build();
    }
}
