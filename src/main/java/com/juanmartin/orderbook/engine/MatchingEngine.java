package com.juanmartin.orderbook.engine;

import com.juanmartin.orderbook.domain.Order;
import com.juanmartin.orderbook.domain.OrderType;
import com.juanmartin.orderbook.domain.Status;
import com.juanmartin.orderbook.domain.Trade;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MatchingEngine {
    private OrderBook orderBook = new OrderBook();
    private ConcurrentLinkedQueue<Trade> tradeLedger = new ConcurrentLinkedQueue<>();

    // Order buffer (temporary data storage for new orders)
    private ArrayBlockingQueue<Order> ingestionQueue = new ArrayBlockingQueue<>(1000);

    public MatchingEngine() {
    }

    public void submitOrder(Order order) throws InterruptedException {
        ingestionQueue.put(order);
    }

    public void start(){

        Thread consumer = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Order order = ingestionQueue.take();
                    processOrder(order);
                } catch (InterruptedException e) {
                    // Restore interrupted state and exit loop
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        // Making it daemon so it doesn't block JVM exit
        consumer.setDaemon(true);
        consumer.start();
    }

    public void processOrder(Order order){
        if (order.getOrderType() == OrderType.ASK){
            while (order.getQuantity() > 0){
                // Checks for the best bid
                Map.Entry<BigDecimal, ConcurrentLinkedQueue<Order>> bestBidEntry = orderBook.getBestBid();
                if (bestBidEntry == null){
                    break;
                }
                Order bestBid = bestBidEntry.getValue().peek();

                // Check if best bid (buyer) price is greater or equal to ask (seller) price
                if (bestBid.getPrice().compareTo(order.getPrice()) >=  0){
                    // Calculate trade quantity, perform trade and log
                    long tradeQuantity = Math.min(order.getQuantity(), bestBid.getQuantity());
                    order.decreaseQuantity(tradeQuantity);
                    bestBid.decreaseQuantity(tradeQuantity);

                    Trade newTrade = new Trade(order.getOrderId(), bestBid.getOrderId(), bestBid.getPrice(), tradeQuantity);
                    tradeLedger.add(newTrade);

                    // Fulfill orders
                    if (bestBid.getQuantity() == 0){
                        bestBid.setOrderStatus(Status.FILLED);
                        bestBidEntry.getValue().poll();

                        // Cleanup price level if applies
                        orderBook.removePriceLevelIfEmpty(bestBid);
                    }else {
                        bestBid.setOrderStatus(Status.PARTIAL);
                    }
                    if (order.getQuantity() == 0){
                        order.setOrderStatus(Status.FILLED);
                    }

                }else {
                    break;
                }
            }

            // Set Partial status and add order to book if not fully fulfilled
            if (order.getQuantity() > 0){
                if (order.getOriginalQuantity() > order.getQuantity()){
                    order.setOrderStatus(Status.PARTIAL);
                }
                orderBook.addOrder(order);
            }
        }else {
            while (order.getQuantity() > 0){
                // Checks for the best ask
                Map.Entry<BigDecimal, ConcurrentLinkedQueue<Order>> bestAskEntry = orderBook.getBestAsk();
                if (bestAskEntry == null){
                    break;
                }
                Order bestAsk = bestAskEntry.getValue().peek();

                // Check if best bid (buyer) price is less or equal to ask (seller) price
                if (bestAsk.getPrice().compareTo(order.getPrice()) <=  0){
                    // Calculate trade quantity, perform trade and log
                    long tradeQuantity = Math.min(order.getQuantity(), bestAsk.getQuantity());
                    order.decreaseQuantity(tradeQuantity);
                    bestAsk.decreaseQuantity(tradeQuantity);

                    Trade newTrade = new Trade(order.getOrderId(), bestAsk.getOrderId(), bestAsk.getPrice(), tradeQuantity);
                    tradeLedger.add(newTrade);

                    // Fulfill orders
                    if (bestAsk.getQuantity() == 0){
                        bestAsk.setOrderStatus(Status.FILLED);
                        bestAskEntry.getValue().poll();

                        // Cleanup price level if applies
                        orderBook.removePriceLevelIfEmpty(bestAsk);
                    }else {
                        bestAsk.setOrderStatus(Status.PARTIAL);
                    }
                    if (order.getQuantity() == 0){
                        order.setOrderStatus(Status.FILLED);
                    }

                }else {
                    break;
                }
            }

            // Set Partial status and add order to book if not fully fulfilled
            if (order.getQuantity() > 0){
                if (order.getOriginalQuantity() > order.getQuantity()){
                    order.setOrderStatus(Status.PARTIAL);
                }
                orderBook.addOrder(order);
            }
        }
    }
}