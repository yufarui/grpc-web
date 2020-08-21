package com.ksfintech.grpc.server.proto;

import lombok.Data;

import java.util.Map;

/**
 * @date: 2020/8/10 14:35
 * @author: farui.yu
 */
@Data
public class ProtoServerService {

    private String serviceName;
    private String httpAddress;
    private String protoPackage;
    private Map<String, ProtoServerMethod> methodMap;

    public ProtoServerMethod getMethodMap(String methodName) {
        ProtoServerMethod protoServerMethod = methodMap.get(methodName);
        if (protoServerMethod == null) {
            methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
            return methodMap.get(methodName);
        }
        return protoServerMethod;
    }
}
