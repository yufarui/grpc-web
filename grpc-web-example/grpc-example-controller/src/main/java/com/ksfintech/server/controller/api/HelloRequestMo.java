package com.ksfintech.server.controller.api;

import lombok.Data;

import java.util.List;

/**
 * @date: 2020/7/30 16:35
 * @author: farui.yu
 */
@Data
public class HelloRequestMo {
    private String name;
    private List<String> age;
    private String wantToFly;
}
