package com.ksfintech.grpc.server.proto;

import lombok.Data;

import java.util.List;

/**
 * @date: 2020/8/10 9:26
 * @author: farui.yu
 */
@Data
public class ProtoContainer {

    private List<ProtoFile> protoFiles;

}
