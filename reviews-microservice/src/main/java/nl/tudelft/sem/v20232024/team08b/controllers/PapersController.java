package nl.tudelft.sem.v20232024.team08b.controllers;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.api.PapersAPI;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Paper;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperStatus;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
     * This method retrieves a summary view of a paper, which includes only its title and abstract.
     *
     * @param requesterID The unique identifier of the user making the request.
     * @param paperID The unique identifier of the paper whose title and abstract are being requested.
     * @return A ResponseEntity containing the paper summary, which includes the title and abstract of the paper.
     * @throws NotFoundException If the paper with the specified ID is not found.
     * @throws IllegalAccessException If the requesting user does not have the appropriate permissions to access the paper.
     * @throws Exception For other general errors, indicating an internal server issue.
     */
    @Override
    public ResponseEntity<PaperSummary> getTitleAndAbstract(Long requesterID,
                                                            Long paperID) {
        try {
            papersService.getTitleAndAbstract(requesterID, paperID);
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
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Retrieves a paper by its ID, omitting author names, for a specific requester.
     * It also checks whether the requester has the appropriate permissions.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return response entity indicating the success of the request
     * @throws NotFoundException If the paper with the specified ID is not found.
     * @throws IllegalAccessException If the requesting user does not have the appropriate permissions to access the paper.
     * @throws Exception For other general errors, indicating an internal server issue.
     */
    @Override
    public ResponseEntity<Paper> get(Long requesterID,
                                     Long paperID) {
        try {
            papersService.getPaper(requesterID, paperID);
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
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();
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
