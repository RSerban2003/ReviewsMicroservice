package nl.tudelft.sem.v20232024.team08b.controllers;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.api.ReviewsAPI;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import nl.tudelft.sem.v20232024.team08b.application.ReviewsService;
import nl.tudelft.sem.v20232024.team08b.dtos.review.DiscussionComment;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewsController implements ReviewsAPI {
    private final ReviewsService reviewsService;
    private final PapersService papersService;

    /**
     * Default constructor for the controller.
     *
     * @param reviewsService the respective service to inject
     * @param papersService service responsible for papers
     */
    @Autowired
    public ReviewsController(ReviewsService reviewsService,
                             PapersService papersService) {
        this.reviewsService = reviewsService;
        this.papersService = papersService;
    }

    /**
     * Returns a review of a paper from a given user. If such review does not exist,
     * or if the accessing user is not present, returns a special status code. For detailed
     * error codes, check the API documentation in the ReviewsAPI interface.
     *
     * @param requesterID the ID of the requesting user
     * @param reviewerID the ID of the reviewer whose review is requested
     * @param paperID the ID of the paper
     * @return a response entity containing the review, or a special error code.
     */
    @Override
    public ResponseEntity<Review> read(Long requesterID,
                                       Long reviewerID,
                                       Long paperID) {
        try {
            Review review = reviewsService.getReview(requesterID, reviewerID, paperID);
            return ResponseEntity.ok(review);
        } catch (NotFoundException e) {
            // The requested paper or reviewer was not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            // The requester must be a reviewer assigned to the given paper
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            // Some other problems have occurred
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Submits a review for a paper. If the review of the paper was already submitted, then
     * updated that review.
     * It also takes into account edge cases, such as if the requester is a reviewer assigned
     * to the given paper. For detailed error codes, check the API documentation in the ReviewsAPI
     * interface.
     *
     * @param review the review to be submitted
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper that is being reviewed
     * @return returns an empty response entity with the status
     *         code indicating if the request was successful
     */
    @Override
    public ResponseEntity<Void> submit(Review review,
                                       Long requesterID,
                                       Long paperID) {
        try {
            reviewsService.submitReview(review, requesterID, paperID);
        } catch (IllegalCallerException | NotFoundException e) {
            // The requested paper or reviewer was not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            // The requester must be a reviewer assigned to the given paper
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            // Some other problems have occurred
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();
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
        try {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(papersService.getPaperPhase(requesterID, paperID));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
     * Posts a confidential comment for a review during the discussion phase.
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
        try {
            reviewsService.submitConfidentialComment(requesterID, reviewerID, paperID, comment);
        } catch (NotFoundException e) {
            // The paper or reviewer was not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            // The requester does not have the necessary permissions
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
     * Gets the confidential comments assigned to a review during the discussion phase.
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
        try {
            return ResponseEntity.ok(reviewsService.getDiscussionComments(requesterID, reviewerID, paperID));
        } catch (NotFoundException e) {
            // The paper or reviewer was not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            // The requester does not have the necessary permissions
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            // Internal server error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
