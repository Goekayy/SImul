package com.example.airportdesmo;

/** Haltestellen des Shuttle-Busses in Rundkurs-Reihenfolge. */
public enum Station {
    RS, T1, T2;

    public Station next() {
        return switch (this) {
            case RS -> T1;
            case T1 -> T2;
            case T2 -> RS;
        };
    }
}
