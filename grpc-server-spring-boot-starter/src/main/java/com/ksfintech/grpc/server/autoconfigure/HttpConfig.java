package com.ksfintech.grpc.server.autoconfigure;

import com.ksfintech.grpc.server.convert.HttpProtoConverter;
import com.ksfintech.grpc.server.convert.ProtoDefConverter;
import com.ksfintech.grpc.server.processor.GrpcServiceProcessor;
import com.ksfintech.grpc.server.processor.ScannerHttpProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @date: 2020/8/21 13:12
 * @author: farui.yu
 */
@Configuration
@ConditionalOnProperty(value = "proto.http.enable", matchIfMissing = true, havingValue = "true")
public class HttpConfig {

    @Bean
    public GrpcServiceProcessor scannerHttpProcessor() {
        return new ScannerHttpProcessor();
    }

    @Bean
    public ProtoDefConverter httpProtoConverter() {
        return new HttpProtoConverter();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                // 连接主机的超时时间
                .setConnectTimeout(Duration.ofNanos(10000))
                // 读取数据的超时时间
                .setReadTimeout(Duration.ofNanos(10000))
                .build();
    }
}
