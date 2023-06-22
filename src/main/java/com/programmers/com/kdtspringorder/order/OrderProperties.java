package com.programmers.com.kdtspringorder.order;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Component
public class OrderProperties implements InitializingBean {

//    @Value("v1.1.1")
    @Value("${kdt.version2:v0.0.0}")
    private String version;

//    @Value("아이")
//    @Value("0")
    @Value("${kdt.minimum-order-amount}")
    private int minimumOrderAmount;

//    @Value("d, a, b")
    @Value("${kdt.support-vendors}")
    private List<String> supportVendors;

    @Value("${PWD}")
    private String currentLocation;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(MessageFormat.format("[Value] version -> {0}", version));
        System.out.println(MessageFormat.format("[Value] minimumOrderAmount -> {0}", minimumOrderAmount
        ));
        System.out.println(MessageFormat.format("[Value] supportVendors -> {0}", supportVendors));
        System.out.println(MessageFormat.format("[Value] currentLocation -> {0}", currentLocation));

    }
}
