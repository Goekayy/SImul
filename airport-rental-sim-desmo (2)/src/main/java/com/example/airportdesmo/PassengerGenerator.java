package com.example.airportdesmo;

import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.*;
import java.util.Random;

public class PassengerGenerator extends SimProcess {

    private final Queue<Passenger> myQueue;
    private final ContDistExponential interArrival;
    private final Station origin;
    private final Random rng = new Random(42);

    public PassengerGenerator(Model owner, String name, boolean trace,
                              Queue<Passenger> queue,
                              ContDistExponential ia,
                              Station origin) {
        super(owner, name, trace);
        this.myQueue = queue;
        this.interArrival = ia;
        this.origin = origin;
    }

    @Override
    public void lifeCycle() throws co.paralleluniverse.fibers.SuspendExecution {
        AirportModel model = (AirportModel) getModel();
        while (presentTime().getTimeAsDouble() < model.SIM_HOURS) {
            /* Create passenger */
            Station dest = (origin == Station.RS)
                    ? (rng.nextBoolean() ? Station.T1 : Station.T2)
                    : Station.RS;

            Passenger p = new Passenger(model, "Pax", false, origin, dest, presentTime().getTimeAsDouble());
            myQueue.insert(p);
            switch (origin) {
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

            /* Wait for next arrival */
            hold(new TimeSpan(interArrival.sample()));
        }
    }
}
