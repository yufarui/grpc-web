package com.ksfintech.grpc.controller.grpcmapping;

import com.ksfintech.grpc.controller.proto.ProtoEnum;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * @date: 2020/8/14 8:36
 * @author: farui.yu
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@RequestMapping
public @interface GrpcMapping {

    String name() default "";

    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};

    RequestMethod[] method() default {};

    String[] params() default {};

    String[] headers() default {};

    String[] consumes() default {};

    String[] produces() default {};

    ProtoEnum protoEnum() default ProtoEnum.BLOCKING;

    String protoMethodName() default "";
}
