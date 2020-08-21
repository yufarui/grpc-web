package com.ksfintech.grpc.server.autoconfigure;

import com.ksfintech.grpc.server.config.GrpcProtoProperties;
import com.ksfintech.grpc.server.config.SpringContext;
import com.ksfintech.grpc.server.convert.HttpProtoConverter;
import com.ksfintech.grpc.server.convert.ProtoDefConverter;
import com.ksfintech.grpc.server.manager.DefaultGrpcServiceManager;
import com.ksfintech.grpc.server.manager.GrpcServiceInvoker;
import com.ksfintech.grpc.server.manager.GrpcServiceManager;
import com.ksfintech.grpc.server.processor.GrpcServiceProcessor;
import com.ksfintech.grpc.server.processor.ScannerHttpProcessor;
import com.ksfintech.grpc.server.scan.ProtoFileScanner;
import com.ksfintech.grpc.server.scan.ProtoFileScannerRegistrar;
import io.grpc.ServerBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.services.HealthStatusManager;
import org.lognet.springboot.grpc.autoconfigure.GRpcServerProperties;
import org.lognet.springboot.grpc.context.LocalRunningGrpcPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @date: 2020/8/4 14:41
 * @author: farui.yu
 */
@Configuration
@ConditionalOnBean(annotation = ProtoFileScanner.class)
@EnableConfigurationProperties({GRpcServerProperties.class, GrpcProtoProperties.class})
@Import({HttpConfig.class, SpringContext.class})
public class GrpcServerAutoConfiguration {

    @LocalRunningGrpcPort
    private int port;

    @Bean
    @ConditionalOnProperty(value = "grpc.method.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public GrpcServiceManager grpcServiceManager() {
        return new DefaultGrpcServiceManager(ServerBuilder.forPort(port));
    }

    @Bean
    @ConditionalOnExpression("#{environment.getProperty('grpc.inProcessServerName','')!=''}")
    public GrpcServiceManager grpcInProcessServiceManager(GRpcServerProperties grpcServerProperties) {
        return new DefaultGrpcServiceManager(InProcessServerBuilder.forName(grpcServerProperties.getInProcessServerName()));
    }

    @Bean
    public HealthStatusManager healthStatusManager() {
        return new HealthStatusManager();
    }

    @Bean
    public GrpcServiceInvoker serviceInvoker() {
        return new GrpcServiceInvoker();
    }

    @Bean
    public GrpcProtoProperties grpcProtoProperties() {
        return new GrpcProtoProperties();
    }

}
