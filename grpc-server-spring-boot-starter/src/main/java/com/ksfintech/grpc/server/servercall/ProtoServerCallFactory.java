package com.ksfintech.grpc.server.servercall;

import com.ksfintech.grpc.server.proto.ProtoServerService;

/**
 * @date: 2020/8/21 11:19
 * @author: farui.yu
 */
public interface ProtoServerCallFactory {

    ProtoServerCall create(ProtoServerService protoServerService, String methodId);
}
