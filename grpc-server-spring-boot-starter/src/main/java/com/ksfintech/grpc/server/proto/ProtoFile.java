package com.ksfintech.grpc.server.proto;

import lombok.Data;

import java.util.List;

/**
 * @date: 2020/8/7 13:29
 * @author: farui.yu
 */
@Data
public class ProtoFile {

    private String fileName;
    private String protoName;
    private String protoPackage;
    private String javaPackage;
    private String httpAddress;
    private List<ProtoServerService> serverServices;

}
