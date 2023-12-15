package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.api.BidsAPI;
import nl.tudelft.sem.v20232024.team08b.application.BidsService;
import org.springframework.beans.factory.annotation.Autowired;

public class BidsController implements BidsAPI {
    private final BidsService bidsService;

    /**
     * Default constructor for the controller.
     *
     * @param bidsService the respective service to inject
     */
    @Autowired
    public BidsController(BidsService bidsService) {
        this.bidsService = bidsService;
    }

}
