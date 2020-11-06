package com.ksfintech.grpc.server.config;

import com.ksfintech.grpc.server.proto.ProtoServerMethod;
import com.ksfintech.grpc.server.proto.ProtoServerService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @date: 2020/8/4 15:00
 * @author: farui.yu
 */
@Data
@ConfigurationProperties("grpc.proto")
public class GrpcProtoProperties {

    @Value("${:true}")
    private boolean serviceNameSimplifyEnable;

    private String httpAddress;

    // key => serviceName
    private Map<String, ProtoServerService> service;

    public ProtoServerService getService(String serviceName) {

        String upper = serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1);
        ProtoServerService protoServerService = service.get(upper);

        if (protoServerService == null) {
            String lower = serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1);
            return service.get(lower);
        }

        return protoServerService;
    }
}
