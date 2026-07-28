package com.juanmartin.orderbook.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class Order {
    private static final AtomicLong idGenerator = new AtomicLong(0);

    private final long orderId;
    private final OrderType orderType;
    private BigDecimal price;
    private long quantity;
    private Instant timestamp;
    private Status orderStatus;

    public Order(OrderType orderType, BigDecimal price, long quantity, Instant timestamp, Status orderStatus) {
        this.orderId = idGenerator.incrementAndGet();
        this.orderType = orderType;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.orderStatus = orderStatus;
    }

    public Order(OrderType orderType) {
        this.orderId = idGenerator.incrementAndGet();
        this.orderType = orderType;
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
