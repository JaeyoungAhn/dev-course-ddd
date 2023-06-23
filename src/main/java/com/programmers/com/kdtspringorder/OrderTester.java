package com.programmers.com.kdtspringorder;

import com.programmers.com.kdtspringorder.AppConfiguration;
import com.programmers.com.kdtspringorder.order.OrderItem;
import com.programmers.com.kdtspringorder.order.OrderProperties;
import com.programmers.com.kdtspringorder.order.OrderService;
import com.programmers.com.kdtspringorder.voucher.FixedAmountVoucher;
import com.programmers.com.kdtspringorder.voucher.JdbcVoucherRepository;
import com.programmers.com.kdtspringorder.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderTester {
    //com.programmers.com.kdtspringorder.OrderTester // SET WARN
    //com.programmers.com.kdtspringorder.A => WARN
    //com.programmers.com.kdtspringorder.voucher // SET INFO
    private static final Logger logger = LoggerFactory.getLogger(OrderTester.class);
    public static void main(String[] args) throws IOException {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        var applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AppConfiguration.class);

        var environment = applicationContext.getEnvironment();
        environment.setActiveProfiles("local");
        applicationContext.refresh();


        var version = environment.getProperty("kdt.version");
        var minimumOrderAmount = environment.getProperty("kdt.minimum-order-amount", Integer.class);
        var supportVendors = environment.getProperty("kdt.support-vendors", List.class);
        var description = environment.getProperty("kdt.description", List.class);

        logger.error("logger name => {}", logger.getName());
        logger.debug("[getEnv] version -> {}", version);
        logger.info("[getEnv] minimumOrderAmount -> {}", minimumOrderAmount);
        logger.info("[getEnv] supportVendors -> {}", supportVendors);
        logger.warn("[getEnv] description -> {}", description);


        var orderProperties = applicationContext.getBean(OrderProperties.class);
        System.out.println(MessageFormat.format("[OPPTs] version -> {0}", orderProperties.getVersion()));
        System.out.println(MessageFormat.format("[OPPTs] minimumOrderAmount -> {0}", orderProperties.getMinimumOrderAmount()));
        System.out.println(MessageFormat.format("[OPPTs] supportVendors -> {0}", orderProperties.getSupportVendors()));
        System.out.println(MessageFormat.format("[OPPTs] description -> {0}", orderProperties.getCurrentLocation()));


        var resource = applicationContext.getResource("classpath:application.yaml");
        var resource2 = applicationContext.getResource("file:HELP.md");
        var resource3 = applicationContext.getResource("https://google.com/");
        System.out.println(MessageFormat.format("Resource -> {0}", resource3.getClass().getCanonicalName()));
//        var file = resource3.getFile();

        var readableByteChannel = Channels.newChannel(resource3.getURL().openStream());
        var bufferedReader = new BufferedReader(Channels.newReader(readableByteChannel, "utf-8"));
        var contents = bufferedReader.lines().collect(Collectors.joining("\n"));
        System.out.println(contents);

//        var strings = Files.readAllLines(file.toPath());
//        System.out.println(strings.stream().reduce("", (a, b) -> a + "\n" + b));

        var customerId = UUID.randomUUID();

//        var voucherRepository = BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(), VoucherRepository.class, "memory");
//        var voucherRepository2 = BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(), VoucherRepository.class, "memory");
//
//        System.out.println(MessageFormat.format("voucherRepository {0}", voucherRepository));
//        System.out.println(MessageFormat.format("voucherRepository2 {0}", voucherRepository2));
//        System.out.println(MessageFormat.format("voucherRepository == voucherRepository2 => {0}", voucherRepository == voucherRepository2));





        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JdbcVoucherRepository));
        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));

        var orderService = applicationContext.getBean(OrderService.class);
        var order = orderService.createOrder(customerId, new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }}, voucher.getVoucherId());
        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90L", order.totalAmount()));

        applicationContext.close();
    }
}