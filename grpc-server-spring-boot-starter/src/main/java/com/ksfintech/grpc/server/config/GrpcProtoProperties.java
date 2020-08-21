package com.ksfintech.grpc.server.config;

import com.ksfintech.grpc.server.proto.ProtoServerMethod;
import com.ksfintech.grpc.server.proto.ProtoServerService;
import lombok.Data;
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

    private String httpAddress;

    // key => serviceName
    private Map<String, ProtoServerService> service;

    public ProtoServerService getService(String serviceName) {
        ProtoServerService protoServerService = service.get(serviceName);

        if (protoServerService == null) {
            serviceName = serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1);
            return service.get(serviceName);
        }

        return protoServerService;
    }
}
