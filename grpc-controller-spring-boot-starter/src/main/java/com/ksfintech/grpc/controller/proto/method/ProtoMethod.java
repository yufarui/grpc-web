package com.ksfintech.grpc.controller.proto.method;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.ksfintech.grpc.controller.exception.GrpcException;
import com.ksfintech.grpc.controller.mapper.MapperBeanPostProcessor;
import com.ksfintech.grpc.controller.mapper.MapperMethod;
import com.ksfintech.grpc.controller.proto.stub.ProtoStub;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @date: 2020/8/19 15:22
 * @author: farui.yu
 */
@Data
@Slf4j
public abstract class ProtoMethod {

    private ProtoStub parent;
    private Method method;
    private String methodName;
    private Class<?> argType;
    private Class<?> returnType;

    /**
     * protoMethod 实际执行的方法
     *
     * @param grpcControllerMethod
     * @param grpcControllerArg
     * @return
     */
    public abstract Object invoke(Method grpcControllerMethod, Object grpcControllerArg);

    public MessageOrBuilder parseReq(Method grpcControllerMethod, Object grpcControllerArg) {
        MessageOrBuilder requestMessage;
        Parameter[] parameters = grpcControllerMethod.getParameters();

        if (parameters.length == 0) {
            requestMessage = parseReq();
        } else {
            Parameter parameter = parameters[0];
            requestMessage = parseReq(parameter.getType(), grpcControllerArg);
        }

        return requestMessage;
    }

    private MessageOrBuilder parseReq() {

        MessageOrBuilder requestMessage;
        try {
            Method newBuilderMethod = getArgType().getMethod("newBuilder");
            Message.Builder newBuilder = (Message.Builder)newBuilderMethod.invoke(new Object());
            requestMessage = newBuilder.build();
        } catch (Exception e) {
            throw new GrpcException("protoService[{}]方法[{}]类型[{}]解析无参失败",
                    getParent().getServiceName(), getMethodName(), this.getClass().getSimpleName(), e);

        }

        return requestMessage;
    }

    private MessageOrBuilder parseReq(Class<?> grpcControllerArgType, Object grpcControllerArg) {

        String reqId = grpcControllerArgType.getSimpleName() + "_" + getArgType().getSimpleName();
        MapperMethod reqMapperMethod = MapperBeanPostProcessor.getMapperMethodMap().get(reqId);
        MessageOrBuilder requestMessage;
        try {
            requestMessage = (MessageOrBuilder) reqMapperMethod.getMethod().invoke(reqMapperMethod.getBean(), grpcControllerArg);
        } catch (Exception e) {
            throw new GrpcException("protoService[{}]方法[{}]类型[{}]解析参数失败",
                    getParent().getServiceName(), getMethodName(), this.getClass().getSimpleName(), e);
        }

        return requestMessage;
    }

    public Object parseResponse(Class<?> grpcControllerReturnType, MessageOrBuilder returnMessage) {

        if (grpcControllerReturnType.getName().equals("void")) {
            return null;
        }

        String returnId = getReturnType().getSimpleName() + "_" + grpcControllerReturnType.getSimpleName();
        MapperMethod returnMapperMethod = MapperBeanPostProcessor.getMapperMethodMap().get(returnId);

        Object result;
        try {
            result = returnMapperMethod.getMethod().invoke(returnMapperMethod.getBean(), returnMessage);
        } catch (Exception e) {
            throw new GrpcException("protoService[{}]方法[{}]类型[{}]解析返参失败",
                    getParent().getServiceName(), getMethodName(), this.getClass().getSimpleName(), e);
        }

        return result;
    }
}
