package com.example.airportdesmo;

import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;

/** Einfaches Entity mit Zeitstempeln für spätere Auswertungen. */
public class Passenger extends Entity {

    private final Station origin;
    private final Station destination;
    private final double arrivalTime;
    private double boardTime = -1;
    private double alightTime = -1;

    public Passenger(Model owner, String name, boolean showInTrace,
                     Station origin, Station destination, double arrivalTime) {
        super(owner, name, showInTrace);
        this.origin = origin;
        this.destination = destination;
        this.arrivalTime = arrivalTime;
    }

    public Station getDestination() { return destination; }
    public Station getOrigin() { return origin; }

    public void boarded(double time) { boardTime = time; }

    public void alighted(double time) { alightTime = time; }

    public double getQueueWait() { return boardTime < 0 ? -1 : boardTime - arrivalTime; }

    public double getSystemTime() { return alightTime < 0 ? -1 : alightTime - arrivalTime; }
}
