package com.juanmartin.orderbook.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class Trade {
    private static final AtomicLong idGenerator = new AtomicLong(1);

    private final long tradeId;

    /* If we reference the order instead of the ID, the order could change its status or quantity,
    and it could lead to confusion. This is also better for memory usage. */
    private final long buyerOrderId;
    private final long sellerOrderId;

    private final BigDecimal executionPrice;
    private final long executionQuantity;
    private final Instant timestamp;

    public Trade(long buyerOrderId, long sellerOrderId, BigDecimal executionPrice, long executionQuantity) {
        this.tradeId = idGenerator.getAndIncrement();
        this.buyerOrderId = buyerOrderId;
        this.sellerOrderId = sellerOrderId;
        this.executionPrice = executionPrice;
        this.executionQuantity = executionQuantity;
        this.timestamp = Instant.now();
    }

    // To String
    @Override
    public String toString() {
        return "Trade{" +
                "tradeId=" + tradeId +
                ", buyerOrderId=" + buyerOrderId +
                ", sellerOrderId=" + sellerOrderId +
                ", executionPrice=" + executionPrice +
                ", executionQuantity=" + executionQuantity +
                ", timestamp=" + timestamp +
                '}';
    }

    // Getters

    public long getTradeId() {
        return tradeId;
    }

    public long getBuyerOrderId() {
        return buyerOrderId;
    }

    public long getSellerOrderId() {
        return sellerOrderId;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public long getExecutionQuantity() {
        return executionQuantity;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
