package com.programmers.com.kdtspringorder;

import java.util.UUID;

public interface Voucher {

    UUID getVoucherId();
    public long discount(long beforeDiscount);
}
