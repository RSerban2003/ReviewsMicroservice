package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.PaperPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.application.verification.PapersVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.*;
import nl.tudelft.sem.v20232024.team08b.dtos.review.*;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReviewsService {
    private final ReviewRepository reviewRepository;
    private final PaperPhaseCalculator paperPhaseCalculator;
    private final PapersVerification papersVerification;
    private final TracksVerification tracksVerification;
    private final UsersVerification usersVerification;

    private final PaperRepository paperRepository;
    /**
     * Default constructor for the service.
     *
     * @param reviewRepository repository storing the reviews
     * @param tracksVerification object responsible for verifying track information
     * @param usersVerification object responsible for verifying user information
     * */
    @Autowired
    public ReviewsService(ReviewRepository reviewRepository,
                          PaperPhaseCalculator paperPhaseCalculator,
                          PapersVerification papersVerification,
                          TracksVerification tracksVerification,
                          UsersVerification usersVerification,
                          PaperRepository paperRepository) {
        this.reviewRepository = reviewRepository;
        this.paperPhaseCalculator = paperPhaseCalculator;
        this.papersVerification = papersVerification;
        this.tracksVerification = tracksVerification;
        this.usersVerification = usersVerification;
        this.paperRepository = paperRepository;
    }

    /**
     * Verifies if:
     *  - given user exists
     *  - given paper exists
     *  - the user is a reviewer of that paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper that is accessed
     * @throws NotFoundException if such paper is not found
     * @throws IllegalAccessException if the user is not allowed to access the paper
     */
    private void verifyIfReviewerCanSubmitReview(Long requesterID,
                                                Long paperID) throws NotFoundException,
                                                                     IllegalAccessException {
        // Check if such paper exists
        if (!papersVerification.verifyPaper(paperID)) {
            throw new NotFoundException("No such paper exists");
        }

        // Check if such user exists and has correct privileges
        if (!usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)) {
            throw new IllegalAccessException("No such user exists");
        }

        // Check if the user is allowed to review this paper
        if (!usersVerification.isReviewerForPaper(requesterID, paperID)) {
            throw new IllegalAccessException("The user is not a reviewer for this paper.");
        }

        // Verify that the current phase is the submitting phase
        tracksVerification.verifyTrackPhaseThePaperIsIn(paperID,
                List.of(TrackPhase.SUBMITTING)
        );
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
        // Phase checking is done inside verifyIf...() method
        verifyIfReviewerCanSubmitReview(requesterID, paperID);

        ReviewID reviewId = new ReviewID(paperID, requesterID);
        Review review = new Review(reviewDTO, reviewId);
        reviewRepository.save(review);
    }

    /**
     * Returns a review from the repository.
     *
     * @param reviewerID the ID of the reviewer
     * @param paperID the ID of the paper
     * @return the review
     */
    private Review getReview(Long reviewerID, Long paperID) throws NotFoundException {

        ReviewID reviewId = new ReviewID(paperID, reviewerID);
        Optional<Review> optional = reviewRepository.findById(reviewId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new NotFoundException("The review could not be found");
        }
    }

    /**
     * This method is designed to retrieve a list of reviewer IDs associated with a given paper.
     * It is intended for use by users who are either chairs or reviewers of the paper in question.
     *
     * @param requesterID the ID of the user making the request
     * @param paperID the ID of the paper for which reviewers are being requested
     * @return The method returns a list of Long values, each representing the ID of a reviewer associated with the paper.
     * @throws NotFoundException if the paper is not found
     * @throws IllegalAccessException if the requester is neither a chair nor a reviewer of the paper
     */
    public List<Long> getReviewersFromPaper(long requesterID, long paperID)
            throws NotFoundException, IllegalAccessException {
        boolean isChair = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
        boolean isReviewer = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER);
        if (!isChair && !isReviewer) {
            throw new IllegalAccessException("Not a chair or reviewer of paper");
        }
        tracksVerification.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.REVIEWING, TrackPhase.FINAL));
        List<Review> reviews = reviewRepository.findByReviewIDPaperID(paperID);
        List<Long> reviewers = new ArrayList<>();
        for (Review review : reviews) {
            reviewers.add(review.getReviewID().getReviewerID());
        }
        return reviewers;
    }

    /**
     * Verifies if a user can access a review. The user either has to be a chair
     * of the track of the review, or the reviewer himself.
     *
     * @param requesterID the ID of the requesting user.
     * @param reviewerID the ID of the reviewer who wrote that review.
     * @param paperID the ID of the paper reviewer.
     * @throws NotFoundException if requested review does not exist.
     * @throws IllegalAccessException if the requester is not allowed to access the review.
     */
    public void verifyIfUserCanAccessReview(Long requesterID,
                                           Long reviewerID,
                                           Long paperID) throws NotFoundException, IllegalAccessException {
        if (!papersVerification.verifyPaper(paperID)) {
            throw new NotFoundException("No such paper exists");
        }

        boolean isReviewer = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER);
        boolean isChair = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
        boolean isAuthor = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.AUTHOR);

        // Check if such review even exists (the method throws if it doesn't)
        getReview(reviewerID, paperID);

        if (isAuthor) {
            // If the requesting user is author, then he can only access the paper during final phase
            tracksVerification.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.FINAL));
        } else if (isChair || isReviewer) {
            // If the requesting user is chair or reviewer, they cannot access the review before the
            // reviewing phase
            tracksVerification.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.REVIEWING, TrackPhase.FINAL));
        } else {
            throw new IllegalAccessException("The requester is not a member of the track");
        }
    }

    /**
     * Gets a review from the local repository. Also performs all checks: checks if the
     * user is allowed to access the review, if such review exists, etc.
     *
     * @param requesterID ID of requesting user
     * @param reviewerID ID of the reviewer
     * @param paperID ID of the reviewed paper
     * @return review DTO object to return
     * @throws NotFoundException if such review was not found
     * @throws IllegalAccessException if the requester is not allowed to access the paper
     * @throws IllegalCallerException if such requester does not exist
     */
    public nl.tudelft.sem.v20232024.team08b.dtos.review.Review getReview(Long requesterID,
                                                                         Long reviewerID,
                                                                         Long paperID) throws NotFoundException,
                                                                                              IllegalAccessException,
                                                                                              IllegalCallerException {
        // Verify if the requesting user is allowed to access and if the given review exists.
        // Also performs elaborate phase checking, depending on requesting user
        verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);

        // Get the review from local repository.
        // TODO: make sure that if the requesting user is author, the confidential comment is stripped.
        Review review = getReview(reviewerID, paperID);

        // Map the review from local domain object to a DTO and return it
        return new nl.tudelft.sem.v20232024.team08b.dtos.review.Review(review);
    }

    /**
     * Finalizes the discussion phase for a paper.
     *
      * @param requesterID ID of requesting user
     * @param paperID ID of the paper
     * @throws IllegalAccessException if requester is not allowed to access the paper
     * @throws IllegalStateException if paper is not in discussion phase or reviews are not all positive nor negative
     * @throws NotFoundException if such requester does not exist or if the paper is not found
     */
    public void finalizeDiscussionPhase(Long requesterID, Long paperID) throws IllegalAccessException,
                                                                                IllegalStateException,
                                                                                NotFoundException {
        //Check if requester is a valid chair of this track which contains this paper.
        if (!usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
            throw new IllegalAccessException("User is not a chair for this track!");
        }
        PaperPhase phase = paperPhaseCalculator.getPaperPhase(paperID);
        //Check if paper is in discussion phase.
        if (!Objects.equals(phase, PaperPhase.IN_DISCUSSION)) {
            throw new IllegalStateException("Paper is not in discussion phase!");
        }
        //Check if all reviews are either in positive or negative.
        //Reviews should not be empty because paper is already in discussion phase
        //which means that there are 3 reviews for the paper.
        List<Review> reviews = reviewRepository.findByReviewIDPaperID(paperID);
        boolean isAgreed = isAgreed(reviews);
        if (!isAgreed) {
            throw new IllegalStateException("Reviews are not all positive nor all negative.");
        }
        RecommendationScore recommendationScore = reviews.get(0).getRecommendationScore();
        Paper paper = getDomainPaper(paperID);
        if (recommendationScore == RecommendationScore.STRONG_REJECT ||
                recommendationScore == RecommendationScore.WEAK_REJECT) {
            paper.setStatus(PaperStatus.REJECTED);
        } else {
            paper.setStatus(PaperStatus.ACCEPTED);
        }
        paper.setReviewsHaveBeenFinalized(true);
        paperRepository.save(paper);
    }

    /**
     * Gets the paper object from paperRepository.
     *
     * @param paperID the ID of the paper
     * @return the paper object
     * @throws NotFoundException if such paper does not exist
     */
    public Paper getDomainPaper(Long paperID) throws NotFoundException {
        Optional<Paper> paper = paperRepository.findById(paperID);
        if (paper.isEmpty()) {
            throw new NotFoundException("Paper was not found");
        }
        return paper.get();
    }

    /**
     * Checks if all reviews have made a uniform decision or not.
     *
     * @param reviews the list of reviews to check
     * @return true if all reviews are either agreed or disagreed. False otherwise.
     */
    public static boolean isAgreed(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            throw new IllegalStateException("No reviews found.");
        }

        boolean isAccept = reviews.get(0).getRecommendationScore() == RecommendationScore.STRONG_ACCEPT
                | reviews.get(0).getRecommendationScore() == RecommendationScore.WEAK_ACCEPT;

        for (int i = 1; i < reviews.size(); i++) { // Start from the second review
            RecommendationScore score = reviews.get(i).getRecommendationScore();
            boolean currentIsAccept = score == RecommendationScore.STRONG_ACCEPT
                    | score == RecommendationScore.WEAK_ACCEPT;

            if (currentIsAccept != isAccept) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifies if the user has permission to submit a confidential comment on a review.
     *
     * @param requesterID The ID of the user submitting the comment
     * @param reviewerID The ID of the reviewer associated with the paper
     * @param paperID The ID of the paper being reviewed
     * @throws NotFoundException if the paper does not exist
     * @throws IllegalAccessException if the user does not have the required permissions
     */
    public void verifySubmitDiscussionComment(Long requesterID,
                                              Long reviewerID,
                                              Long paperID) throws NotFoundException,
                                                                 IllegalAccessException {
        boolean isReviewer = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER);
        boolean isAssignedToPaper = usersVerification.isReviewerForPaper(reviewerID, paperID);

        if (!papersVerification.verifyPaper(paperID)) {
            throw new NotFoundException("Paper does not exist");
        }

        if (!isReviewer || !isAssignedToPaper) {
            throw new IllegalAccessException("The user does not have permission to submit this comment");
        }

        papersVerification.verifyPhasePaperIsIn(paperID, PaperPhase.IN_DISCUSSION);

    }

    /**
     * Submits a confidential comment to a review.
     *
     * @param requesterID The ID of the user submitting the comment
     * @param reviewerID The ID of the reviewer associated with the paper
     * @param paperID The ID of the paper being commented on
     * @param text The confidential comment text
     * @throws NotFoundException if the paper does not exist
     * @throws IllegalAccessException if the user does not have the required permissions
     */
    public void submitDiscussionComment(Long requesterID,
                                        Long reviewerID,
                                        Long paperID,
                                        String text) throws NotFoundException,
                                                              IllegalAccessException {
        verifySubmitDiscussionComment(requesterID, reviewerID, paperID);

        Comment comment = new Comment(requesterID, text);

        Review review = getReview(reviewerID, paperID);
        review.getConfidentialComments().add(comment);
        reviewRepository.save(review);
    }

    /**
     * Verifies if the user has the permission to view discussion comments on a review.
     *
     * @param requesterID The ID of the user requesting to view the comments
     * @param reviewerID The ID of the reviewer associated with the paper
     * @param paperID The ID of the paper
     * @throws NotFoundException if the paper does not exist
     * @throws IllegalAccessException if the user does not have the required permissions
     */
    public void verifyGetDiscussionComments(Long requesterID,
                                               Long reviewerID,
                                               Long paperID) throws NotFoundException,
                                                                    IllegalAccessException {
        boolean isChair = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
        boolean isReviewer = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER);
        boolean isAssignedToPaper = usersVerification.isReviewerForPaper(reviewerID, paperID);

        if (!papersVerification.verifyPaper(paperID)) {
            throw new NotFoundException("Paper does not exist");
        }

        if (!isChair && !(isReviewer && isAssignedToPaper)) {
            throw new IllegalAccessException("The user does not have permission to view these comments");
        }

        papersVerification.verifyPhasePaperIsIn(paperID, PaperPhase.IN_DISCUSSION);
    }

    /**
     * Retrieves discussion comments for a specific paper.
     *
     * @param requesterID The ID of the user requesting the comments
     * @param reviewerID The ID of the reviewer associated with the paper
     * @param paperID The ID of the paper whose comments are being retrieved
     * @throws NotFoundException if the review or paper does not exist
     * @throws IllegalAccessException if the user does not have the necessary permissions
     */
    public List<DiscussionComment> getDiscussionComments(Long requesterID,
                                                         Long reviewerID,
                                                         Long paperID) throws NotFoundException,
                                                                              IllegalAccessException {
        verifyGetDiscussionComments(requesterID, reviewerID, paperID);

        Review review = getReview(reviewerID, paperID);
        //Retrieve the list of Comments assigned to the Review
        List<Comment> comments = review.getConfidentialComments();
        //Parse the list of Comments into a list of DiscussionComments
        List<DiscussionComment> discussionComments = new ArrayList<>();
        for (Comment comment : comments) {
            discussionComments.add(new DiscussionComment(comment));
        }
        return discussionComments;
    }
}
