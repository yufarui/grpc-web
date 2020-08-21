package com.ksfintech.grpc.server.controller;

import com.ksfintech.grpc.server.convert.ProtoDefConverter;
import com.ksfintech.grpc.server.manager.GrpcServiceManager;
import com.ksfintech.grpc.server.convert.HttpProtoConverter;
import com.ksfintech.grpc.server.proto.ProtoServerService;
import com.orientsec.grpc.common.model.BusinessResult;
import io.grpc.ServerServiceDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @date: 2020/8/10 18:18
 * @author: farui.yu
 */
@RestController
@RequestMapping("/grpc")
public class GrpcServiceController {

    @Autowired
    private GrpcServiceManager grpcServiceManager;

    @Autowired
    private ProtoDefConverter protoDefConverter;

    @PostMapping("/update")
    public BusinessResult update(@RequestBody ProtoServerService protoServerService) {
        ServerServiceDefinition def = protoDefConverter.convertDirect(protoServerService);
        return grpcServiceManager.update(def);
    }

    @PostMapping("/add")
    public BusinessResult add(@RequestBody ProtoServerService protoServerService) {
        ServerServiceDefinition def = protoDefConverter.convertDirect(protoServerService);
        return grpcServiceManager.add(def);
    }

    @PostMapping("/delete")
    public BusinessResult delete(@RequestBody ProtoServerService protoServerService) {
        ServerServiceDefinition def = protoDefConverter.convertDirect(protoServerService);
        return grpcServiceManager.delete(def);
    }
}
