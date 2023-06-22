package com.programmers.com.kdtspringorder;

import com.programmers.com.kdtspringorder.AppConfiguration;
import com.programmers.com.kdtspringorder.order.OrderItem;
import com.programmers.com.kdtspringorder.order.OrderProperties;
import com.programmers.com.kdtspringorder.order.OrderService;
import com.programmers.com.kdtspringorder.voucher.FixedAmountVoucher;
import com.programmers.com.kdtspringorder.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderTester {
    public static void main(String[] args) {
        var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

        var environment = applicationContext.getEnvironment();
        var version = environment.getProperty("kdt.version");
        var minimumOrderAmount = environment.getProperty("kdt.minimum-order-amount", Integer.class);
        var supportVendors = environment.getProperty("kdt.support-vendors", List.class);
        var description = environment.getProperty("kdt.description", List.class);

        System.out.println(MessageFormat.format("[getEnv] version -> {0}", version));
        System.out.println(MessageFormat.format("[getEnv] minimumOrderAmount -> {0}", minimumOrderAmount));
        System.out.println(MessageFormat.format("[getEnv] supportVendors -> {0}", supportVendors));
        System.out.println(MessageFormat.format("[getEnv] description -> {0}", description));


        var orderProperties = applicationContext.getBean(OrderProperties.class);
        System.out.println(MessageFormat.format("[OPPTs] version -> {0}", orderProperties.getVersion()));
        System.out.println(MessageFormat.format("[OPPTs] minimumOrderAmount -> {0}", orderProperties.getMinimumOrderAmount()));
        System.out.println(MessageFormat.format("[OPPTs] supportVendors -> {0}", orderProperties.getSupportVendors()));
        System.out.println(MessageFormat.format("[OPPTs] description -> {0}", orderProperties.getCurrentLocation()));


        var customerId = UUID.randomUUID();

        var voucherRepository = BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(), VoucherRepository.class, "memory");
        var voucherRepository2 = BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(), VoucherRepository.class, "memory");
        System.out.println(MessageFormat.format("voucherRepository {0}", voucherRepository));
        System.out.println(MessageFormat.format("voucherRepository2 {0}", voucherRepository2));
        System.out.println(MessageFormat.format("voucherRepository == voucherRepository2 => {0}", voucherRepository == voucherRepository2));


//        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));
        var orderService = applicationContext.getBean(OrderService.class);
        var order = orderService.createOrder(customerId, new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }}, voucher.getVoucherId());
        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90L", order.totalAmount()));

        applicationContext.close();
    }
}