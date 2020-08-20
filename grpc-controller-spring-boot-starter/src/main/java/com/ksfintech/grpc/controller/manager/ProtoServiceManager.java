package com.ksfintech.grpc.controller.manager;

import com.ksfintech.grpc.controller.proto.ProtoService;
import io.grpc.ManagedChannel;
import org.springframework.boot.CommandLineRunner;

/**
 * channel的注册,支持自定义配置
 *
 * @date: 2020/8/20 8:59
 * @author: farui.yu
 */
public interface ProtoServiceManager extends CommandLineRunner {

    /**
     * 创建channel
     *
     * @param protoServices
     * @return
     */
    ManagedChannel create(ProtoService protoServices);
}
