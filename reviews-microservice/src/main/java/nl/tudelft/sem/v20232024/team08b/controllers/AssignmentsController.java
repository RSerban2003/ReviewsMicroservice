package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.api.AssignmentsAPI;
import nl.tudelft.sem.v20232024.team08b.application.AssignmentsService;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignmentsController implements AssignmentsAPI {
    private final AssignmentsService assignmentsService;

    /**
     * Default constructor for the controller.
     *
     * @param assignmentsService the respective service to inject
     */
    @Autowired
    public AssignmentsController(AssignmentsService assignmentsService) {
        this.assignmentsService = assignmentsService;
    }

}
