package com.orientsec.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @date: 2020/8/4 15:42
 * @author: farui.yu
 */
@SpringBootApplication
public class GrpcServerApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(GrpcServerApp.class, args);
    }
}
