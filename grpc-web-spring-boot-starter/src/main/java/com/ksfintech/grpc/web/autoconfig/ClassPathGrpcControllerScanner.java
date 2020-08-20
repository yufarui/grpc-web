package com.ksfintech.grpc.web.autoconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 将GrpcController对应类交由GrpcControllerFactoryBean控制
 *
 * @date: 2020/8/4 15:31
 * @author: farui.yu
 */
public class ClassPathGrpcControllerScanner extends ClassPathBeanDefinitionScanner {

    private static List<Class> classList = new ArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathGrpcControllerScanner.class);

    public ClassPathGrpcControllerScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {

        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            LOGGER.warn("No DistroBatch was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();

            String beanClassName = definition.getBeanClassName();

            Class<?> beanClass = null;
            try {
                beanClass = ClassUtils.forName(beanClassName, getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
            }

            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClass);
            definition.setBeanClass(GrpcControllerFactoryBean.class);

            classList.add(beanClass);
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    public static List<Class> getClassList() {
        return classList;
    }
}

