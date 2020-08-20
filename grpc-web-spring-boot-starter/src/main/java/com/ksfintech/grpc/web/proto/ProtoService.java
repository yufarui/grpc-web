package com.ksfintech.grpc.web.proto;

import com.ksfintech.grpc.web.proto.stub.ProtoStub;
import lombok.Data;

import java.util.Map;

/**
 * @date: 2020/8/19 13:58
 * @author: farui.yu
 */
@Data
public class ProtoService {

    private Class<?> protoClass;
    private String protoClassName;
    private String serviceName;
    private Map<ProtoEnum, ProtoStub> protoStubMap;

    public ProtoStub getProtoStubMap(ProtoEnum protoEnum) {
        return protoStubMap.get(protoEnum);
    }
}
