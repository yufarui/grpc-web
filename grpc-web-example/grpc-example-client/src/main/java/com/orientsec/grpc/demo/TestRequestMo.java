package com.orientsec.grpc.demo;

/**
 * @date: 2020/8/13 18:56
 * @author: farui.yu
 */
public class TestRequestMo {
    private String name;
    private HelloRequestMo helloRequestMo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HelloRequestMo getHelloRequestMo() {
        return helloRequestMo;
    }

    public void setHelloRequestMo(HelloRequestMo helloRequestMo) {
        this.helloRequestMo = helloRequestMo;
    }
}
