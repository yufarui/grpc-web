package com.ksfintech.grpc.server.scan;

import com.alibaba.fastjson.JSON;
import com.ksfintech.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import com.ksfintech.grpc.server.proto.ProtoFile;
import com.ksfintech.grpc.server.proto.ProtoContainer;
import com.ksfintech.grpc.server.util.ProtoFileConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @date: 2020/8/7 13:31
 * @author: farui.yu
 */
@Slf4j
public class ProtoFileScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Autowired
    private AbstractApplicationContext applicationContext;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(ProtoFileScanner.class.getName()));
        String[] location = annotationAttributes.getStringArray("location");

        List<String> protoUris = searchProtoFileWithJar(location);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProtoContainer.class);

        List<ProtoFile> protoFiles = new ArrayList<>();
        for (String fileName : protoUris) {
            ProtoFile protoFile = ProtoFileConvertUtil.convert(fileName);
            protoFiles.add(protoFile);
        }

        builder.addPropertyValue("protoFiles", protoFiles);

        registry.registerBeanDefinition("protoContainer", builder.getBeanDefinition());
    }

    private List<String> searchProtoFile(String... locations) {

        List<String> protoUris = new ArrayList<>();
        for (int i = 0; i < locations.length; i++) {
            File file;
            try {
                file = resourceLoader.getResource(locations[i]).getFile();
            } catch (Exception e) {
                log.error("查找文件位置{}出错", locations, e);
                continue;
            }
            String[] uris = file.list();

            for (String uri : uris) {
                if (StringUtils.endsWith(uri, ".proto")) {
                    protoUris.add(uri);
                }
            }
        }

        return protoUris;
    }

    private List<String> searchProtoFileWithJar(String... locations) {

        List<String> protoUris = new ArrayList<>();
        for (int i = 0; i < locations.length; i++) {
            File file = null;
            try {

                List<URL> resources = ClassLoaderUtils.getResources(
                        urlSlashHandler(locations[i]), getClass());
                for (int j = 0; j < resources.size(); j++) {
                    file = new File(resources.get(j).getPath());
                }

                String[] uris = file.list();

                for (String uri : uris) {
                    if (StringUtils.endsWith(uri, ".proto")) {
                        protoUris.add(uri);
                    }
                }
            } catch (Exception e) {
                log.error("查找文件位置{}出错", locations, e);
                continue;
            }

        }

        return protoUris;
    }

    private String urlSlashHandler(String path) {
        String location = path;
        if (!location.startsWith("/")) {
            location = "/" + location;
        }
        return location;
    }
}
