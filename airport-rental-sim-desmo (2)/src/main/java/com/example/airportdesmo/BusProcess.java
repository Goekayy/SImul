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
    public void lifeCycle() throws co.paralleluniverse.fibers.SuspendExecution {
        AirportModel model = (AirportModel) getModel();

        double lastRound = presentTime().getTimeAsDouble();
        while (presentTime().getTimeAsDouble() < model.SIM_HOURS) {

            double arriveTime = presentTime().getTimeAsDouble();

            /* 1. Alight */
            onBoard.removeIf(p -> {
                if (p.getDestination() == currentStation) {
                    p.alighted(presentTime().getTimeAsDouble());
                    switch (p.getOrigin()) {
                        case T1 -> model.systemT1.update(p.getSystemTime());
                        case T2 -> model.systemT2.update(p.getSystemTime());
                        case RS -> model.systemRS.update(p.getSystemTime());
                    }
                    return true;
                }
                return false;
            });

            /* 2. Board */
            Queue<Passenger> queue = switch (currentStation) {
                case T1 -> model.qT1;
                case T2 -> model.qT2;
                case RS -> model.qRS;
            };

            while (onBoard.size() < CAPACITY && !queue.isEmpty()) {
                Passenger p = queue.first();
                queue.remove(p);
                p.boarded(presentTime().getTimeAsDouble());
                onBoard.add(p);
                switch (currentStation) {
                    case T1 -> model.waitT1.update(p.getQueueWait());
                    case T2 -> model.waitT2.update(p.getQueueWait());
                    case RS -> model.waitRS.update(p.getQueueWait());
                }
            }
            switch (currentStation) {
                case T1 -> {
                    model.qLenT1.update(model.qT1.size());
                    if (model.qT1.size() > model.maxQT1) model.maxQT1 = model.qT1.size();
                }
                case T2 -> {
                    model.qLenT2.update(model.qT2.size());
                    if (model.qT2.size() > model.maxQT2) model.maxQT2 = model.qT2.size();
                }
                case RS -> {
                    model.qLenRS.update(model.qRS.size());
                    if (model.qRS.size() > model.maxQRS) model.maxQRS = model.qRS.size();
                }
            }

            /* 3. Minimum dwell 5 minutes (= 5/60 hours) */
            hold(new TimeSpan(5.0 / 60.0));

            /* pick up late arrivals after 5 minutes */
            while (onBoard.size() < CAPACITY && !queue.isEmpty()) {
                Passenger p = queue.first();
                queue.remove(p);
                p.boarded(presentTime().getTimeAsDouble());
                onBoard.add(p);
                switch (currentStation) {
                    case T1 -> model.waitT1.update(p.getQueueWait());
                    case T2 -> model.waitT2.update(p.getQueueWait());
                    case RS -> model.waitRS.update(p.getQueueWait());
                }
            }
            switch (currentStation) {
                case T1 -> {
                    model.qLenT1.update(model.qT1.size());
                    if (model.qT1.size() > model.maxQT1) model.maxQT1 = model.qT1.size();
                    model.dwellT1.update(presentTime().getTimeAsDouble() - arriveTime);
                }
                case T2 -> {
                    model.qLenT2.update(model.qT2.size());
                    if (model.qT2.size() > model.maxQT2) model.maxQT2 = model.qT2.size();
                    model.dwellT2.update(presentTime().getTimeAsDouble() - arriveTime);
                }
                case RS -> {
                    model.qLenRS.update(model.qRS.size());
                    if (model.qRS.size() > model.maxQRS) model.maxQRS = model.qRS.size();
                    model.dwellRS.update(presentTime().getTimeAsDouble() - arriveTime);
                }
            }

            model.busOccupancy.update(onBoard.size());

            /* 4. Travel to next station */
            Station next = currentStation.next();
            double travelMin = switch (currentStation) {
                case RS, T1 -> 7;
                case T2 -> 10;
            };
            hold(new TimeSpan(travelMin / 60.0));
            if (currentStation == Station.RS && next == Station.T1) {
                model.roundDurations.update(presentTime().getTimeAsDouble() - lastRound);
                lastRound = presentTime().getTimeAsDouble();
            }
            currentStation = next;
        }
    }
}
