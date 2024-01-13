package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.ReviewsService;
import nl.tudelft.sem.v20232024.team08b.application.verification.PapersVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.domain.*;
import nl.tudelft.sem.v20232024.team08b.dtos.review.DiscussionComment;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    void verifySubmitConfidentialComment_NotFoundException() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                reviewsService.verifySubmitConfidentialComment(requesterID, reviewerID, paperID));
    }

    @Test
    void verifySubmitConfidentialComment_IllegalAccessException() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        assertThrows(IllegalAccessException.class, () ->
                reviewsService.verifySubmitConfidentialComment(requesterID, reviewerID, paperID));
    }

    @Test
    void verifySubmitConfidentialComment_Successful() throws NotFoundException, IllegalAccessException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);

        reviewsService.verifySubmitConfidentialComment(requesterID, reviewerID, paperID);
    }

    @Test
    void submitConfidentialComment_NotFoundException() throws NotFoundException, IllegalAccessException {
        reviewsService = Mockito.spy(reviewsService);

        doThrow(new NotFoundException(""))
                .when(reviewsService).verifySubmitConfidentialComment(requesterID, reviewerID, paperID);

        assertThrows(NotFoundException.class, () -> reviewsService.submitConfidentialComment(requesterID, reviewerID, paperID, "text"));
    }

    @Test
    void submitConfidentialComment_IllegalAccessException() throws NotFoundException, IllegalAccessException {
        reviewsService = Mockito.spy(reviewsService);

        doThrow(new IllegalAccessException(""))
                .when(reviewsService).verifySubmitConfidentialComment(requesterID, reviewerID, paperID);

        assertThrows(IllegalAccessException.class, () -> reviewsService.submitConfidentialComment(requesterID, reviewerID, paperID, "text"));
    }

    @Test
    void submitConfidentialComment_Successful() throws NotFoundException, IllegalAccessException {
        Comment comment = new Comment(requesterID, "text");

        reviewsService = spy(reviewsService);

        doNothing().when(reviewsService).verifySubmitConfidentialComment(requesterID, reviewerID, paperID);
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(fakeReview));

        reviewsService.submitConfidentialComment(requesterID, reviewerID, paperID, "text");
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

        assertThrows(IllegalAccessException.class, () -> reviewsService.getDiscussionComments(requesterID, reviewerID, paperID));
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
}
