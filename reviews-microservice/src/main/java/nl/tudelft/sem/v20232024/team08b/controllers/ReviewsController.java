package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.api.ReviewsAPI;
import nl.tudelft.sem.v20232024.team08b.application.ReviewsService;
import nl.tudelft.sem.v20232024.team08b.dtos.DiscussionComment;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ReviewsController implements ReviewsAPI {
    private final ReviewsService reviewsService;

    /**
     * Default constructor for the controller.
     *
     * @param reviewsService the respective service to inject
     */
    @Autowired
    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    /**
     * Gets a review of a paper from a given user.
     *
     * @param requesterID the ID of the requesting user
     * @param reviewerID the ID of the review
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Review> read(Long requesterID,
                                       Long reviewerID,
                                       Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Submits a review for a paper.
     *
     * @param review the review to be submitted
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Void> submit(Review review,
                                       Long requesterID,
                                       Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Gets the list of a reviewers for a given paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<List<Long>> getReviewers(Long requesterID,
                                                   Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Gets how far along a paper is in the review process.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<PaperPhase> getPhase(Long requesterID,
                                               Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Ends the discussion phase for a paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Void> finalization(Long requesterID,
                                             Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Posts a *discussion* comment.
     *
     * @param requesterID the ID of the requesting user
     * @param reviewerID the ID of the reviewer
     * @param paperID the ID of the paper
     * @param comment the comment to be posted
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Void> submitConfidentialComment(Long requesterID,
                                                          Long reviewerID,
                                                          Long paperID,
                                                          String comment) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Gets the discussion comments.
     *
     * @param requesterID the ID of the requesting user
     * @param reviewerID the ID of the reviewer
     * @param paperID the ID of the paper
     * @return the list of discussion comments
     */
    @Override
    public ResponseEntity<List<DiscussionComment>> getDiscussionComments(Long requesterID,
                                                                         Long reviewerID,
                                                                         Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
