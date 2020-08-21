package com.ksfintech.grpc.server.manager;

import com.ksfintech.grpc.server.processor.GrpcServiceProcessor;
import com.orientsec.grpc.common.constant.GlobalConstants;
import com.orientsec.grpc.common.constant.RegistryConstants;
import com.orientsec.grpc.common.model.BusinessResult;
import com.orientsec.grpc.common.util.ExceptionUtils;
import com.orientsec.grpc.common.util.GrpcUtils;
import com.orientsec.grpc.common.util.IpUtils;
import com.orientsec.grpc.common.util.MapUtils;
import com.orientsec.grpc.provider.Registry;
import com.orientsec.grpc.registry.common.URL;
import com.orientsec.grpc.registry.service.Provider;
import io.grpc.*;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.services.HealthStatusManager;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.context.GRpcServerInitializedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @date: 2020/8/10 18:32
 * @author: farui.yu
 */
@Slf4j
public class DefaultGrpcServiceManager implements GrpcServiceManager {

    @Autowired
    private HealthStatusManager healthStatusManager;

    @Autowired
    private AbstractApplicationContext applicationContext;

    @Autowired(required = false)
    private List<GrpcServiceProcessor> grpcServiceProcessorList;

    private ServerBuilder<?> serverBuilder;

    private Server server;

    private List<ServerServiceDefinition> grpcServiceCache = new ArrayList<>();

    public DefaultGrpcServiceManager(ServerBuilder<?> serverBuilder) {
        this.serverBuilder = serverBuilder;
    }

    @PostConstruct
    public void init() {
        serverBuilder.addService(healthStatusManager.getHealthService());
        try {
            server = serverBuilder.build().start();
        } catch (Exception e) {
        }

        applicationContext.publishEvent(new GRpcServerInitializedEvent(server));

        if (CollectionUtils.isEmpty(grpcServiceProcessorList)) {
            log.warn("没有可加载的server定义");
            return;
        }

        for (int i = 0; i < grpcServiceProcessorList.size(); i++) {
            List<ServerServiceDefinition> grpcServices = grpcServiceProcessorList.get(i)
                    .findGrpcServices();
            if (!CollectionUtils.isEmpty(grpcServices)) {
                grpcServiceCache.addAll(grpcServices);
            }
        }
    }

    @Override
    public void start() {

        for (int i = 0; i < grpcServiceCache.size(); i++) {
            ServerServiceDefinition serviceDefinition = grpcServiceCache.get(i);

            BusinessResult result = add(serviceDefinition);
            if (result.isSuccess()) {
                String serviceName = serviceDefinition.getServiceDescriptor().getName();
                healthStatusManager.setStatus(serviceName, HealthCheckResponse.ServingStatus.SERVING);
            }
        }

        startDaemonAwaitThread();
    }

    @Override
    public void stop() {
        log.info("Shutting down gRPC server ...");
        server.getServices().forEach(def -> healthStatusManager.clearStatus(def.getServiceDescriptor().getName()));
        Optional.ofNullable(server).ifPresent(Server::shutdown);
        log.info("gRPC server stopped.");
    }

    @Override
    public BusinessResult update(ServerServiceDefinition serverServiceDefinitions) {
        return Registry.updateService(server, serverServiceDefinitions);
    }

    @Override
    public BusinessResult add(ServerServiceDefinition serverServiceDefinitions) {
        return Registry.registerNewService(server, serverServiceDefinitions);
    }

    @Override
    public BusinessResult delete(ServerServiceDefinition serviceDefinition) {

        try {
            int port = server.getPort();
            String serviceName = serviceDefinition.getServiceDescriptor().getName();
            String ip = IpUtils.getIP4WithPriority();

            Map<String, String> parameters = new HashMap<>(MapUtils.capacity(2));
            parameters.put(GlobalConstants.Consumer.Key.INTERFACE, serviceName);
            parameters.put(GlobalConstants.CommonKey.CATEGORY, RegistryConstants.PROVIDERS_CATEGORY);
            URL queryUrl = new URL(RegistryConstants.GRPC_PROTOCOL, ip, port, parameters);

            Provider provider = new Provider();
            List<URL> urls = provider.lookup(queryUrl);

            if (urls == null || urls.isEmpty()) {
                return new BusinessResult(false, "在注册中心上没有查找到服务名为["
                        + serviceName + "]、ip为[" + ip + "]、端口为["
                        + port + "]的服务注册信息。");
            } else if (urls.size() != 1) {
                return new BusinessResult(false, "在注册中心上服务名为["
                        + serviceName + "]、ip为[" + ip + "]、端口为["
                        + port + "]的服务注册信息存在[" + urls.size() + "]条。");
            }

            URL providerUrl = urls.get(0);

            // 获取当前服务端的服务信息
            List<ServerServiceDefinition> oldServices = server.getServices();
            List<ServerServiceDefinition> newServices = new ArrayList<>(oldServices.size());

            String name;
            for (ServerServiceDefinition item : oldServices) {
                name = item.getServiceDescriptor().getName();
                if (!serviceName.equals(name)) {
                    newServices.add(item);
                }
            }

            // 向服务端对象写入服务信息
            HandlerRegistry registry = server.getRegistry();
            if (registry == null) {
                return new BusinessResult(false, "从服务端对象中无法获取到注册处理器。");
            }
            registry.resetServicesAndMethods(newServices);

            provider.unRegisterService(providerUrl);

            return new BusinessResult(true, "OK");
        } catch (Exception e) {
            log.error("删除已注册服务所提供的的方法出错", e);
            String message = "删除已注册服务所提供的的方法出错，出错信息堆栈信息为:" + ExceptionUtils.getExceptionStackMsg(e);
            return new BusinessResult(false, message);
        }
    }

    private void startDaemonAwaitThread() {
        Thread awaitThread = new Thread(() -> {
            try {
                this.server.awaitTermination();
            } catch (InterruptedException e) {
                log.error("gRPC server stopped.", e);
            }
        });
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    private static String getMethods(ServerServiceDefinition item) {
        StringBuilder sb = new StringBuilder();
        Collection<MethodDescriptor<?, ?>> methodDesps;
        String methodName;

        methodDesps = item.getServiceDescriptor().getMethods();

        for (MethodDescriptor<?, ?> md : methodDesps) {
            methodName = GrpcUtils.getSimpleMethodName(md.getFullMethodName());
            sb.append(methodName);
            sb.append(",");// 多个方法之间用英文逗号分隔
        }

        sb.deleteCharAt(sb.lastIndexOf(","));

        String methods = sb.toString();
        return methods;
    }
}
