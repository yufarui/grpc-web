package com.ksfintech.grpc.web.autoconfig;

import com.ksfintech.grpc.web.grpcmapping.GrpcController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @date: 2020/8/4 15:30
 * @author: farui.yu
 */
public class GrpcControllerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(GrpcControllerRegistrar.class);

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!AutoConfigurationPackages.has(this.beanFactory)) {
            logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
            return;
        }

        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
        if (logger.isDebugEnabled()) {
            packages.forEach(pkg -> logger.debug("Using auto-configuration base package '{}'", pkg));
        }

        ClassPathGrpcControllerScanner scanner = new ClassPathGrpcControllerScanner(registry);
        scanner.addIncludeFilter(new AnnotationTypeFilter(GrpcController.class));
        scanner.doScan(StringUtils.toStringArray(packages));
    }
}
