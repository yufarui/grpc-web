package com.ksfintech.grpc.server.scan;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ProtoFileScannerRegistrar.class)
public @interface ProtoFileScanner {

    @AliasFor("location")
    String[] value() default {""};

    @AliasFor("value")
    String[] location() default {""};

    String[] excluded() default {};

}
