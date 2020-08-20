package com.ksfintech.grpc.controller.proto;

import lombok.Data;

import java.util.Map;

/**
 * @date: 2020/8/19 16:25
 * @author: farui.yu
 */
@Data
public class ProtoServiceContainer {

    /**
     * key => protoClazzSimpleName;
     */
    private Map<String, ProtoService> protoServiceMap;

    public ProtoService getProtoServiceMap(String protoClazzSimpleName) {
        return protoServiceMap.get(protoClazzSimpleName);
    }
}
