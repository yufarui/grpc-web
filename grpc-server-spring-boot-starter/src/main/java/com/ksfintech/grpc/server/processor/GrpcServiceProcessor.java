package com.ksfintech.grpc.server.processor;


import io.grpc.ServerServiceDefinition;

import java.util.List;

/**
 * 发现ServerServiceDefinition的处理器
 *
 * @date: 2020/8/4 15:12
 * @author: farui.yu
 */
public interface GrpcServiceProcessor {

    List<ServerServiceDefinition> findGrpcServices();

}
