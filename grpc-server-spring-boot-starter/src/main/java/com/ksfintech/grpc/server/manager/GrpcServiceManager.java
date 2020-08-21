package com.ksfintech.grpc.server.manager;

import com.orientsec.grpc.common.model.BusinessResult;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;

import java.util.List;

/**
 * @date: 2020/8/10 18:24
 * @author: farui.yu
 */
public interface GrpcServiceManager {

    void start();

    void stop();

    BusinessResult update(ServerServiceDefinition serverServiceDefinitions);

    BusinessResult add(ServerServiceDefinition serverServiceDefinitions);

    BusinessResult delete(ServerServiceDefinition serviceDefinition);

//    List<ServiceDescriptor> query();
}
