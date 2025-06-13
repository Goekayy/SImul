package com.example.airportdesmo;

import desmoj.core.simulator.*;
import desmoj.core.dist.*;

public class AirportModel extends Model {

    /* Parameters */
    public final double SIM_HOURS;
    public final double LAMBDA_T1, LAMBDA_T2, LAMBDA_RS;

    /* Resources & queues */
    public ProcessQueue<Passenger> qT1;
    public ProcessQueue<Passenger> qT2;
    public ProcessQueue<Passenger> qRS;

    /* Distributions */
    public ContDistExponential distT1;
    public ContDistExponential distT2;
    public ContDistExponential distRS;

    public AirportModel(double simHours, double lT1, double lT2, double lRS, Experiment exp) {
        super(null, "Airport Rental Model", true, true);
        this.SIM_HOURS = simHours;
        LAMBDA_T1 = lT1; LAMBDA_T2 = lT2; LAMBDA_RS = lRS;
        connectToExperiment(exp);
    }

    @Override
    public String description() {
        return "Simple DESMO-J model of a shuttle between RS, T1, T2.";
    }

    @Override
    public void init() {
        /* Queues */
        qT1 = new ProcessQueue<>(this, "Queue T1", true, true);
        qT2 = new ProcessQueue<>(this, "Queue T2", true, true);
        qRS = new ProcessQueue<>(this, "Queue RS", true, true);

        /* λ is passengers/hour, DESMO-J works with hours as default time unit */
        distT1 = new ContDistExponential(this, "IA T1", 1.0 / LAMBDA_T1, true, true);
        distT2 = new ContDistExponential(this, "IA T2", 1.0 / LAMBDA_T2, true, true);
        distRS = new ContDistExponential(this, "IA RS", 1.0 / LAMBDA_RS, true, true);

        /* Stream seeds for reproducibility */
        distT1.setSeed(42);
        distT2.setSeed(43);
        distRS.setSeed(44);
    }

    @Override
    public void doInitialSchedules() {
        /* Passenger generators */
        new PassengerGenerator(this, "Gen T1", true, qT1, distT1, Station.T1).activate();
        new PassengerGenerator(this, "Gen T2", true, qT2, distT2, Station.T2).activate();
        new PassengerGenerator(this, "Gen RS", true, qRS, distRS, Station.RS).activate();

        /* Bus */
        new BusProcess(this, "Shuttle Bus", true).activate();
    }
}
