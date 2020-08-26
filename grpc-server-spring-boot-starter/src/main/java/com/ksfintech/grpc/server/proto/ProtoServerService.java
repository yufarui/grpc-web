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

        String upper = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        ProtoServerMethod protoServerMethod = methodMap.get(upper);

        if (protoServerMethod == null) {
            String lower = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
            return methodMap.get(lower);
        }
        return protoServerMethod;
    }
}
