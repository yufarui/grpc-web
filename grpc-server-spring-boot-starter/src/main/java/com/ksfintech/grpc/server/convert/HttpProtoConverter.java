package com.ksfintech.grpc.server.convert;

import com.ksfintech.grpc.server.config.GrpcProtoProperties;
import com.ksfintech.grpc.server.exception.GrpcException;
import com.ksfintech.grpc.server.proto.ProtoServerService;
import com.ksfintech.grpc.server.servercall.HttpProtoServerCallFactory;
import io.grpc.ServerServiceDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * @date: 2020/8/11 10:12
 * @author: farui.yu
 */
@Slf4j
public class HttpProtoConverter extends AbstractProtoDefConverter {

    @Autowired
    private GrpcProtoProperties grpcProtoProperties;

    @Override
    public ServerServiceDefinition convert(ProtoServerService protoServerService) {

        changeService(grpcProtoProperties, protoServerService);

        check(protoServerService);

        return convertDirect(protoServerService);
    }

    private void check(ProtoServerService protoServerService) {
        if(StringUtils.isEmpty(protoServerService.getHttpAddress())) {
            throw new GrpcException("服务[{}]不存在Http地址", protoServerService.getServiceName());
        }

        protoServerService.getMethodMap().forEach((methodName, protoServerMethod) -> {
            if (StringUtils.isEmpty(protoServerMethod.getUrlPath())) {
                throw new GrpcException("服务[{}]方法[{}]不存在映射的url", protoServerService.getServiceName(), methodName);
            }
        });
    }

    @Override
    public ServerServiceDefinition convertDirect(ProtoServerService protoServerService) {
        return super.convertDirect(new HttpProtoServerCallFactory(), protoServerService);
    }

}
