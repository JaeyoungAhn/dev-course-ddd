package com.programmers.com.kdtspringorder.aop;

import com.programmers.com.kdtspringorder.order.OrderService;
import com.programmers.com.kdtspringorder.voucher.FixedAmountVoucher;
import com.programmers.com.kdtspringorder.voucher.VoucherRepository;
import com.programmers.com.kdtspringorder.voucher.VoucherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringJUnitConfig
@ActiveProfiles("test")
public class AopTests {

    @Configuration
    @ComponentScan(basePackages = {"com.programmers.com.kdtspringorder.voucher", "com.programmers.com.kdtspringorder.aop"})

    @EnableAspectJAutoProxy
    static class Config {
    }
    @Autowired
    ApplicationContext context;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherService voucherService;

    @Test
    @DisplayName("Aop test")
    public void testOrderService() {
        // Given
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
        voucherRepository.insert(fixedAmountVoucher);

//        voucherService.getVoucher(fixedAmountVoucher.getVoucherId());
    }
}