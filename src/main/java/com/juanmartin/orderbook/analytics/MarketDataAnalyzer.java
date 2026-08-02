package com.juanmartin.orderbook.analytics;

import com.juanmartin.orderbook.domain.Trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MarketDataAnalyzer {

    public static void printReport(ConcurrentLinkedQueue<Trade> tradeLedger){
        if (tradeLedger.isEmpty()){
            System.out.println("No trades have been made.");
            return;
        }

        // 1.- Total Volume
        long totalVolume = tradeLedger.stream()
                .mapToLong(Trade::getExecutionQuantity)
                .sum();

        // 1.1.- Total Volume in $
        BigDecimal totalVolumeDollars = tradeLedger.stream()
                .map(Trade::getExecutionPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2,- Max and Min trade prices
        BigDecimal maxPrice = tradeLedger.stream()
                .map(Trade::getExecutionPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal minPrice = tradeLedger.stream()
                .map(Trade::getExecutionPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 3.- VWAP [SUM(TradePrice * TradeQuantity)]/[SUM(TradeExecutedVolume)]

        // SUM(TradePrice * TradeQuantity)] (numerator)
        BigDecimal numerator = tradeLedger.stream()
                .map(trade -> trade.getExecutionPrice().multiply(BigDecimal.valueOf(trade.getExecutionQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Reduce agregates individual values starting at zero

        // SUM(TradeExecutedVolume) (denom) is the same as totalVolume

        // VWAP
        BigDecimal vwap = numerator.divide(BigDecimal.valueOf(totalVolume), 2, RoundingMode.HALF_UP);

        // Print
        System.out.println("\n================ MARKET ANALYTICS SUMMARY ================");
        System.out.println("Total Trades Executed : " + tradeLedger.size());
        System.out.println("Total Volume Traded   : " + totalVolume + " shares");
        System.out.println("Total Volume in $     : $" + totalVolumeDollars);
        System.out.println("High Price            : $" + maxPrice);
        System.out.println("Low Price             : $" + minPrice);
        System.out.println("VWAP                  : $" + vwap);
        System.out.println("=========================================================\n");

    }
}
