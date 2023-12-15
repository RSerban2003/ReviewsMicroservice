package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.api.AssignmentsAPI;
import nl.tudelft.sem.v20232024.team08b.application.AssignmentsService;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummaryWithID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
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

    /**
     * Manually assigns reviewer to a specific paper. At least 3
     * reviewers must be assigned to a paper.
     *
     * @param requesterID the ID of the requesting user
     * @param reviewerID the ID of the reviewer
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Void> assignManual(Long requesterID,
                                             Long reviewerID,
                                             Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Automatically assigns a reviewer to a specific paper.
     *
     * @param requesterID the ID of the requesting user the ID of the requesting user
     * @param conferenceID the ID of the conference
     * @param trackID the ID of the track
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Void> assignAuto(Long requesterID,
                                           Long conferenceID,
                                           Long trackID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Finalises the assignment of reviewers, so they can no longer be changed
     * manually or automatically.
     *
     * @param requesterID the ID of the requesting user
     * @param conferenceID the ID of the conference
     * @param trackID the ID of the track
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Void> finalization(Long requesterID,
                                             Long conferenceID,
                                             Long trackID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Get current assignments for a paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<List<Long>> assignments(Long requesterID,
                                                  Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Removes a reviewer from a paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @param reviewerID the ID of the reviewer
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Void> remove(Long requesterID,
                                       Long paperID,
                                       Long reviewerID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Gets all papers a reviewer (the requester) is assigned to.
     *
     * @param requesterID the ID of the requesting user
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<List<PaperSummaryWithID>> getAssignedPapers(Long requesterID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
