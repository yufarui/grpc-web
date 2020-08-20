package com.ksfintech.grpc.web.proto.method;

import com.google.common.util.concurrent.*;
import com.google.protobuf.MessageOrBuilder;
import com.ksfintech.grpc.web.exception.GrpcException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @date: 2020/8/19 15:19
 * @author: farui.yu
 */
@Slf4j
public class ProtoFutureMethod extends ProtoMethod {

    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() + 1, 2 * Runtime.getRuntime().availableProcessors(),
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(Integer.MAX_VALUE),
            r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("ProtoFutureMethod.worker");
                return thread;
            })
    );

    @Override
    public Object invoke(Method grpcControllerMethod, Object grpcControllerArg) {

        MessageOrBuilder requestMessage = parseReq(grpcControllerMethod, grpcControllerArg);

        ListenableFuture<MessageOrBuilder> listenableFuture = invoke(requestMessage);

        Futures.addCallback(listenableFuture,
                new FutureCallback<MessageOrBuilder>() {
                    @Override
                    public void onSuccess(MessageOrBuilder result) {
                        log.info("protoService[{}]方法[{}]类型[{}]调用成功,结果[{}]",
                                getParent().getServiceName(), getMethodName(),
                                ProtoFutureMethod.class.getSimpleName(), result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        log.error("protoService[{}]方法[{}]类型[{}]调用失败",
                                getParent().getServiceName(), getMethodName(),
                                ProtoFutureMethod.class.getSimpleName(), t);
                    }
                }, executorService);

        return null;
    }

    public ListenableFuture<MessageOrBuilder> invoke(MessageOrBuilder arg) {
        try {
            Object result = getMethod().invoke(getParent().getAbstractStub(), arg);
            return (ListenableFuture<MessageOrBuilder>) result;
        } catch (Exception e) {
            throw new GrpcException("protoService[{}]方法[{}]类型[{}]调用失败",
                    getParent().getServiceName(), getMethodName(), this.getClass().getSimpleName(), e);
        }
    }

}
