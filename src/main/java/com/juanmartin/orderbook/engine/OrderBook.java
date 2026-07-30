package com.juanmartin.orderbook.engine;

import com.juanmartin.orderbook.domain.Order;
import com.juanmartin.orderbook.domain.OrderType;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

// Container that stores and organizes the orders that haven't been completed
public class OrderBook {
    // ConcurrentSkipListMap as it automatically keeps keys sorted
    /* ConcurrentLinkedQueue ensures that When multiple orders come
    in at the exact same price , the queue ensures the order that arrived
    first gets executed first FIFO*/
    // Key is the Price of the order and the Queue is for the orders in FIFO
    ConcurrentSkipListMap<BigDecimal, ConcurrentLinkedQueue<Order>> bids = new ConcurrentSkipListMap<>();
    ConcurrentSkipListMap<BigDecimal, ConcurrentLinkedQueue<Order>> asks = new ConcurrentSkipListMap<>(Comparator.reverseOrder());

    public void addOrder(Order order){
        // Check if order is ASK or BID
        if (order.getOrderType() == OrderType.ASK){
            // Check if price exists on the book Keys, if not creates a new Queue for that price.
            if(asks.containsKey(order.getPrice())){
                ConcurrentLinkedQueue<Order> orderList = asks.get(order.getPrice());
                orderList.add(order);
            }else {
                ConcurrentLinkedQueue<Order> orderList = new ConcurrentLinkedQueue<>();
                orderList.add(order);
                asks.put(order.getPrice(), orderList);
            }
        }else {
            // Check if price exists on the book Keys, if not creates a new Queue for that price.
            if(bids.containsKey(order.getPrice())){
                ConcurrentLinkedQueue<Order> orderList = bids.get(order.getPrice());
                orderList.add(order);
            }else {
                ConcurrentLinkedQueue<Order> orderList = new ConcurrentLinkedQueue<>();
                orderList.add(order);
                bids.put(order.getPrice(), orderList);
            }
        }
    }

    // Get highest bid
    public Map.Entry<BigDecimal, ConcurrentLinkedQueue<Order>> getBestBid(){
        return bids.firstEntry();
    }

    // Get lowest ask
    public Map.Entry<BigDecimal, ConcurrentLinkedQueue<Order>> getBestAsk(){
        return asks.firstEntry();
    }

    // Cleanup of unused keys in books
    public void removePriceLevelIfEmpty(Order order){
        if (order.getOrderType() == OrderType.ASK){
            ConcurrentLinkedQueue<Order> orderList = asks.get(order.getPrice());
            if (orderList.isEmpty()){asks.remove(order.getPrice());};
        }else {
            ConcurrentLinkedQueue<Order> orderList = bids.get(order.getPrice());
            if (orderList.isEmpty()){bids.remove(order.getPrice());};
        }
    }


}
