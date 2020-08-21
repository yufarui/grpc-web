package com.ksfintech.grpc.server.convert;

import com.ksfintech.grpc.server.proto.ProtoServerService;
import io.grpc.ServerServiceDefinition;

/**
 * @date: 2020/8/21 11:11
 * @author: farui.yu
 */
public interface ProtoDefConverter {

    ServerServiceDefinition convert(ProtoServerService protoServerService);

    ServerServiceDefinition convertDirect(ProtoServerService protoServerService);
}
