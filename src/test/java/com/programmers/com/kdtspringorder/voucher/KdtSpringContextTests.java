package com.programmers.com.kdtspringorder.voucher;

import com.programmers.com.kdtspringorder.AppConfiguration;
import com.programmers.com.kdtspringorder.order.OrderItem;
import com.programmers.com.kdtspringorder.order.OrderService;
import com.programmers.com.kdtspringorder.order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {AppConfiguration.class})
@ContextConfiguration // 기본적으로 static을 찾게됨
//@SpringJUnitConfig
@ActiveProfiles("test")
public class KdtSpringContextTests {

    @Configuration
    @ComponentScan(basePackages = {"com.programmers.com.kdtspringorder.order", "com.programmers.com.kdtspringorder.voucher"})

    static class Config {
//        @Bean
//        VoucherRepository voucherRepository() {
//            return new VoucherRepository() {
//                @Override
//                public Optional<Voucher> findById(UUID voucherId) {
//                    return Optional.empty();
//                }
//
//                @Override
//                public Voucher insert(Voucher voucher) {
//                    return null;
//                }
//            };
//        }
    }
    @Autowired
    ApplicationContext context;

    @Autowired
    OrderService orderService;

    @Autowired
    VoucherRepository voucherRepository;

    @Test
    @DisplayName("applicationContext가 생성되야 한다.")
    public void testApplicationContext() {
        assertThat(context, notNullValue());
    }

    @Test
    @DisplayName("VoucherRepository가 빈으로 등록되어 있어야 한다.")
    public void testVoucherRepositoryCreation() {
        var bean = context.getBean(VoucherRepository.class);
        assertThat(bean, notNullValue());
    }

    @Test
    @DisplayName("orderService를 사용해서 주문을 생성할 수 있다.")
    public void testOrderService() {
        // Given
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
        voucherRepository.insert(fixedAmountVoucher);

        // When
        var order = orderService.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(), 200, 1)), fixedAmountVoucher.getVoucherId());

        // Then
        assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(), is(false));
        assertThat(order.getVoucher().get().getVoucherId(), is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(), is(OrderStatus.ACCEPTED));
    }
}