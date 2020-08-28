package com.ksfintech.grpc.server.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ksfintech.grpc.server.proto.ProtoFile;
import com.ksfintech.grpc.server.proto.ProtoServerMethod;
import com.ksfintech.grpc.server.proto.ProtoServerService;
import io.protostuff.compiler.ParserModule;
import io.protostuff.compiler.model.*;
import io.protostuff.compiler.parser.ClasspathFileReader;
import io.protostuff.compiler.parser.Importer;
import io.protostuff.compiler.parser.ProtoContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ProtoFileConvertUtil {

    private final Importer importer;

    public static ProtoFileConvertUtil getProtoFileConvertUtil() {
        return protoFileConvertUtil;
    }

    private static final ProtoFileConvertUtil protoFileConvertUtil = new ProtoFileConvertUtil();

    private ProtoFileConvertUtil() {
        Injector injector = Guice.createInjector(new ParserModule());
        this.importer = injector.getInstance(Importer.class);
    }

    public static ProtoFile convert(String fileName) {

        ProtoContext context = protoFileConvertUtil.importer
                .importFile(new ClasspathFileReader(), fileName);

        Proto proto = context.getProto();

        ProtoFile protoFile = createInitProto(proto);

        serviceHandler(proto.getServices(), protoFile);

        return protoFile;
    }

    private static ProtoFile createInitProto(Proto proto) {
        ProtoFile protoFile = new ProtoFile();
        protoFile.setFileName(proto.getFilename());
        protoFile.setProtoName(proto.getName());
        protoFile.setProtoPackage(proto.getPackage().toString());

        DynamicMessage options = proto.getOptions();

        protoFile.setJavaPackage(javaPackageHandler(options));
        protoFile.setHttpAddress(httpAddressHandler(options));
        protoFile.setServerServices(new ArrayList<>());
        return protoFile;
    }

    private static String javaPackageHandler(DynamicMessage options) {
        String javaPackage;
        try {
            javaPackage = options.get("java_package").getString();
        } catch (Exception e) {
            return null;
        }
        return javaPackage;
    }

    private static String httpAddressHandler(DynamicMessage options) {

        String httpAddress = null;
        try {
            httpAddress = options.get("(proto.option.http_address)").getString();
        } catch (Exception e) {
        }

        return httpAddress;
    }

    private static void serviceHandler(List<Service> serviceList, ProtoFile protoFile) {

        List<ProtoServerService> protoServerServices = new ArrayList<>();

        if (CollectionUtils.isEmpty(serviceList)) {
            return;
        }

        for (int i = 0; i < serviceList.size(); i++) {

            Service service = serviceList.get(i);

            ProtoServerService protoServerService = new ProtoServerService();
            protoServerService.setHttpAddress(protoFile.getHttpAddress());
            protoServerService.setProtoPackage(protoFile.getProtoPackage());
            protoServerService.setServiceName(service.getName());

            List<ServiceMethod> methods = service.getMethods();

            for (int j = 0; j < methods.size(); j++) {
                Map<String, ProtoServerMethod> protoServerMethodMap = new ConcurrentHashMap<>();
                ProtoServerMethod protoServerMethod = new ProtoServerMethod();
                ServiceMethod serviceMethod = methods.get(j);

                String urlPath = urlPathHandler(serviceMethod);
                protoServerMethod.setUrlPath(urlPath);
                protoServerMethod.setMethodName(serviceMethod.getName());
                protoServerMethod.setArgStream(serviceMethod.isArgStream());
                protoServerMethod.setReturnStream(serviceMethod.isReturnStream());
                protoServerMethod.setJavaArgType(convertJavaType(serviceMethod.getArgType()));
                protoServerMethod.setJavaReturnType(convertJavaType(serviceMethod.getReturnType()));

                protoServerMethodMap.put(serviceMethod.getName(), protoServerMethod);
                protoServerService.setMethodMap(protoServerMethodMap);
            }

            protoServerServices.add(protoServerService);
        }
        protoFile.setServerServices(protoServerServices);
    }

    private static String convertJavaType(Message message) {

        DynamicMessage options = message.getProto().getOptions();
        String javaPackage = javaPackageHandler(options);
        return javaPackage + "." + message.getName();
    }

    private static String urlPathHandler(ServiceMethod serviceMethod) {

        DynamicMessage options = serviceMethod.getOptions();

        String urlPath = null;
        try {
            urlPath = options.get("(proto.option.url_path)").getString();
        } catch (Exception e) {
        }
        if (StringUtils.isEmpty(urlPath)) {
            urlPath = serviceMethod.getName();
            urlPath = urlPath.substring(0, 1).toLowerCase() + urlPath.substring(1);
        }

        return urlSlashHandler(urlPath);
    }

    private static String urlSlashHandler(String path) {
        String location = path;
        if (!location.startsWith("/")) {
            location = "/" + location;
        }
        return location;
    }
}
