package com.ksfintech.server.controller.demo;


import com.ksfintech.server.controller.api.HelloReplyMo;
import com.ksfintech.server.controller.api.HelloRequestMo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @date: 2020/7/31 8:44
 * @author: farui.yu
 */
@RestController
@Slf4j
@CrossOrigin
public class GrpcServerController {

    @PostMapping("/test")
    public HelloReplyMo test(@RequestBody HelloRequestMo helloRequest) {
        log.info("hi [{}]" , helloRequest);
        HelloReplyMo helloReplyMo = new HelloReplyMo();
        helloReplyMo.setMessage(helloRequest.getName() + "response");
        return helloReplyMo;
    }

    @PostMapping("/test1")
    public HelloReplyMo test1(@RequestBody HelloRequestMo helloRequest) {
        log.info("hi [{}]" , helloRequest);
        HelloReplyMo helloReplyMo = new HelloReplyMo();
        helloReplyMo.setMessage(helloRequest.getName() + "come bro");
        return helloReplyMo;
    }


    @PostMapping("/test2")
    public HelloReplyMo test2() {
        HelloReplyMo helloReplyMo = new HelloReplyMo();
        helloReplyMo.setMessage("tet2: " + "response");
        return helloReplyMo;
    }

    @PostMapping("/test3")
    public void test3() {
        log.info("空空如也");
    }

}
