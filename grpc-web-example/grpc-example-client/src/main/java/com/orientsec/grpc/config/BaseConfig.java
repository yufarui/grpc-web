package com.orientsec.grpc.config;

import com.ksfintech.grpc.controller.proto.ProtoServiceScanner;
import org.springframework.context.annotation.Configuration;

/**
 * @date: 2020/8/17 16:07
 * @author: farui.yu
 */
@Configuration
@ProtoServiceScanner("com.ksfintech.proto.service")
public class BaseConfig {
}
