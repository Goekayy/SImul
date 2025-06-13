package com.example.airportdesmo;

import desmoj.core.simulator.*;

import java.util.ArrayList;
import java.util.List;

public class BusProcess extends SimProcess {

    private static final int CAPACITY = 20;
    private final List<Passenger> onBoard = new ArrayList<>(CAPACITY);
    private Station currentStation = Station.RS;

    public BusProcess(Model owner, String name, boolean trace) {
        super(owner, name, trace);
    }

    @Override
    public void lifeCycle() {
        AirportModel model = (AirportModel) getModel();

        while (presentTime().getTimeAsDouble() < model.SIM_HOURS) {

            /* 1. Alight */
            onBoard.removeIf(p -> {
                if (p.getDestination() == currentStation) {
                    p.alighted(presentTime().getTimeAsDouble());
                    return true;
                }
                return false;
            });

            /* 2. Board */
            ProcessQueue<Passenger> queue = switch (currentStation) {
                case T1 -> model.qT1;
                case T2 -> model.qT2;
                case RS -> model.qRS;
            };

            while (onBoard.size() < CAPACITY && !queue.isEmpty()) {
                Passenger p = queue.first();
                queue.remove(p);
                p.boarded(presentTime().getTimeAsDouble());
                onBoard.add(p);
            }

            /* 3. Minimum dwell 5 minutes (= 5/60 hours) */
            hold(new TimeSpan(5.0 / 60.0));

            /* 4. Travel to next station */
            Station next = currentStation.next();
            double travelMin = switch (currentStation) {
                case RS, T1 -> 7;
                case T2 -> 10;
            };
            hold(new TimeSpan(travelMin / 60.0));
            currentStation = next;
        }
    }
}
