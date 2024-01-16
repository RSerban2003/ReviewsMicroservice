package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.ReviewsService;
import nl.tudelft.sem.v20232024.team08b.application.phase.PaperPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.application.verification.PapersVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.domain.*;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperStatus;
import nl.tudelft.sem.v20232024.team08b.dtos.review.DiscussionComment;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewsServiceTests {
    private final ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    @MockBean
    private final PaperRepository paperRepository = Mockito.mock(PaperRepository.class);
    @MockBean
    private final PapersVerification papersVerification = Mockito.mock(PapersVerification.class);
    @MockBean
    private final TracksVerification tracksVerification = Mockito.mock(TracksVerification.class);
    @MockBean
    private final UsersVerification usersVerification = Mockito.mock(UsersVerification.class);
    @MockBean
    private final ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);
    @MockBean
    private final PaperPhaseCalculator paperPhaseCalculator = Mockito.mock(PaperPhaseCalculator.class);
    private ReviewsService reviewsService = new ReviewsService(
                reviewRepository,
                paperPhaseCalculator,
            papersVerification,
            tracksVerification,
            usersVerification,
            paperRepository
        );

    private nl.tudelft.sem.v20232024.team08b.dtos.review.Review reviewDTO;
    private Submission fakeSubmission;
    private Review fakeReview;
    private final Long requesterID = 0L;
    private final Long reviewerID = 3L;
    private final Long paperID = 4L;
    @BeforeEach
    void prepare() {
        reviewDTO = new nl.tudelft.sem.v20232024.team08b.dtos.review.Review(
                ConfidenceScore.BASIC,
                "Comment for author",
                "Confidential comment",
                RecommendationScore.WEAK_REJECT
        );

        fakeReview = new Review();
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(2L, "comment"));
        comments.add(new Comment(3L, "comment"));
        fakeReview.setConfidentialComments(comments);

        fakeSubmission = new Submission();
        fakeSubmission.setTrackId(2L);
        fakeSubmission.setEventId(1L);
    }

    @Test
    void submitReview_NoSuchPaper() {
        when(papersVerification.verifyPaper(paperID)).thenReturn(false);
        Assert.assertThrows(NotFoundException.class, () ->
                reviewsService.submitReview(reviewDTO, requesterID, paperID));
    }

    @Test
    void testAllReviewsAgreePositively() {
        List<Review> reviews = List.of(
                new Review(null, null, null, RecommendationScore.STRONG_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.WEAK_ACCEPT, null, null)
        );
        assertTrue(ReviewsService.isAgreed(reviews));
    }

    @Test
    void testIsAgreedWithFirstReviewWeakAccept() {
        List<Review> reviews = List.of(
                new Review(null, null, null, RecommendationScore.WEAK_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.WEAK_ACCEPT, null, null)
        );
        assertTrue(ReviewsService.isAgreed(reviews));
    }

    @Test
    void testIsAgreedWithFirstReviewWeakAcceptAndDisagreement() {
        List<Review> reviews = List.of(
                new Review(null, null, null, RecommendationScore.WEAK_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_REJECT, null, null)
        );
        assertFalse(ReviewsService.isAgreed(reviews));
    }

    @Test
    void testAllReviewsAgreeNegatively() {
        List<Review> reviews = List.of(
                new Review(null, null, null, RecommendationScore.STRONG_REJECT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_REJECT, null, null),
                new Review(null, null, null, RecommendationScore.WEAK_REJECT, null, null)
        );
        assertTrue(ReviewsService.isAgreed(reviews));
    }

    @Test
    void testMixedReviews() {
        List<Review> reviews = List.of(
                new Review(null, null, null, RecommendationScore.STRONG_REJECT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.WEAK_REJECT, null, null)
        );
        assertFalse(ReviewsService.isAgreed(reviews));
    }

    @Test
    void testEmptyListOfReviews() {
        List<Review> reviews = Collections.emptyList();
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            ReviewsService.isAgreed(reviews);
        });
        Assertions.assertEquals("No reviews found.", exception.getMessage());
    }

    @Test
    public void finalizeDiscussion_PaperNotFound() throws Exception {
        when(paperPhaseCalculator.getPaperPhase(paperID)).thenThrow(new NotFoundException("No such paper found"));
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        assertThrows(NotFoundException.class, () -> reviewsService.finalizeDiscussionPhase(requesterID, paperID));
    }

    @Test
    public void finalizeDiscussion_InvalidRequester() throws Exception {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);
        assertThrows(IllegalAccessException.class, () -> reviewsService.finalizeDiscussionPhase(requesterID, paperID));
    }

    @Test
    public void finalizeDiscussion_InvalidPaperPhase() throws Exception {
        when(paperPhaseCalculator.getPaperPhase(paperID)).thenReturn(PaperPhase.IN_REVIEW);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> reviewsService.finalizeDiscussionPhase(requesterID, paperID));
        verify(reviewRepository, never()).findByReviewIDPaperID(paperID);
    }

    @Test
    public void finalizeDiscussion_MixedReviews() throws Exception {
        List<Review> reviews = List.of(
                new Review(null, null, null, RecommendationScore.STRONG_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_REJECT, null, null),
                new Review(null, null, null, RecommendationScore.WEAK_ACCEPT, null, null)
        );
        when(paperPhaseCalculator.getPaperPhase(paperID)).thenReturn(PaperPhase.IN_DISCUSSION);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(reviews);
        Exception e = assertThrows(IllegalStateException.class, () ->
                reviewsService.finalizeDiscussionPhase(requesterID, paperID));
        assertEquals("Reviews are not all positive nor all negative.", e.getMessage());
    }

    @Test
    public void finalizeDiscussion_NullReviews() throws Exception {
        when(paperPhaseCalculator.getPaperPhase(paperID)).thenReturn(PaperPhase.IN_DISCUSSION);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> reviewsService.finalizeDiscussionPhase(requesterID, paperID));
    }

    @Test
    public void finalizeDiscussionSuccessful_AllAccept() throws Exception {
        List<Review> reviews = List.of(
                new Review(null, null, null, RecommendationScore.STRONG_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.WEAK_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_ACCEPT, null, null)
        );
        Paper paper = new Paper(paperID, null, PaperStatus.NOT_DECIDED, false);
        Optional<Paper> optional = Optional.of(paper);
        when(paperPhaseCalculator.getPaperPhase(paperID)).thenReturn(PaperPhase.IN_DISCUSSION);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(reviews);
        when(paperRepository.findById(paperID)).thenReturn(optional);
        reviewsService.finalizeDiscussionPhase(requesterID, paperID);
        assertTrue(paper.getReviewsHaveBeenFinalized());
        verify(paperRepository, times(1)).save(paper);
    }

    @Test
    void testGetDomainPaperSuccess() throws NotFoundException, IllegalAccessException {
        Paper expectedPaper = new Paper();
        when(paperRepository.findById(paperID)).thenReturn(Optional.of(expectedPaper));

        Paper paper = reviewsService.getDomainPaper(paperID);

        Assertions.assertNotNull(paper);
        Assertions.assertEquals(expectedPaper, paper);
    }

    @Test
    void testGetDomainPaperNotFoundException() throws NotFoundException, IllegalAccessException {
        when(paperRepository.findById(paperID)).thenReturn(Optional.empty());

        Exception e = assertThrows(NotFoundException.class, () -> {
            reviewsService.getDomainPaper(paperID);
        });
        assertEquals("Paper was not found", e.getMessage());
    }

    @Test
    void testFinalizeDiscussionPhaseRejectedStrongRejects()
            throws IllegalAccessException, NotFoundException, IllegalStateException {
        Paper paper = new Paper();
        paper.setId(paperID);
        List<Review> reviews = List.of(
                new Review(null, null, null, RecommendationScore.STRONG_REJECT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_REJECT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_REJECT, null, null)
        );

        when(paperPhaseCalculator.getPaperPhase(paperID)).thenReturn(PaperPhase.IN_DISCUSSION);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(reviews);
        when(paperRepository.findById(paperID)).thenReturn(Optional.of(paper));

        reviewsService.finalizeDiscussionPhase(requesterID, paperID);
        Assertions.assertEquals(PaperStatus.REJECTED, paper.getStatus());
        assertTrue(paper.getReviewsHaveBeenFinalized());
        verify(paperRepository, times(1)).save(paper);
    }

    @Test
    void testFinalizeDiscussionPhaseRejectedWeakReject() throws Exception {
        Paper paper = new Paper();
        paper.setId(paperID);
        List<Review> reviews = List.of(
                new Review(null, null, null, RecommendationScore.WEAK_REJECT, null, null),
                new Review(null, null, null, RecommendationScore.WEAK_REJECT, null, null),
                new Review(null, null, null, RecommendationScore.WEAK_REJECT, null, null)
        );

        when(paperPhaseCalculator.getPaperPhase(paperID)).thenReturn(PaperPhase.IN_DISCUSSION);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(reviews);
        when(paperRepository.findById(paperID)).thenReturn(Optional.of(paper));

        reviewsService.finalizeDiscussionPhase(requesterID, paperID);
        Assertions.assertEquals(PaperStatus.REJECTED, paper.getStatus());
        assertTrue(paper.getReviewsHaveBeenFinalized());
        verify(paperRepository, times(1)).save(paper);
    }

    @Test
    void testFinalizeDiscussionPhaseAccepted() throws IllegalAccessException, NotFoundException, IllegalStateException {
        Paper paper = new Paper();
        paper.setId(paperID);

        List<Review> reviews = List.of(
                new Review(null, null, null, RecommendationScore.STRONG_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.STRONG_ACCEPT, null, null),
                new Review(null, null, null, RecommendationScore.WEAK_ACCEPT, null, null)
        );

        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(paperPhaseCalculator.getPaperPhase(paperID)).thenReturn(PaperPhase.IN_DISCUSSION);
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(reviews);
        when(paperRepository.findById(paperID)).thenReturn(Optional.of(paper));

        reviewsService.finalizeDiscussionPhase(requesterID, paperID);

        assertEquals(PaperStatus.ACCEPTED, paper.getStatus());
    }

    @Test
    void submitReview_NoSuchUser() throws NotFoundException {
        // Assume first IF works
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        // Essentially, fake the track, that the submission belongs to
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);

        // Assume the second IF does not work
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(false);

        Assert.assertThrows(IllegalAccessException.class, () ->
                reviewsService.submitReview(reviewDTO, requesterID, paperID));
    }

    @Test
    void submitReview_NotAReviewer() throws NotFoundException {
        // Assume first IF works
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        // Essentially, fake the track, that the submission belongs to
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);

        // Assume the second IF works
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);

        // Assume that the user is not a reviewer - i.e., the third IF does not work
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);

        Assert.assertThrows(IllegalAccessException.class, () ->
                reviewsService.submitReview(reviewDTO, requesterID, paperID));
    }

    @Test
    void submitReview_WrongPhase() throws NotFoundException, IllegalAccessException {
        // Assume first IF works
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        // Essentially, fake the track, that the submission belongs to
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);

        // Assume the second IF works
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);

        // Assume that the user a reviewer
        when(usersVerification.isReviewerForPaper(requesterID, paperID)).thenReturn(true);

        // Assume the current phase is not the submitting phase
        doThrow(new IllegalAccessException("")).when(tracksVerification).verifyTrackPhaseThePaperIsIn(
                paperID,
                List.of(TrackPhase.SUBMITTING)
        );
        Assert.assertThrows(IllegalAccessException.class, () ->
                reviewsService.submitReview(reviewDTO, requesterID, paperID));
    }

    @Test
    void submitReview_Successful() throws Exception {
        // Assume first IF works
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        // Essentially, fake the track, that the submission belongs to
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);

        // Assume the second IF works
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);

        // Assume that the third if works
        when(usersVerification.isReviewerForPaper(requesterID, paperID)).thenReturn(true);

        // Assert that a correct review is added to the repository
        Review expected = new Review(reviewDTO, new ReviewID(paperID, requesterID));
        reviewsService.submitReview(reviewDTO, requesterID, paperID);
        verify(reviewRepository).save(expected);
    }

    @Test
    void verifyIfUserCanAccessReview_NoSuchPaper() {
        // Assume paper does not exist
        when(papersVerification.verifyPaper(paperID)).thenReturn(false);

        Assert.assertThrows(NotFoundException.class, () ->
                reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID));
    }

    @Test
    void verifyIfUserCanAccessReview_NoSuchUserInTheTrack() {
        // Assume paper exists
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        // Assume user is not a reviewer
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(false);
        // Assume user is not a chair either
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(false);
        // Assume user is not an author
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(false);

        // Assume the review does exist
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(new Review()));


        Assert.assertThrows(IllegalAccessException.class, () ->
                reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID));
    }

    @Test
    void verifyIfUserCanAccessReview_NoSuchReview() {
        // Assume paper exists
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        // Assume is reviewer
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);
        // Assume is not chair
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(false);
        // Assume user is not an author
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.AUTHOR))
                .thenReturn(false);
        // Assume the review doesn't exist
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.empty());


        Assert.assertThrows(NotFoundException.class, () ->
                reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID));
    }

    @Test
    void verifyIfUserCanAccessReview_SuccessfulChair() throws NotFoundException, IllegalAccessException {
        // Assume paper exists
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        // Assume user is not a reviewer
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(false);
        // Assume user is a chair
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(true);
        // Assume user is not an author
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.AUTHOR))
                .thenReturn(false);
        // Assume the review does exist
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(new Review()));

        reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);

        // Verify that the method checks for phase
        verify(tracksVerification).verifyTrackPhaseThePaperIsIn(paperID,
                List.of(TrackPhase.REVIEWING, TrackPhase.FINAL));
    }

    @Test
    void verifyIfUserCanAccessReview_SuccessfulReviewer() throws NotFoundException, IllegalAccessException {
        // Assume paper exists
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        // Assume user is a reviewer
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);
        // Assume user is not a chair
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(false);
        // Assume user is not an author
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.AUTHOR))
                .thenReturn(false);

        // Assume the review does exist
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(new Review()));

        reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);

        // Verify that the method checks for phase
        verify(tracksVerification).verifyTrackPhaseThePaperIsIn(paperID,
                List.of(TrackPhase.REVIEWING, TrackPhase.FINAL));

    }

    @Test
    void verifyIfUserCanAccessReview_SuccessfulAuthor() throws NotFoundException, IllegalAccessException {
        // Assume paper exists
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        // Assume user is not reviewer
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(false);
        // Assume user is not a chair
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(false);
        // Assume user is an author
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.AUTHOR))
                .thenReturn(true);
        // Assume the review does exist
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(new Review()));

        reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);

        // Verify that the method checks for phase
        verify(tracksVerification).verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.FINAL));
    }


    @Test
    void getReview_Successful() throws NotFoundException, IllegalAccessException {
        // We are going to mock the "verifyIfUserCanAccessReview" method
        reviewsService = Mockito.spy(reviewsService);

        doNothing().when(reviewsService).verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(fakeReview));
        when(usersVerification.isAuthorToPaper(requesterID, paperID)).thenReturn(false);
        nl.tudelft.sem.v20232024.team08b.dtos.review.Review expectedDTO =
                new nl.tudelft.sem.v20232024.team08b.dtos.review.Review(fakeReview);
        assertThat(reviewsService.getReview(requesterID, reviewerID, paperID)).isEqualTo(expectedDTO);
    }

    @Test
    void getReview_NoSuchID() throws NotFoundException, IllegalAccessException {
        // We are going to mock the "verifyIfUserCanAccessReview" method
        reviewsService = Mockito.spy(reviewsService);

        doNothing().when(reviewsService).verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.empty());
        when(usersVerification.isAuthorToPaper(requesterID, paperID)).thenReturn(false);
        assertThrows(NotFoundException.class, () ->
                reviewsService.getReview(requesterID, reviewerID, paperID));
    }

    @Test
    void getReview_UnauthorizedAccess() throws NotFoundException, IllegalAccessException {
        // We are going to mock the "verifyIfUserCanAccessReview" method
        reviewsService = Mockito.spy(reviewsService);

        // Assume the review was not found
        doThrow(new NotFoundException("")).when(reviewsService)
                .verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(fakeReview));
        when(usersVerification.isAuthorToPaper(requesterID, paperID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> reviewsService.getReview(requesterID, reviewerID, paperID));
    }

    @Test
    void getReview_SuccessfulForAuthor() throws NotFoundException, IllegalAccessException {
        // We are going to mock the "verifyIfUserCanAccessReview" method
        reviewsService = Mockito.spy(reviewsService);

        doNothing().when(reviewsService).verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(fakeReview));
        when(usersVerification.isAuthorToPaper(requesterID, paperID)).thenReturn(true);
        nl.tudelft.sem.v20232024.team08b.dtos.review.Review review =
                reviewsService.getReview(requesterID, reviewerID, paperID);
        assertNull(review.getConfidentialComment());
    }

    @Test
    void verifySubmitConfidentialComment_NotFoundException() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                reviewsService.verifySubmitDiscussionComment(requesterID, reviewerID, paperID));
    }

    @Test
    void verifySubmitConfidentialComment_IllegalAccessException_NotReviewerNotAssigned() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        assertThrows(IllegalAccessException.class, () ->
                reviewsService.verifySubmitDiscussionComment(requesterID, reviewerID, paperID));
    }

    @Test
    void verifySubmitConfidentialComment_IllegalAccessException_NotReviewer() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        assertThrows(IllegalAccessException.class, () ->
                reviewsService.verifySubmitDiscussionComment(requesterID, reviewerID, paperID));
    }

    @Test
    void verifySubmitConfidentialComment_IllegalAccessException_NotAssigned() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        assertThrows(IllegalAccessException.class, () ->
                reviewsService.verifySubmitDiscussionComment(requesterID, reviewerID, paperID));
    }

    @Test
    void verifySubmitConfidentialComment_Successful() throws NotFoundException, IllegalAccessException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        reviewsService.verifySubmitDiscussionComment(requesterID, reviewerID, paperID);
    }

    @Test
    void submitConfidentialComment_NotFoundException() throws NotFoundException, IllegalAccessException {
        reviewsService = Mockito.spy(reviewsService);

        doThrow(new NotFoundException(""))
                .when(reviewsService).verifySubmitDiscussionComment(requesterID, reviewerID, paperID);

        assertThrows(NotFoundException.class, () ->
                reviewsService.submitDiscussionComment(requesterID, reviewerID, paperID, "text"));
    }

    @Test
    void submitConfidentialComment_IllegalAccessException() throws NotFoundException, IllegalAccessException {
        reviewsService = Mockito.spy(reviewsService);

        doThrow(new IllegalAccessException(""))
                .when(reviewsService).verifySubmitDiscussionComment(requesterID, reviewerID, paperID);

        assertThrows(IllegalAccessException.class, () ->
                reviewsService.submitDiscussionComment(requesterID, reviewerID, paperID, "text"));
    }

    @Test
    void submitConfidentialComment_Successful() throws NotFoundException, IllegalAccessException {

        reviewsService = spy(reviewsService);

        doNothing().when(reviewsService).verifySubmitDiscussionComment(requesterID, reviewerID, paperID);
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(fakeReview));

        reviewsService.submitDiscussionComment(requesterID, reviewerID, paperID, "text");
        Comment comment = new Comment(requesterID, "text");
        assertThat(fakeReview.getConfidentialComments().contains(comment));
        verify(reviewRepository).save(fakeReview);

    }

    @Test
    void verifyGetDiscussionComments_NotFoundException() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                reviewsService.verifyGetDiscussionComments(requesterID, reviewerID, paperID));
    }

    @Test
    void verifyGetDiscussionComments_IllegalAccessException() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        assertThrows(IllegalAccessException.class, () ->
                reviewsService.verifyGetDiscussionComments(requesterID, reviewerID, paperID));
    }

    @Test
    void verifyGetDiscussionComments_IllegalAccessException_NotChairNotAssigned() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        assertThrows(IllegalAccessException.class, () ->
                reviewsService.verifyGetDiscussionComments(requesterID, reviewerID, paperID));
    }

    @Test
    void verifyGetDiscussionComments_IllegalAccessException_NotChairNotReviewer() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        assertThrows(IllegalAccessException.class, () ->
                reviewsService.verifyGetDiscussionComments(requesterID, reviewerID, paperID));
    }

    @Test
    void verifyGetDiscussionComments_Successful_isReviewer() throws NotFoundException, IllegalAccessException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        reviewsService.verifyGetDiscussionComments(requesterID, reviewerID, paperID);
    }

    @Test
    void verifyGetDiscussionComments_Successful_isChair() throws NotFoundException, IllegalAccessException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        reviewsService.verifyGetDiscussionComments(requesterID, reviewerID, paperID);
    }

    @Test
    void verifyGetDiscussionComments_Successful_isChairAndReviewer() throws NotFoundException, IllegalAccessException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        reviewsService.verifyGetDiscussionComments(requesterID, reviewerID, paperID);
    }

    @Test
    void verifyGetDiscussionComments_Successful_isChairAndAssigned() throws NotFoundException, IllegalAccessException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        reviewsService.verifyGetDiscussionComments(requesterID, reviewerID, paperID);
    }

    @Test
    void verifyGetDiscussionComments_Successful() throws NotFoundException, IllegalAccessException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        reviewsService.verifyGetDiscussionComments(requesterID, reviewerID, paperID);
    }

    @Test
    void getDiscussionComments_NotFoundException() throws NotFoundException, IllegalAccessException {
        reviewsService = Mockito.spy(reviewsService);

        doThrow(new NotFoundException(""))
                .when(reviewsService).verifyGetDiscussionComments(requesterID, reviewerID, paperID);

        assertThrows(NotFoundException.class, () -> reviewsService.getDiscussionComments(requesterID, reviewerID, paperID));
    }

    @Test
    void getDiscussionComments_IllegalAccessException() throws NotFoundException, IllegalAccessException {
        reviewsService = Mockito.spy(reviewsService);

        doThrow(new IllegalAccessException(""))
                .when(reviewsService).verifyGetDiscussionComments(requesterID, reviewerID, paperID);

        assertThrows(IllegalAccessException.class, () ->
                reviewsService.getDiscussionComments(requesterID, reviewerID, paperID));
    }

    @Test
    void getDiscussionComments_Successful() throws NotFoundException, IllegalAccessException {
        List<DiscussionComment> expectedComments = new ArrayList<>();
        expectedComments.add(new DiscussionComment(2L, "comment"));
        expectedComments.add(new DiscussionComment(3L, "comment"));

        reviewsService = spy(reviewsService);

        doNothing().when(reviewsService).verifyGetDiscussionComments(requesterID, reviewerID, paperID);
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(fakeReview));

        List<DiscussionComment> actualComments = reviewsService.getDiscussionComments(requesterID, reviewerID, paperID);

        assertThat(actualComments)
                .isEqualToComparingFieldByFieldRecursively(expectedComments);
    }

    @Test
    void testGetReviewersFromPaperSuccessChair() throws NotFoundException, IllegalAccessException {
        ReviewID id1 = new ReviewID(paperID, 1L);
        ReviewID id2 = new ReviewID(paperID, 2L);
        List<Review> mockReviews = List.of(
                new Review(id1, null, null, null, null, null),
                new Review(id2, null, null, null, null, null)
        );

        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(mockReviews);

        List<Long> reviewers = reviewsService.getReviewersFromPaper(requesterID, paperID);
        Assertions.assertEquals(List.of(1L, 2L), reviewers);
    }

    @Test
    void testGetReviewersFromPaperSuccessReviewer() throws NotFoundException, IllegalAccessException {
        ReviewID id1 = new ReviewID(paperID, 1L);
        ReviewID id2 = new ReviewID(paperID, 2L);
        List<Review> mockReviews = List.of(
                new Review(id1, null, null, null, null, null),
                new Review(id2, null, null, null, null, null)
        );

        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(mockReviews);

        List<Long> reviewers = reviewsService.getReviewersFromPaper(requesterID, paperID);
        Assertions.assertEquals(List.of(1L, 2L), reviewers);
    }

    @Test
    void testGetReviewersFromPaperAccessException() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);

        Exception e = assertThrows(IllegalAccessException.class, () -> {
            reviewsService.getReviewersFromPaper(requesterID, paperID);
        });
        assertEquals("Not a chair or reviewer of paper", e.getMessage());
    }

    @Test
    void testGetReviewersFromPaperInvalidTrackPhase() throws NotFoundException, IllegalAccessException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        doThrow(new IllegalStateException()).when(tracksVerification)
                .verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.REVIEWING, TrackPhase.FINAL));

        assertThrows(IllegalStateException.class, () -> {
            reviewsService.getReviewersFromPaper(requesterID, paperID);
        });
    }
}