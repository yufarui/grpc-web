package com.ksfintech.grpc.server.proto;

import lombok.Data;

@Data
public class ProtoMessage {

    private String messageName;
    private String javaPackage;
    private String protoPackage;
    private String protoFullName;

    private String javaType;
}
