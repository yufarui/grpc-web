package com.ksfintech.grpc.server.servercall;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.ksfintech.grpc.server.config.SpringContext;
import com.ksfintech.grpc.server.exception.GrpcException;
import com.ksfintech.grpc.server.proto.ProtoServerMethod;
import com.ksfintech.grpc.server.proto.ProtoServerService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;

/**
 * @date: 2020/8/11 11:04
 * @author: farui.yu
 */
@Slf4j
public class HttpProtoServerCall implements ProtoServerCall {

    private ProtoServerService protoServerService;
    private String methodId;

    public HttpProtoServerCall(ProtoServerService protoServerService, String methodId) {
        this.protoServerService = protoServerService;
        this.methodId = methodId;
    }

    @Override
    public void invoke(Object request, StreamObserver responseObserver) {
        String build;
        try {
            build = JsonFormat.printer().print((MessageOrBuilder) request);
        } catch (Exception e) {
            throw new GrpcException("请求转换json失败", e);
        }

        ProtoServerMethod protoServerMethod = protoServerService.getMethodMap(methodId);

        String url = "http://" + protoServerService.getHttpAddress() + protoServerMethod.getUrlPath();

        JSONObject result = getRestTemplate().postForObject(url, JSON.parseObject(build), JSONObject.class);

        if (result == null) {
            log.info("请求结果为空");
            responseObserver.onCompleted();
            return;
        }
        String javaReturnType = protoServerMethod.getJavaReturnType();

        try {
            Class<?> javaReturnClass = ClassUtils.forName(javaReturnType, getClass().getClassLoader());
            Method newBuilderMethod = javaReturnClass.getMethod("newBuilder");
            Message.Builder builder = (Message.Builder) newBuilderMethod.invoke(new Object());

            JsonFormat.parser().merge(result.toJSONString(), builder);
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            throw new GrpcException("结果转换json失败", e);
        }
    }

    protected RestTemplate getRestTemplate() {
        return SpringContext.getBean(RestTemplate.class);
    }

    @Override
    public StreamObserver<?> invoke(StreamObserver responseObserver) {
        throw new AssertionError();
    }

}
