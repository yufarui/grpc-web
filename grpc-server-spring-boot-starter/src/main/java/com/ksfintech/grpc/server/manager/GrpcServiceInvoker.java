package com.ksfintech.grpc.server.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

/**
 * @date: 2020/8/6 9:46
 * @author: farui.yu
 */
@Slf4j
public class GrpcServiceInvoker implements CommandLineRunner, DisposableBean {

    @Autowired
    private GrpcServiceManager grpcServiceManager;

    @Override
    public void run(String... args) throws Exception {
        grpcServiceManager.start();
    }

    @Override
    public void destroy() throws Exception {
        grpcServiceManager.stop();
    }
}
