package com.ksfintech.grpc.server.servercall;

import com.ksfintech.grpc.server.proto.ProtoServerService;
import io.grpc.stub.ServerCalls;

/**
 * @date: 2020/8/21 10:49
 * @author: farui.yu
 */
public interface ProtoServerCall extends ServerCalls.UnaryMethod, ServerCalls.ServerStreamingMethod,
        ServerCalls.ClientStreamingMethod, ServerCalls.BidiStreamingMethod {

}
