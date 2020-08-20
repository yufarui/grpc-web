package com.ksfintech.grpc.web.autoconfig;

import com.ksfintech.grpc.web.exception.GrpcException;
import com.ksfintech.grpc.web.grpcmapping.GrpcController;
import com.ksfintech.grpc.web.grpcmapping.GrpcMapping;
import com.ksfintech.grpc.web.mapper.MapperBeanPostProcessor;
import com.ksfintech.grpc.web.mapper.MapperMethod;
import com.ksfintech.grpc.web.proto.ProtoEnum;
import com.ksfintech.grpc.web.proto.ProtoService;
import com.ksfintech.grpc.web.proto.ProtoServiceContainer;
import com.ksfintech.grpc.web.proto.method.ProtoMethod;
import com.ksfintech.grpc.web.proto.stub.ProtoStub;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @date: 2020/8/17 16:26
 * @author: farui.yu
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 500)
public class CheckGrpcControllerBean implements CommandLineRunner {

    @Autowired
    private ProtoServiceContainer protoServiceContainer;

    public void check(Class grpcClass) {

        String className = grpcClass.getSimpleName();
        log.info("类{{}}开始检查", className);

        ProtoService protoService = checkGrpcService(grpcClass);

        Method[] declaredMethods = grpcClass.getDeclaredMethods();

        for (int i = 0; i < declaredMethods.length; i++) {
            Method method = declaredMethods[i];
            String methodName = method.getName();

            GrpcMapping grpcMapping = AnnotationUtils.findAnnotation(method, GrpcMapping.class);
            ProtoEnum protoEnum = grpcMapping.protoEnum();
            String protoMethodName = grpcMapping.protoMethodName();
            if (!StringUtils.isBlank(protoMethodName)) {
                methodName = protoMethodName;
            }

            ProtoStub protoStubMap = protoService.getProtoStubMap(protoEnum);

            ProtoMethod stubMethod = protoStubMap.getStubMethodMap(methodName);

            if (stubMethod == null) {
                throw new GrpcException("你在GrpcController[{}]定义了method[{}],但protobuf中不存在", className, methodName);
            }

            checkReqProtoMessage(method, stubMethod);
            checkReturnObject(method, stubMethod);
        }
        log.info("类{{}}检查结束", className);
    }

    private ProtoService checkGrpcService(Class grpcClass) {
        GrpcController grpcController = AnnotationUtils.getAnnotation(grpcClass, GrpcController.class);
        String grpcService = grpcController.grpcService();

        String standardId = grpcService.substring(0, 1).toUpperCase() + grpcService.substring(1);

        ProtoService protoService = protoServiceContainer.getProtoServiceMap(standardId);

        if (protoService == null) {
            throw new GrpcException("请检查你是否将grpcService[{}],放入到容器中", grpcService);
        }

        return protoService;
    }


    private void checkReqProtoMessage(Method method, ProtoMethod protoMethod) {

        String methodName = method.getName();

        Parameter[] parameters = method.getParameters();

        if (parameters.length > 1) {
            throw new GrpcException("methodName[{}],参数不合法", methodName);
        }

        if (parameters.length == 0) {
            try {
                Class protoArgType = protoMethod.getArgType();
                Method newBuilder = protoArgType.getMethod("newBuilder");
                newBuilder.invoke("build");
            } catch (Exception e) {
                throw new GrpcException("methodName[{}],入参无法初始化", methodName);
            }
        }

        if (parameters.length == 1) {
            String convertReqName = method.getParameters()[0].getType().getSimpleName();
            String convertRepName = protoMethod.getArgType().getSimpleName();
            String paramName = convertReqName.substring(0, 1).toLowerCase() + convertReqName.substring(1);

            String reqId = convertReqName + "_" + convertRepName;
            MapperMethod reqMapperMethod = MapperBeanPostProcessor.getMapperMethodMap().get(reqId);
            if (reqMapperMethod == null) {
                throw new GrpcException("请检查你是否定义了合适的转换类,其格式为{} {}({} {});", convertRepName,
                        methodName, convertReqName, paramName);
            }
        }
    }

    private void checkReturnObject(Method method, ProtoMethod protoMethod) {
        Class<?> returnType = method.getReturnType();

        if (returnType.equals(Void.class)) {
            return;
        }

        String methodName = method.getName();

        String convertReqName = protoMethod.getReturnType().getSimpleName();
        String convertRepName = returnType.getSimpleName();
        String paramName = convertReqName.substring(0, 1).toLowerCase() + convertReqName.substring(1);

        String returnId = convertReqName + "_" + convertRepName;

        MapperMethod returnMapperMethod = MapperBeanPostProcessor.getMapperMethodMap().get(returnId);

        if (returnMapperMethod == null) {
            throw new GrpcException("请检查你是否定义了合适的转换类,其格式为{} {}({} {});", convertRepName,
                    methodName, convertReqName, paramName);
        }

    }

    @Override
    public void run(String... args) throws Exception {

        log.info("====开始检查====");
        List<Class> classList = ClassPathGrpcControllerScanner.getClassList();
        for (Class grpcController : classList) {
            check(grpcController);
        }
        log.info("====检查结束====");
    }
}
