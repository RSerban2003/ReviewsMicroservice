package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.api.PapersAPI;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import nl.tudelft.sem.v20232024.team08b.dtos.Paper;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperStatus;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperSummary;
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
     * Gets the title and abstract of a paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<PaperSummary> getTitleAndAbstract(Long requesterID,
                                                            Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Get the whole paper without the author names.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Paper> get(Long requesterID,
                                     Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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
