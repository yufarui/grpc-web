package com.ksfintech.grpc.web.proto.method;

import com.google.protobuf.MessageOrBuilder;
import com.ksfintech.grpc.web.exception.GrpcException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @date: 2020/8/19 14:39
 * @author: farui.yu
 */
@Slf4j
public class ProtoBlockingMethod extends ProtoMethod {

    @Override
    public Object invoke(Method grpcControllerMethod, Object grpcControllerArg) {

        MessageOrBuilder requestMessage = parseReq(grpcControllerMethod, grpcControllerArg);

        MessageOrBuilder returnMessage = invoke(requestMessage);

        return parseResponse(grpcControllerMethod.getReturnType(), returnMessage);
    }

    public MessageOrBuilder invoke(MessageOrBuilder arg) {
        try {
            Object result = getMethod().invoke(getParent().getAbstractStub(), arg);
            return (MessageOrBuilder) result;
        } catch (Exception e) {
            throw new GrpcException("protoService[{}]方法[{}]调用失败",
                    getParent().getServiceName(), getMethodName(), e);
        }
    }
}
