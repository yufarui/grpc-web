package com.ksfintech.grpc.server.convert;

import com.ksfintech.grpc.server.config.GrpcProtoProperties;
import com.ksfintech.grpc.server.proto.ProtoServerService;
import com.ksfintech.grpc.server.servercall.HttpProtoServerCallFactory;
import io.grpc.ServerServiceDefinition;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @date: 2020/8/11 10:12
 * @author: farui.yu
 */
public class HttpProtoConverter extends AbstractProtoDefConverter {

    @Autowired
    private GrpcProtoProperties grpcProtoProperties;

    @Override
    public ServerServiceDefinition convert(ProtoServerService protoServerService) {

        changeService(grpcProtoProperties, protoServerService);

        return convertDirect(protoServerService);
    }

    @Override
    public ServerServiceDefinition convertDirect(ProtoServerService protoServerService) {
        return super.convertDirect(new HttpProtoServerCallFactory(), protoServerService);
    }

}
