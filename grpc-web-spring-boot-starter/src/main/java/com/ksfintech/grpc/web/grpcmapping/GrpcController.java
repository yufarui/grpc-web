package com.ksfintech.grpc.web.grpcmapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * @date: 2020/8/14 8:36
 * @author: farui.yu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Controller
@ResponseBody
public @interface GrpcController {

    String grpcService();

}
