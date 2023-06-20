package com.programmers.com.kdtspringorder;

import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID orderid;
    private final UUID customerId;
    private final List<OrderItem> orderItems;
    private FixedAmountVoucher fixedAmountVoucher;
    private OrderStatus orderStatus = OrderStatus.ACCEPTED;

    public Order(UUID orderid, UUID customerId, List<OrderItem> orderItems, long discountAmount) {
        this.orderid = orderid;
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.fixedAmountVoucher = new FixedAmountVoucher(discountAmount);
        this.orderStatus = orderStatus;
    }

    public long totalAmount() {
        var beforeDiscount = orderItems.stream().map(v -> v.getProductPrice() * v.getQuantity())
                .reduce(0L, Long::sum);
        return fixedAmountVoucher.discount(beforeDiscount);
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}