package com.juanmartin.orderbook.simulator;

import com.juanmartin.orderbook.domain.Order;
import com.juanmartin.orderbook.domain.OrderType;
import com.juanmartin.orderbook.engine.MatchingEngine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class OrderGenerator implements Runnable {

    private final MatchingEngine matchingEngine;
    private final String identifier;

    public OrderGenerator(MatchingEngine matchingEngine, String identifier) {
        this.matchingEngine = matchingEngine;
        this.identifier = identifier;
    }

    @Override
    public void run() {

        Random random = new Random();

        while (!Thread.currentThread().isInterrupted()) {
            OrderType type = (random.nextInt(2) == 1) ? OrderType.ASK : OrderType.BID;

            double min = (type == OrderType.ASK) ? 100.00 : 90.00;
            double max = (type == OrderType.ASK) ? 140.00 : 120.00;

            // Price generator (Asks a little higher than bids)
            double randomDouble = ThreadLocalRandom.current().nextDouble(min, max);

            // Converting it to BigDecimal and formating the value to two decimal places.
            // Rounds up if the third decimal digit is 5 or greater.
            BigDecimal newPrice = BigDecimal.valueOf(randomDouble).setScale(2, RoundingMode.HALF_UP);

            // Random Quantity
            int newQuantity = random.nextInt(10, 100);

            Order newOrder = new Order(type, newPrice, newQuantity);

            try {
                matchingEngine.submitOrder(newOrder);
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Clean exit on shutdown
                break;
            }
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public MatchingEngine getMatchingEngine() {
        return matchingEngine;
    }
}
