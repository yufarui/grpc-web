package com.ksfintech.grpc.server.servercall;

import com.ksfintech.grpc.server.proto.ProtoServerService;

/**
 * @date: 2020/8/21 11:29
 * @author: farui.yu
 */
public class HttpProtoServerCallFactory implements ProtoServerCallFactory {

    @Override
    public ProtoServerCall create(ProtoServerService protoServerService, String methodId) {
        return new HttpProtoServerCall(protoServerService, methodId);
    }

}
