package com.ksfintech.grpc.web.manager;

import com.ksfintech.grpc.web.proto.ProtoEnum;
import com.ksfintech.grpc.web.proto.ProtoService;
import com.ksfintech.grpc.web.proto.ProtoServiceContainer;
import com.ksfintech.grpc.web.proto.stub.ProtoStub;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @date: 2020/8/17 8:45
 * @author: farui.yu
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 490)
public class DefaultProtoManager implements ProtoServiceManager {

    @Autowired
    private ProtoServiceContainer protoServiceContainer;

    @Override
    public void run(String... args) throws Exception {
        Map<String, ProtoService> protoServiceMap = protoServiceContainer.getProtoServiceMap();

        if (CollectionUtils.isEmpty(protoServiceMap)) {
            log.info("不存在protoService");
            return;
        }
        Collection<ProtoService> values = protoServiceMap.values();
        for (ProtoService protoService : values) {

            Class<?> grpcClass = protoService.getProtoClass();
            ManagedChannel channel = create(protoService);

            Map<ProtoEnum, ProtoStub> protoStubMap = protoService.getProtoStubMap();
            for (ProtoStub protoStub : protoStubMap.values()) {
                Method newBlockingStub = grpcClass.getMethod(ProtoEnum.chooseStubString(protoStub.getProtoEnum()),
                        Channel.class);
                AbstractStub<?> stub = (AbstractStub<?>) newBlockingStub.invoke(new Object(), channel);
                protoStub.setAbstractStub(stub);
            }

            log.info("注册[{}]结束", protoService.getServiceName());
        }
    }


    @Override
    public ManagedChannel create(ProtoService protoServices) {

        String serviceName = protoServices.getServiceName();

        String target = "zookeeper:///" + serviceName;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        return channel;
    }
}
