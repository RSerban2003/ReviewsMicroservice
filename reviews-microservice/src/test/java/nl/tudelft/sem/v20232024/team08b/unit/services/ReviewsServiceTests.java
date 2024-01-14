package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.ReviewsService;
import nl.tudelft.sem.v20232024.team08b.application.verification.PapersVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.domain.ConfidenceScore;
import nl.tudelft.sem.v20232024.team08b.domain.RecommendationScore;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ReviewsServiceTests {
    private final ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    private final PapersVerification papersVerification = Mockito.mock(PapersVerification.class);
    private final TracksVerification tracksVerification = Mockito.mock(TracksVerification.class);
    private final UsersVerification usersVerification = Mockito.mock(UsersVerification.class);
    private final ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);
    private ReviewsService reviewsService = new ReviewsService(
            reviewRepository,
            papersVerification,
            tracksVerification,
            usersVerification
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

        assertThrows(NotFoundException.class, () -> reviewsService.getReview(requesterID, reviewerID, paperID));
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
