package com.example.airportdesmo;

import desmoj.core.simulator.*;
import desmoj.core.dist.*;
import desmoj.core.statistic.*;

public class AirportModel extends Model {

    /* Parameters */
    public final double SIM_HOURS;
    public final double LAMBDA_T1, LAMBDA_T2, LAMBDA_RS;

    /* Resources & queues */
    public Queue<Passenger> qT1;
    public Queue<Passenger> qT2;
    public Queue<Passenger> qRS;

    /* Distributions */
    public ContDistExponential distT1;
    public ContDistExponential distT2;
    public ContDistExponential distRS;

    /* Statistics */
    public Accumulate qLenT1, qLenT2, qLenRS;
    public long maxQT1 = 0, maxQT2 = 0, maxQRS = 0;
    public Tally waitT1, waitT2, waitRS;
    public Tally systemT1, systemT2, systemRS;
    public Tally busOccupancy;
    public Tally roundDurations;
    public Tally dwellRS, dwellT1, dwellT2;

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
        qT1 = new Queue<>(this, "Queue T1", true, true);
        qT2 = new Queue<>(this, "Queue T2", true, true);
        qRS = new Queue<>(this, "Queue RS", true, true);

        qLenT1 = new Accumulate(this, "Len T1", 0.0, true, true);
        qLenT2 = new Accumulate(this, "Len T2", 0.0, true, true);
        qLenRS = new Accumulate(this, "Len RS", 0.0, true, true);

        waitT1 = new Tally(this, "Wait T1", true, true);
        waitT2 = new Tally(this, "Wait T2", true, true);
        waitRS = new Tally(this, "Wait RS", true, true);

        systemT1 = new Tally(this, "Sys T1", true, true);
        systemT2 = new Tally(this, "Sys T2", true, true);
        systemRS = new Tally(this, "Sys RS", true, true);

        busOccupancy = new Tally(this, "Bus Occ", true, true);
        roundDurations = new Tally(this, "Round Dur", true, true);
        dwellRS = new Tally(this, "Dwell RS", true, true);
        dwellT1 = new Tally(this, "Dwell T1", true, true);
        dwellT2 = new Tally(this, "Dwell T2", true, true);

        /* λ is passengers/hour, DESMO-J works with hours as default time unit */
        distT1 = new ContDistExponential(this, "IA T1", 1.0 / LAMBDA_T1, true, true);
        distT2 = new ContDistExponential(this, "IA T2", 1.0 / LAMBDA_T2, true, true);
        distRS = new ContDistExponential(this, "IA RS", 1.0 / LAMBDA_RS, true, true);

        /* Stream seeds for reproducibility */
        distT1.setSeed(42);
        distT2.setSeed(43);
        distRS.setSeed(44);

        qLenT1.update(0);
        qLenT2.update(0);
        qLenRS.update(0);
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
