package com.ksfintech.grpc.server.proto;

import lombok.Data;

/**
 * @date: 2020/8/7 10:44
 * @author: farui.yu
 */
@Data
public class ProtoServerMethod {

    private String urlPath;
    private String methodName;
    private boolean argStream;
    private boolean returnStream;
    private String javaArgType;
    private String javaReturnType;

}
