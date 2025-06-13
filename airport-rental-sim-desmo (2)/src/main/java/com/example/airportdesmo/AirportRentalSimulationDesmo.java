package com.example.airportdesmo;

import desmoj.core.simulator.Experiment;

public class AirportRentalSimulationDesmo {


    public static void main(String[] args) {
        double hours = 80;
        if (args.length > 1 && "--hours".equals(args[0])) {
            hours = Double.parseDouble(args[1]);
        }

        Experiment exp = new Experiment("AirportSim-Desmo");
        exp.setShowProgressBar(false);
        AirportModel model = new AirportModel(hours, 10, 8, 9, exp);

        exp.stop(new desmoj.core.simulator.TimeInstant(hours)); // stop after given hours
        exp.traceOff(new desmoj.core.simulator.TimeInstant(0)); // disable tracing
        exp.start();

        exp.report();
        exp.finish();

        System.out.println("Simulation finished after " + hours + " hours.");
    }
}
