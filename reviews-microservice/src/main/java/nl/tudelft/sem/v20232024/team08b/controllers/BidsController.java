package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.api.BidsAPI;
import nl.tudelft.sem.v20232024.team08b.application.BidsService;
import nl.tudelft.sem.v20232024.team08b.dtos.Bid;
import nl.tudelft.sem.v20232024.team08b.dtos.BidByReviewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
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

    /**
     * Responds with a list of bids and the IDs of the corresponding
     * reviewers. The requester must be a chair of the track that the paper is in.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<List<BidByReviewer>> getBidsForPaper(Long requesterID,
                                                               Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Gets the bid of a given reviewer for a given paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @param reviewerID the ID of the reviewer
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Bid> getBidForPaperByReviewer(Long requesterID,
                                                        Long paperID,
                                                        Long reviewerID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Saves the preference (based on expertise) of the requester in regards to reviewing the
     * given paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @param bid the bid of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Void> bid(Long requesterID,
                                    Long paperID,
                                    Bid bid) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
