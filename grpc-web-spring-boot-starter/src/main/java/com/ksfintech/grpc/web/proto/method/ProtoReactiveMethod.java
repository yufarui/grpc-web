package com.ksfintech.grpc.web.proto.method;

import com.google.protobuf.MessageOrBuilder;
import com.ksfintech.grpc.web.exception.GrpcException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @date: 2020/8/19 16:04
 * @author: farui.yu
 */
@Slf4j
public class ProtoReactiveMethod extends ProtoMethod {

    @Override
    public Object invoke(Method grpcControllerMethod, Object grpcControllerArg) {

        MessageOrBuilder requestMessage = parseReq(grpcControllerMethod, grpcControllerArg);

        StreamObserver<MessageOrBuilder> responseObserver = new StreamObserver<MessageOrBuilder>() {
            @Override
            public void onNext(MessageOrBuilder result) {
                log.info("protoService[{}]方法[{}]类型[{}]调用成功,结果[{}]",
                        getParent().getServiceName(), getMethodName(),
                        ProtoReactiveMethod.class.getSimpleName(), result);
            }

            @Override
            public void onError(Throwable t) {
                log.error("protoService[{}]方法[{}]类型[{}]调用失败",
                        getParent().getServiceName(), getMethodName(),
                        ProtoReactiveMethod.class.getSimpleName(), t);
            }

            @Override
            public void onCompleted() {
                log.info("protoService[{}]方法[{}]类型[{}]调用结束",
                        getParent().getServiceName(), getMethodName(),
                        this.getClass().getSimpleName());
            }
        };

        invoke(requestMessage, responseObserver);

        return null;
    }

    public void invoke(MessageOrBuilder arg, StreamObserver<MessageOrBuilder> observer) {
        try {
            getMethod().invoke(getParent().getAbstractStub(), arg, observer);
        } catch (Exception e) {
            throw new GrpcException("protoService[{}]方法[{}]类型[{}]调用失败",
                    getParent().getServiceName(), getMethodName(), this.getClass().getSimpleName(), e);
        }
    }
}
