package com.juanmartin.orderbook;

import com.juanmartin.orderbook.analytics.MarketDataAnalyzer;
import com.juanmartin.orderbook.engine.MatchingEngine;
import com.juanmartin.orderbook.simulator.OrderGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) throws InterruptedException {
        MatchingEngine engine = new MatchingEngine();
        engine.start();

        // Broker Threads (Producer)
        ExecutorService producerPool = Executors.newFixedThreadPool(3);
        producerPool.submit(new OrderGenerator(engine, "Broker-1"));
        producerPool.submit(new OrderGenerator(engine, "Broker-2"));
        producerPool.submit(new OrderGenerator(engine, "Broker-3"));

        // Thread for Data analysis
        ScheduledExecutorService reporterService = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "MarketDataReporter");
            thread.setDaemon(true); // Daemon so it doesn't block JVM exit when I shutdown
            return thread;
        });

        reporterService.scheduleAtFixedRate(
                () -> MarketDataAnalyzer.printReport(engine.getTradeLedger()),
                5, // Initial delay
                5, // Execute every
                TimeUnit.SECONDS
        );

        // Runtime of the app, Sleeping the main Thread for x ms
        Thread.sleep(16000);

        producerPool.shutdownNow();
        reporterService.shutdown();

        System.out.println("\n================ FINAL ANALYSIS ================");
        System.out.println("Total Trades Executed: " + engine.getTradeLedger().size());
        System.out.println(
                "Total Trades NOT Executed (didn't find trading partner): "
                        + (engine.getOrderBook().getBids().size() + engine.getOrderBook().getAsks().size())
        );
        MarketDataAnalyzer.printReport(engine.getTradeLedger());
        // engine.getOrderBook().printOrderBook();
    }
}
