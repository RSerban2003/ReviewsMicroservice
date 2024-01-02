package nl.tudelft.sem.v20232024.team08b.controllers;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.api.PapersAPI;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Paper;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperStatus;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
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

    /**
     * Retrieves a summary view of a paper, which includes only its title and abstract, for a specific requester.
     * This method also checks if the requester has the appropriate permissions.
     *
     * This method handles the caught exceptions and converts them into appropriate HTTP status responses.
     * - NotFoundException results in an HTTP NOT_FOUND status if the paper with the specified ID is not found.
     * - IllegalAccessException results in an HTTP FORBIDDEN status if the requester does not have the appropriate permissions.
     * - Other general exceptions result in an HTTP INTERNAL_SERVER_ERROR status, indicating an internal server issue.
     *
     * @param requesterID The unique identifier of the user making the request.
     * @param paperID The unique identifier of the paper whose title and abstract are being requested.
     * @return A ResponseEntity containing the paper summary, which includes the title and abstract of the paper.
     */
    @Override
    public ResponseEntity<PaperSummary> getTitleAndAbstract(Long requesterID,
                                                            Long paperID) {
        try {
            return ResponseEntity.ok(papersService.getTitleAndAbstract(requesterID, paperID));
        } catch (IllegalCallerException | NotFoundException e) {
            // The requested paper was not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            // The requester must be a reviewer assigned to the given paper or a chair,
            // and the review phase must have started
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            // Internal server error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a paper by its ID, omitting author names, for a specific requester.
     * It also checks whether the requester has the appropriate permissions.
     *
     * This method handles the caught exceptions and converts them into appropriate HTTP status responses.
     * - NotFoundException results in an HTTP NOT_FOUND status if the paper with the specified ID is not found.
     * - IllegalAccessException results in an HTTP FORBIDDEN status if the requester does not have the appropriate permissions.
     * - Other general exceptions result in an HTTP INTERNAL_SERVER_ERROR status, indicating an internal server issue.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return A ResponseEntity indicating the success of the request or the type of failure if an error occurs.
     */
    @Override
    public ResponseEntity<Paper> get(Long requesterID,
                                     Long paperID) {
        try {
            return ResponseEntity.ok(papersService.getPaper(requesterID, paperID));
        } catch (IllegalCallerException | NotFoundException e) {
            // The requested paper was not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            // The requester must be a reviewer assigned to the given paper or a chair,
            // and the review phase must have started
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            // Internal server error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds with whether the paper has been accepted or rejected
     * or if it hasn't been decided yet.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<PaperStatus> getState(Long requesterID,
                                                Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
