package com.programmers.com.kdtspringorder;

import com.programmers.com.kdtspringorder.order.Order;
import com.programmers.com.kdtspringorder.voucher.MemoryVoucherRepository;
import com.programmers.com.kdtspringorder.voucher.Voucher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.util.Optional;
import java.util.UUID;

@Configuration

//@ComponentScan

@ComponentScan(basePackages = {"com.programmers.com.kdtspringorder.order", "com.programmers.com.kdtspringorder.voucher"})

//@ComponentScan(
//        basePackages = {"com.programmers.com.kdtspringorder.voucher","com.programmers.com.kdtspringorder.order"},
//        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MemoryVoucherRepository.class)})

//@ComponentScan(basePackageClasses = {Order.class, Voucher.class})
public class AppConfiguration {
//    @Bean
//    public VoucherRepository voucherRepository() {
//        return new VoucherRepository() {
//            @Override
//            public Optional<Voucher> findById(UUID voucherId) {
//                return Optional.empty();
//            }
//        };
//    }
//
//    @Bean
//    public OrderRepository orderRepository() {
//        return new OrderRepository() {
//            @Override
//            public Order insert(Order order) {
//
//                return order;
//            }
//        };
//    }

//    @Bean
//    public VoucherService voucherService(VoucherRepository voucherRepository) {
//        return new VoucherService(voucherRepository);
//    }
//
//    @Bean
//    public OrderService orderService(VoucherService voucherService, OrderRepository orderRepository) {
//        return new OrderService(voucherService, orderRepository);
//    }

}
