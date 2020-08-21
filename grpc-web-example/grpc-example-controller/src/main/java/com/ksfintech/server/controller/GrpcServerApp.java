package com.ksfintech.server.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @date: 2020/7/28 11:21
 * @author: farui.yu
 */
@SpringBootApplication
public class GrpcServerApp {
    public static void main(String[] args) {
        SpringApplication.run(GrpcServerApp.class, args);
    }
}
