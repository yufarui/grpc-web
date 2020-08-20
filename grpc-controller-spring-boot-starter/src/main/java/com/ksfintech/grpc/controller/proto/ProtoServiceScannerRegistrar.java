package com.ksfintech.grpc.controller.proto;

import com.ksfintech.grpc.controller.exception.GrpcException;
import com.ksfintech.grpc.controller.util.ProtoServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date: 2020/8/7 13:31
 * @author: farui.yu
 */
@Slf4j
public class ProtoServiceScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    /**
     * key => protoClazzSimpleName
     */
    private Map<String, ProtoService> protoServiceMap = new ConcurrentHashMap<>();

    @Autowired
    private AbstractApplicationContext applicationContext;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(ProtoServiceScanner.class.getName()));
        String[] location = annotationAttributes.getStringArray("value");

        searchGrpcService(location);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProtoServiceContainer.class);

        builder.addPropertyValue("protoServiceMap", protoServiceMap);

        registry.registerBeanDefinition("protoServiceContainer", builder.getBeanDefinition());
        protoServiceMap = null;
    }

    private void searchGrpcService(String... locations) {

        for (int i = 0; i < locations.length; i++) {

            try {
                String packageName = locations[i];
                String packageFile = packageName.replace('.', '/');
                Resource resource = resourceLoader.getResource(packageFile);
                if (!resource.exists()) {
                    log.error("查找文件位置{}出错", locations[i]);
                }
                Map<String, ProtoService> protoServiceMap = ProtoServiceUtil
                        .findClasses(packageName, resource.getFile());

                handler(packageName, protoServiceMap);

            } catch (Exception e) {
                log.error("查找文件位置{}出错", locations[i], e);
                continue;
            }
        }
    }

    private void handler(String packageName, Map<String, ProtoService> packageProtoService) {

        if (CollectionUtils.isEmpty(packageProtoService)) {
            log.info("当前目录{},不存在服务", packageName);
            return;
        }

        log.info("当前目录{},服务列表[{}]", packageName, String.join(",", packageProtoService.keySet()));

        packageProtoService.forEach((key, value) -> {
            if (protoServiceMap.get(key) != null) {
                throw new GrpcException("不允许存在重复Grpc服务名称[{}]", key);
            }
            protoServiceMap.put(key, value);
        });
    }
}
