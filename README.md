```markdown
# Multithreaded Limit Order Book & Matching Engine in Java

A high-throughput, concurrent limit order book and matching engine built in core Java. It simulates a real-time electronic exchange by handling asynchronous order submission, fast price-time priority matching, and live stream-based market data analytics.

## System Architecture Overview

               ┌────────────────────────┐
               │    Order Generator     │
               │   (Producer Threads)   │
               └───────────┬────────────┘
                           │
                           ▼
               ┌────────────────────────┐
               │  ArrayBlockingQueue    │
               │   (Ingestion Buffer)   │
               └───────────┬────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│                    Matching Engine                     │
│               (Single Consumer Thread)                 │
│  ┌──────────────────────────────────────────────────┐  │
│  │                    OrderBook                     │  │
│  │  • Bids: ConcurrentSkipListMap (Reverse Order)   │  │
│  │  • Asks: ConcurrentSkipListMap (Natural Order)   │  │
│  │  • Price Level: ConcurrentLinkedQueue (FIFO)     │  │
│  └────────────────────────┬─────────────────────────┘  │
│                           │                            │
│                           ▼                            │
│                  TradeLedger Queue                     │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
               ┌────────────────────────┐
               │   MarketDataAnalyzer   │
               │  (Scheduled Reporter)  │
               └────────────────────────┘

```

---

## Key Architectural & Design Decisions

### 1. Lock-Free Single-Consumer Pattern

Instead of wrapping the entire matching engine in heavy `synchronized` blocks, order matching runs on a dedicated single consumer thread reading from an `ArrayBlockingQueue`. This lock-free producer-consumer pattern allows multiple client threads (producers) to push orders into the engine concurrently while guaranteeing single-threaded execution during order execution—preventing race conditions and maximizing matching throughput.

### 2. $O(1)$ Price-Time Priority Matching Data Structures

* **Bids and Asks (`ConcurrentSkipListMap`):** Sorts price levels automatically. Asks are ordered ascending (lowest seller first), and Bids are ordered descending (highest buyer first).
* **Orders per Price Level (`ConcurrentLinkedQueue`):** Orders at the exact same price level are queued sequentially to strictly enforce FIFO (First-In, First-Out) time priority.

### 3. Non-Blocking Market Analytics

To prevent analytics from slowing down the matching core, a background `ScheduledExecutorService` queries the shared `TradeLedger` every 5 seconds. It uses **Java Streams & Lambdas** to compute key exchange metrics on the fly without locking execution.

---

## Features

* **Price-Time Priority (FIFO):** Matches orders based on best price first, breaking ties by order arrival time.
* **Partial & Full Fills:** Fully supports order splitting and status tracking (`NEW`, `PARTIAL`, `FILLED`).
* **Concurrent Ingestion Pipeline:** Uses `ArrayBlockingQueue` with backpressure support to queue inbound orders safely.
* **Live Market Analytics:**
* Total Executed Volume (shares)
* Period High & Low Prices
* **VWAP** (Volume-Weighted Average Price):

$$\text{VWAP} = \frac{\sum (\text{Price} \times \text{Quantity})}{\sum \text{Total Quantity}}$$





---

## Project Structure

```text
src/main/java/com/juanmartin/orderbook/
├── analytics/
│   └── MarketDataAnalyzer.java    # Stream calculations (VWAP, High/Low, Volume)
├── domain/
│   ├── Order.java                 # Core domain entity & state management
│   ├── OrderType.java             # BID / ASK enum
│   ├── Status.java                # NEW, PARTIAL, FILLED
│   └── Trade.java                 # Immutable execution record
├── engine/
│   ├── MatchingEngine.java        # Engine core & ingestion loop
│   └── OrderBook.java             # Bids/Asks skip lists & queue management
├── simulator/
│   └── OrderGenerator.java        # Producer simulating broker order stream
└── Main.java                      # Multi-threaded execution & runner

```

---

## How to Run

### Requirements

* **JDK 17+**
* Maven or IntelliJ IDEA / Eclipse

### Running the Engine

1. Clone the repository:
```bash
git clone https://github.com/your-username/Multithreaded-Trading-Engine-in-Java.git

```


2. Build and run `App.java`:
```bash
cd Multithreaded-Trading-Engine-in-Java
javac src/main/java/com/juanmartin/orderbook/App.java
java com.juanmartin.orderbook.App

```



---

## Sample Terminal Output

```text
================ MARKET ANALYTICS SUMMARY ================
Total Trades Executed : 98
Total Volume Traded   : 4812 shares
High Price            : $138.94
Low Price             : $100.03
VWAP                  : $118.42
=========================================================

****************************************************************************************************
*** ASK Book ***
112.87$ asks: [[ Order #216(2 shares remaining) ], [ Order #234(92 shares remaining) ]]
114.56$ asks: [[ Order #164(11 shares remaining) ]]
115.07$ asks: [[ Order #222(84 shares remaining) ]]

*** BID Book ***
106.05$ bids: [[ Order #142(53 shares remaining) ]]
105.35$ bids: [[ Order #136(87 shares remaining) ]]
105.15$ bids: [[ Order #71(44 shares remaining) ]]
****************************************************************************************************

```
