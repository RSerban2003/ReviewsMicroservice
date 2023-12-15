package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.api.TracksAPI;
import nl.tudelft.sem.v20232024.team08b.application.TracksService;
import org.springframework.beans.factory.annotation.Autowired;

public class TracksController implements TracksAPI {
    private final TracksService tracksService;

    /**
     * Default constructor for the controller.
     *
     * @param tracksService the respective service to inject
     */
    @Autowired
    public TracksController(TracksService tracksService) {
        this.tracksService = tracksService;
    }

}
