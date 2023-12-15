package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.api.PapersAPI;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import org.springframework.beans.factory.annotation.Autowired;

public class PapersController implements PapersAPI {
    private final PapersService papersService;

    /**
     * Default constructor for the controller.
     *
     * @param papersService the respective service to inject
     */
    @Autowired
    public PapersController(PapersService papersService) {
        this.papersService = papersService;
    }
}
