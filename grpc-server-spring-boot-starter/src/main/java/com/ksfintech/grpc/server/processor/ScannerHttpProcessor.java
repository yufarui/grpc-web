package com.ksfintech.grpc.server.processor;

import com.ksfintech.grpc.server.config.GrpcProtoProperties;
import com.ksfintech.grpc.server.convert.HttpProtoConverter;
import com.ksfintech.grpc.server.convert.ProtoDefConverter;
import com.ksfintech.grpc.server.proto.ProtoContainer;
import com.ksfintech.grpc.server.proto.ProtoFile;
import com.ksfintech.grpc.server.proto.ProtoServerService;
import io.grpc.ServerServiceDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @date: 2020/8/10 15:12
 * @author: farui.yu
 */
@Slf4j
public class ScannerHttpProcessor implements GrpcServiceProcessor {

    @Autowired
    private ProtoContainer protoContainer;

    @Autowired
    @Qualifier("httpProtoConverter")
    private ProtoDefConverter protoDefConverter;

    @Override
    public List<ServerServiceDefinition> findGrpcServices() {

        List<ServerServiceDefinition> serviceDefinitionList = new ArrayList<>();

        List<ProtoFile> protoFiles = protoContainer.getProtoFiles();

        if (CollectionUtils.isEmpty(protoFiles)) {
            return serviceDefinitionList;
        }

        for (ProtoFile protoFile : protoFiles) {
            List<ProtoServerService> protoServerServices = protoFile.getServerServices();

            if (CollectionUtils.isEmpty(protoServerServices)) {
                continue;
            }
            for (ProtoServerService protoServerService : protoServerServices) {
                serviceDefinitionList.add(protoDefConverter.convert(protoServerService));
            }
        }

        return serviceDefinitionList;
    }
}
