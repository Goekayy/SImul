package com.example.airportdesmo;

import desmoj.core.dist.ContDistExponential;
import desmoj.core.simulator.*;

public class PassengerGenerator extends SimProcess {

    private final ProcessQueue<Passenger> myQueue;
    private final ContDistExponential interArrival;
    private final Station origin;

    public PassengerGenerator(Model owner, String name, boolean trace,
                              ProcessQueue<Passenger> queue,
                              ContDistExponential ia,
                              Station origin) {
        super(owner, name, trace);
        this.myQueue = queue;
        this.interArrival = ia;
        this.origin = origin;
    }

    @Override
    public void lifeCycle() {
        AirportModel model = (AirportModel) getModel();
        while (presentTime().getTimeAsDouble() < model.SIM_HOURS) {
            /* Create passenger */
            Station dest = (origin == Station.RS)
                    ? (model.random().nextBoolean() ? Station.T1 : Station.T2)
                    : Station.RS;

            Passenger p = new Passenger(model, "Pax", false, origin, dest, presentTime().getTimeAsDouble());
            myQueue.insert(p);

            /* Wait for next arrival */
            hold(new TimeSpan(interArrival.sample()));
        }
    }
}
