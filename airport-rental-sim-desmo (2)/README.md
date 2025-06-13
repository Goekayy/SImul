# Airport Rental Shuttle – DESMO‑J Version

A minimal discrete‑event simulation of a 20‑seat shuttle circulating
**RS → T1 → T2 → RS** using the **DESMO‑J** library.
The bus waits at least five minutes at each stop and departs immediately once
no boarding/alighting occurs.

## Build & Run

```bash
mvn clean package
java -jar target/airport-rental-sim-desmo-1.0.0-jar-with-dependencies.jar   # 80 h default
java -jar target/airport-rental-sim-desmo-1.0.0-jar-with-dependencies.jar --hours 120
```

## Model Overview

| Component | Implementation |
|-----------|----------------|
| Passenger arrivals | `PassengerGenerator` (one per station) with exponential inter‑arrival times |
| Shuttle bus | `BusProcess` looping through the stations with dynamic dwell logic (≥5 min) |
| Queues | `Queue<Passenger>` per station |
| Metrics | Various `Tally`/`Accumulate` stats for waits, queue lengths, bus rounds, dwell times, and system time |

The model is intentionally compact (≈250 LOC) to highlight how DESMO‑J replaces the custom event loop.  
Feel free to extend capacities, add multiple buses, or collect additional statistics.
