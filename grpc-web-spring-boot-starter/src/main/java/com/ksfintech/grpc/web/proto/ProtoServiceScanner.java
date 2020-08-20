package com.ksfintech.grpc.web.proto;

import com.ksfintech.grpc.web.proto.ProtoServiceScannerRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @date: 2020/8/14 11:22
 * @author: farui.yu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ProtoServiceScannerRegistrar.class)
public @interface ProtoServiceScanner {

    String[] value() default {};

}
