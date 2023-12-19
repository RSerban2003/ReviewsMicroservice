package nl.tudelft.sem.v20232024.team08b.application;

import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.repos.CommentRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewsService {
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final PaperRepository paperRepository;
    private final VerificationService verificationService;
    private final ExternalRepository externalRepository;

    /**
     * Default constructor for the service.
     *
     * @param reviewRepository repository storing the reviews
     * @param commentRepository repository storing the comments
     * @param paperRepository repository storing the papers
     * @param verificationService service that handles authentication
     * @param externalRepository  repository storing everything outside of
     *                            this microservice
     */
    @Autowired
    public ReviewsService(ReviewRepository reviewRepository,
                          CommentRepository commentRepository,
                          PaperRepository paperRepository,
                          VerificationService verificationService,
                          ExternalRepository externalRepository) {
        this.reviewRepository = reviewRepository;
        this.commentRepository = commentRepository;
        this.paperRepository = paperRepository;
        this.verificationService = verificationService;
        this.externalRepository = externalRepository;
    }

    /**
     * Checks if a user is a reviewer of a paper.
     * TODO: make sure that when a user is assigned to review some paper, an empty
     *       review is created with that user and the paper ID.
     *
     * @param reviewerID the ID of the user
     * @param paperID the ID of the paper
     * @return true, iff the user is a reviewer for the paper
     */
    private boolean isReviewerForPaper(Long reviewerID, Long paperID) {
        return reviewRepository.findById(new ReviewID(reviewerID, paperID)).isPresent();
    }

    /**
     * Adds or updates a review by a user for a specific paper.
     *
     * @param reviewDTO the review object that was given by the user
     * @param requesterID the ID of the submitter
     * @param paperID the ID of the for which the review is submitted
     */
    public void submitReview(nl.tudelft.sem.v20232024.team08b.dtos.review.Review reviewDTO,
                             Long requesterID,
                             Long paperID) throws Exception {

        // Check if such paper exists
        if (!verificationService.verifyPaper(paperID)) {
            throw new IllegalArgumentException("No such paper exists");
        }

        // Check if such user exists and has correct privileges
        Long containingTrackID = externalRepository.getSubmission(paperID).getTrackId();
        Long containingConferenceID = externalRepository.getSubmission(paperID).getEventId();
        if (!verificationService.verifyUser(requesterID, containingConferenceID,
                                            containingTrackID, UserRole.REVIEWER)) {
            throw new IllegalCallerException("No such user exists");
        }

        // Check if the user is allowed to review this paper
        if (!isReviewerForPaper(requesterID, paperID)) {
            throw new IllegalAccessException("The user is not a reviewer for this paper.");
        }

        ReviewID reviewId = new ReviewID(paperID, requesterID);
        Review review = new Review(reviewDTO, reviewId);
        reviewRepository.save(review);
    }
}
