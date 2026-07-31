package com.juanmartin.orderbook;

import com.juanmartin.orderbook.domain.Order;
import com.juanmartin.orderbook.domain.OrderType;
import com.juanmartin.orderbook.engine.MatchingEngine;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        Order order1 = new Order(OrderType.ASK, BigDecimal.valueOf(100.50), 15);
        Order order1b = new Order(OrderType.ASK, BigDecimal.valueOf(100.50), 30);
        Order order2 = new Order(OrderType.ASK, BigDecimal.valueOf(60.50), 15);

        Order order3 = new Order(OrderType.BID, BigDecimal.valueOf(60.50), 15);
        Order order3b = new Order(OrderType.BID, BigDecimal.valueOf(60.50), 10);
        Order order4 = new Order(OrderType.BID, BigDecimal.valueOf(40), 7);

        MatchingEngine matchingEngine = new MatchingEngine();


        matchingEngine.processOrder(order1);
        matchingEngine.processOrder(order1b);
        matchingEngine.processOrder(order2);
        matchingEngine.processOrder(order3);
        matchingEngine.processOrder(order3b);
        matchingEngine.processOrder(order4);

        matchingEngine.getOrderBook().printOrderBook();

    }
}