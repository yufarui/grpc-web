package com.ksfintech.grpc.web.exception;

import org.slf4j.helpers.MessageFormatter;

import java.util.Arrays;

/**
 * @date: 2020/8/17 14:04
 * @author: farui.yu
 */
public class GrpcException extends RuntimeException {

    private String message;
    private Throwable cause;

    public GrpcException(String message, Object... args) {
        if (args == null || args.length == 0) {
            this.message = message;
            return;
        }

        if (args[args.length - 1] instanceof Throwable) {
            this.message = MessageFormatter.arrayFormat(message, Arrays.copyOfRange(args, 0, args.length - 1)).getMessage();
            this.cause = (Throwable) args[args.length - 1];
        } else {
            this.message = MessageFormatter.arrayFormat(message, args).getMessage();
        }

    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public synchronized Throwable getCause() {
        return this.cause;
    }
}
