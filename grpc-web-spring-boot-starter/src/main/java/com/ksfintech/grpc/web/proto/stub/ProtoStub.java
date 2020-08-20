package com.ksfintech.grpc.web.proto.stub;

import com.ksfintech.grpc.web.proto.ProtoEnum;
import com.ksfintech.grpc.web.proto.ProtoService;
import com.ksfintech.grpc.web.proto.method.ProtoMethod;
import io.grpc.stub.AbstractStub;
import lombok.Data;

import java.util.Map;

/**
 * @date: 2020/8/19 14:02
 * @author: farui.yu
 */
@Data
public class ProtoStub {

    private ProtoService parent;
    private ProtoEnum protoEnum;
    private String serviceName;
    private AbstractStub<?> abstractStub;

    private Map<String, ProtoMethod> stubMethodMap;

    public ProtoStub() {
    }

    public ProtoStub(ProtoEnum protoEnum) {
        this.protoEnum = protoEnum;
    }

    public ProtoMethod getStubMethodMap(String methodName) {
        return stubMethodMap.get(methodName);
    }
}
