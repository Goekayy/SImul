# Airport Rental Shuttle – DESMO‑J Version

A minimal discrete‑event simulation of a 20‑seat shuttle circulating  
**RS → T1 → T2 → RS** using the **DESMO‑J** library.

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
| Shuttle bus | `BusProcess` looping through the three stations, enforcing 5 min dwell & travel times |
| Queues | `ProcessQueue<Passenger>` per station |
| Metrics | For brevity this skeleton just prints DESMO‑J’s standard report; extend with `Tally` etc. as needed. |

The model is intentionally compact (≈250 LOC) to highlight how DESMO‑J replaces the custom event loop.  
Feel free to extend capacities, add multiple buses, or collect additional statistics.
