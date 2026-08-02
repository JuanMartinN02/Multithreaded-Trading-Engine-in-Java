package com.juanmartin.orderbook;

import com.juanmartin.orderbook.domain.Order;
import com.juanmartin.orderbook.domain.OrderType;
import com.juanmartin.orderbook.engine.MatchingEngine;
import com.juanmartin.orderbook.simulator.OrderGenerator;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MatchingEngine engine = new MatchingEngine();
        engine.start();
        ExecutorService producerPool = Executors.newFixedThreadPool(3);
        producerPool.submit(new OrderGenerator(engine, "Broker-1"));
        producerPool.submit(new OrderGenerator(engine, "Broker-2"));
        producerPool.submit(new OrderGenerator(engine, "Broker-3"));

        // Runtime for 5 seconds[
        Thread.sleep(5000);

        producerPool.shutdownNow();

        System.out.println("Total Trades Executed: " + engine.getTradeLedger().size());
        engine.getOrderBook().printOrderBook();
    }
}