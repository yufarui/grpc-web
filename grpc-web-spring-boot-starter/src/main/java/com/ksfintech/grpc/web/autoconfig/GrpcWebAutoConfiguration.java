package com.ksfintech.grpc.web.autoconfig;


import com.ksfintech.grpc.web.manager.DefaultProtoManager;
import com.ksfintech.grpc.web.manager.ProtoServiceManager;
import com.ksfintech.grpc.web.mapper.MapperBeanPostProcessor;
import com.ksfintech.grpc.web.proto.ProtoServiceScanner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @date: 2020/8/14 9:56
 * @author: farui.yu
 */
@Configuration
@ConditionalOnBean(annotation = ProtoServiceScanner.class)
@Import({GrpcControllerRegistrar.class})
public class GrpcWebAutoConfiguration {

    @Bean
    public GrpcControllerFactoryBean<?> grpcControllerFactoryBean() {
        return new GrpcControllerFactoryBean<>();
    }

    @Bean
    public MapperBeanPostProcessor mapperBeanPostProcessor() {
        return new MapperBeanPostProcessor();
    }

    @Bean
    @ConditionalOnProperty(value = "grpc.check.enable", matchIfMissing = true, havingValue = "true")
    public CheckGrpcControllerBean checkGrpcControllerBean() {
        return new CheckGrpcControllerBean();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtoServiceManager protoServiceManager() {
        return new DefaultProtoManager();
    }
}
