package com.ksfintech.grpc.controller.proto;

import com.ksfintech.grpc.controller.proto.method.ProtoBlockingMethod;
import com.ksfintech.grpc.controller.proto.method.ProtoFutureMethod;
import com.ksfintech.grpc.controller.proto.method.ProtoMethod;
import com.ksfintech.grpc.controller.proto.method.ProtoReactiveMethod;
import com.ksfintech.grpc.controller.proto.stub.ProtoStub;

/**
 * @date: 2020/8/19 14:03
 * @author: farui.yu
 */
public enum ProtoEnum {
    /**
     *
     */
    BLOCKING,
    FUTURE,
    REACTIVE,
    ;

    public static String chooseStubString(ProtoEnum protoEnum) {
        switch (protoEnum) {
            case BLOCKING:
                return "newBlockingStub";
            case FUTURE:
                return "newFutureStub";
            case REACTIVE:
                return "newStub";
            default:
                throw new RuntimeException();
        }
    }

    public static ProtoStub chooseStub(ProtoEnum protoEnum) {
        return new ProtoStub(protoEnum);
    }

    public static ProtoMethod chooseMethod(ProtoEnum protoEnum) {
        switch (protoEnum) {
            case BLOCKING:
                return new ProtoBlockingMethod();
            case FUTURE:
                return new ProtoFutureMethod();
            case REACTIVE:
                return new ProtoReactiveMethod();
            default:
                throw new RuntimeException();
        }
    }
}
