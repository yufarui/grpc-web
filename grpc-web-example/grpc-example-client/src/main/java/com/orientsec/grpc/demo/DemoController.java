package com.orientsec.grpc.demo;

import com.ksfintech.grpc.controller.grpcmapping.GrpcController;
import com.ksfintech.grpc.controller.grpcmapping.GrpcMapping;
import com.ksfintech.grpc.controller.proto.ProtoEnum;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @date: 2020/8/14 8:42
 * @author: farui.yu
 */
@CrossOrigin
@GrpcController(grpcService = "myService")
public interface DemoController {

    @GrpcMapping(value = "/test", method = RequestMethod.POST)
    HelloReplyMo sayHello(@Valid @RequestBody HelloRequestMo helloRequestMo);

    @GrpcMapping(value = "/test1", method = RequestMethod.POST,
            protoEnum = ProtoEnum.FUTURE, protoMethodName = "sayHello")
    HelloReplyMo sayHelloF(@Valid @RequestBody HelloRequestMo helloRequestMo);

    @GrpcMapping(value = "/test2", method = RequestMethod.POST,
            protoEnum = ProtoEnum.REACTIVE, protoMethodName = "sayHello")
    HelloReplyMo sayHelloR(@Valid @RequestBody HelloRequestMo helloRequestMo);

    @GrpcMapping(value = "/test3", method = RequestMethod.POST, protoMethodName = "sayHello")
    HelloReplyMo sayHello1();

    @GrpcMapping(value = "/test4", method = RequestMethod.POST, protoMethodName = "sayHello")
    void sayHello2(@Valid @RequestBody HelloRequestMo helloRequestMo);

    @GrpcMapping(value = "/test5", method = RequestMethod.POST, protoMethodName = "sayHello")
    void sayHello3();
}
