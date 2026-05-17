package com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;

public record WaitlistOrder(
        Order order
) {
    public WaitlistOrder {
        Preconditions.requireNonNull(order, "order");
    }

    public static WaitlistOrder fifo() {
        return new WaitlistOrder(Order.FIFO);
    }

    public enum Order {
        FIFO,
        LIFO,
        RECENTLY_CREATED
    }
}