package com.juanmartin.orderbook;

import com.juanmartin.orderbook.domain.Order;
import com.juanmartin.orderbook.domain.OrderType;
import com.juanmartin.orderbook.engine.OrderBook;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Order order1 = new Order(OrderType.ASK, BigDecimal.valueOf(100.50), 15);
        Order order1b = new Order(OrderType.ASK, BigDecimal.valueOf(100.50), 30);
        Order order2 = new Order(OrderType.ASK, BigDecimal.valueOf(60.50), 15);

        Order order3 = new Order(OrderType.BID, BigDecimal.valueOf(60.50), 15);
        Order order3b = new Order(OrderType.BID, BigDecimal.valueOf(60.50), 10);
        Order order4 = new Order(OrderType.BID, BigDecimal.valueOf(40), 7);

        OrderBook book = new OrderBook();

        book.addOrder(order1);
        book.addOrder(order1b);
        book.addOrder(order2);
        book.addOrder(order3);
        book.addOrder(order3b);
        book.addOrder(order4);

        book.printOrderBook();

    }
}