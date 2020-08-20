package com.orientsec.grpc.controlleradvice;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date: 2020/8/14 17:25
 * @author: farui.yu
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * 全局异常捕捉处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Map errorHandler(MethodArgumentNotValidException ex) {
        Map map = new HashMap();
        map.put("code", 500);

        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();

        List<String> errorList = new ArrayList<>();
        allErrors.forEach(o -> {
            FieldError error = (FieldError) o;
            errorList.add(error.getField() + ":" + error.getDefaultMessage());
        });

        map.put("msg", String.join(",", errorList));
        return map;
    }
}
