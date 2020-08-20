package com.orientsec.grpc.mapper;

import com.ksfintech.proto.reply.HelloReply;
import com.ksfintech.proto.request.HelloRequest;
import com.ksfintech.proto.request.TestRequest;
import com.orientsec.grpc.demo.HelloReplyMo;
import com.orientsec.grpc.demo.HelloRequestMo;
import com.orientsec.grpc.demo.TestRequestMo;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @date: 2020/8/13 15:15
 * @author: farui.yu
 */
@Mapper(componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(source = "nameBar", target = "name")
    HelloRequest sayHello(HelloRequestMo helloRequestMo);

    HelloReplyMo sayHello(HelloReply helloReply);

    @Mapping(source = "hq", target = "helloRequestMo")
    TestRequestMo testRequestToProto(TestRequest testRequest);

    @Mapping(source = "helloRequestMo", target = "hq")
    TestRequest testProtoToRequest(TestRequestMo testRequestMo);
}
