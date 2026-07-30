package com.juanmartin.orderbook.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class Order {
    private static final AtomicLong idGenerator = new AtomicLong(1);

    private final long orderId = idGenerator.incrementAndGet();
    private final OrderType orderType;
    private final BigDecimal price;
    private final long quantity;
    private final Instant timestamp = Instant.now();

    private long remainingQuantity;
    private Status orderStatus;

    public Order(OrderType orderType, BigDecimal price, long quantity) {
        this.orderType = orderType;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        orderStatus = Status.NEW;
    }

    // To String

    @Override
    public String toString() {
        return "[ Order #" + orderId + "(" + remainingQuantity + " shares remaining) ]";
    }

    public void decreaseQuantity(long amount){
        if (amount >= this.remainingQuantity){
            this.remainingQuantity = remainingQuantity - amount;
        }else {
            throw new IllegalArgumentException("Cannot decrease more than amount available for Order");
        }
    }

    // Setters

    public void setOrderStatus(Status orderStatus) {
        this.orderStatus = orderStatus;
    }

    // Getters

    public long getOrderId() {
        return orderId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Status getOrderStatus() {
        return orderStatus;
    }
}
