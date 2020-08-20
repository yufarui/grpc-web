package com.ksfintech.grpc.web.autoconfig;


import com.ksfintech.grpc.web.grpcmapping.GrpcMappingHandlerMapping;
import com.ksfintech.grpc.web.manager.DefaultProtoManager;
import com.ksfintech.grpc.web.manager.ProtoServiceManager;
import com.ksfintech.grpc.web.mapper.MapperBeanPostProcessor;
import com.ksfintech.grpc.web.proto.ProtoServiceScanner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.handler.ConversionServiceExposingInterceptor;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;

/**
 * @date: 2020/8/14 9:56
 * @author: farui.yu
 */
@Configuration
@ConditionalOnBean(annotation = ProtoServiceScanner.class)
@Import({GrpcControllerRegistrar.class})
public class GrpcWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GrpcMappingHandlerMapping grpcMappingHandlerMapping(
            @Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
            @Qualifier("mvcConversionService") FormattingConversionService conversionService,
            @Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {

        return grpcMappingHandler(contentNegotiationManager, conversionService,
                resourceUrlProvider);
    }

    public GrpcMappingHandlerMapping grpcMappingHandler(
            @Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
            @Qualifier("mvcConversionService") FormattingConversionService conversionService,
            @Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {

        GrpcMappingHandlerMapping mapping = new GrpcMappingHandlerMapping();
        mapping.setOrder(0);
        mapping.setInterceptors(getInterceptors(conversionService, resourceUrlProvider));
        mapping.setContentNegotiationManager(contentNegotiationManager);

        return mapping;
    }

    protected final Object[] getInterceptors(
            FormattingConversionService mvcConversionService,
            ResourceUrlProvider mvcResourceUrlProvider) {

        Object[] Interceptors = {
                new ConversionServiceExposingInterceptor(mvcConversionService),
                new ResourceUrlProviderExposingInterceptor(mvcResourceUrlProvider)
        };

        return Interceptors;
    }

    @Bean
    public GrpcControllerFactoryBean<?> grpcControllerFactoryBean() {
        return new GrpcControllerFactoryBean<>();
    }

    @Bean
    @ConditionalOnProperty(value = "grpc.check.enable", matchIfMissing = true, havingValue = "true")
    public CheckGrpcControllerBean checkGrpcControllerBean() {
        return new CheckGrpcControllerBean();
    }

    @Bean
    public MapperBeanPostProcessor mapperBeanPostProcessor() {
        return new MapperBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtoServiceManager protoServiceManager() {
        return new DefaultProtoManager();
    }
}
