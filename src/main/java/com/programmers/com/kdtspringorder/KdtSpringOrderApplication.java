package com.programmers.com.kdtspringorder;

import com.programmers.com.kdtspringorder.order.OrderProperties;
import com.programmers.com.kdtspringorder.voucher.FixedAmountVoucher;
import com.programmers.com.kdtspringorder.voucher.JdbcVoucherRepository;
import com.programmers.com.kdtspringorder.voucher.VoucherRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.text.MessageFormat;
import java.util.UUID;

@SpringBootApplication
@ComponentScan(basePackages = {"com.programmers.com.kdtspringorder.order", "com.programmers.com.kdtspringorder.voucher", "com.programmers.com.kdtspringorder.configuration"})

public class KdtSpringOrderApplication {

    public static void main(String[] args) {

        var springApplication = new SpringApplication(KdtSpringOrderApplication.class);
        springApplication.setAdditionalProfiles("local");

//        var applicationContext = SpringApplication.run(KdtSpringOrderApplication.class, args);
        var applicationContext = springApplication.run(args);

        var orderProperties = applicationContext.getBean(OrderProperties.class);

        System.out.println(MessageFormat.format("[OPPTs] version -> {0}", orderProperties.getVersion()));
        System.out.println(MessageFormat.format("[OPPTs] minimumOrderAmount -> {0}", orderProperties.getMinimumOrderAmount()));
        System.out.println(MessageFormat.format("[OPPTs] supportVendors -> {0}", orderProperties.getSupportVendors()));
        System.out.println(MessageFormat.format("[OPPTs] description -> {0}", orderProperties.getCurrentLocation()));

        var customerId = UUID.randomUUID();

        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JdbcVoucherRepository));
        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));
    }

}
