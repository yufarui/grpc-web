package com.ksfintech.grpc.web.manager;

import com.ksfintech.grpc.web.proto.ProtoService;
import io.grpc.ManagedChannel;
import org.springframework.boot.CommandLineRunner;

/**
 * @date: 2020/8/20 8:59
 * @author: farui.yu
 */
public interface ProtoServiceManager extends CommandLineRunner {

    /**
     * 创建channel
     * @param protoServices
     * @return
     */
    ManagedChannel create(ProtoService protoServices);
}
