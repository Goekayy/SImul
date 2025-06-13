package com.example.airportdesmo;

import desmoj.core.simulator.Experiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirportRentalSimulationDesmo {

    private static final Logger LOG = LoggerFactory.getLogger(AirportRentalSimulationDesmo.class);

    public static void main(String[] args) {
        double hours = 80;
        if (args.length > 1 && "--hours".equals(args[0])) {
            hours = Double.parseDouble(args[1]);
        }

        Experiment exp = new Experiment("AirportSim-Desmo");
        AirportModel model = new AirportModel(hours, 10, 8, 9, exp);

        exp.stop(new desmoj.core.simulator.TimeInstant(hours)); // stop after given hours
        exp.traceOff(); // keep console clean – enable if needed
        exp.start();

        exp.report();
        exp.finish();

        LOG.info("Simulation finished after {} hours.", hours);
    }
}
