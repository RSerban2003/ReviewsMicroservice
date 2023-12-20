package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.ReviewsService;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.domain.ConfidenceScore;
import nl.tudelft.sem.v20232024.team08b.domain.RecommendationScore;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.CommentRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ReviewsServiceTests {
    @MockBean
    private final ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    @MockBean
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    @MockBean
    private final PaperRepository paperRepository = Mockito.mock(PaperRepository.class);
    @MockBean
    private final VerificationService verificationService = Mockito.mock(VerificationService.class);
    @MockBean
    private final ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);
    ReviewsService reviewsService = new ReviewsService(
            reviewRepository,
            commentRepository,
            paperRepository,
            verificationService,
            externalRepository
    );

    private nl.tudelft.sem.v20232024.team08b.dtos.review.Review reviewDTO;
    private Submission fakeSubmission;
    private Review fakeReview;
    private Long requesterID = 0L;
    private Long conferenceID = 1L;
    private Long trackID = 2L;
    private Long reviewerID = 3L;
    private Long paperID = 4L;
    @BeforeEach
    void prepare() {
        reviewDTO = new nl.tudelft.sem.v20232024.team08b.dtos.review.Review(
                ConfidenceScore.BASIC,
                "Comment for author",
                "Confidential comment",
                RecommendationScore.STRONG_ACCEPT
        );

        fakeReview = new Review();

        fakeSubmission = new Submission();
        fakeSubmission.setTrackId(trackID);
        fakeSubmission.setEventId(conferenceID);
    }

    @Test
    void submitReview_NoSuchPaper() {
        when(verificationService.verifyPaper(paperID)).thenReturn(false);
        Assert.assertThrows(NotFoundException.class, () -> {
            reviewsService.submitReview(reviewDTO, requesterID, paperID);
        });
    }

    @Test
    void submitReview_NoSuchUser() throws NotFoundException {
        // Assume first IF works
        when(verificationService.verifyPaper(paperID)).thenReturn(true);

        // Essentially, fake the track, that the submission belongs to
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);

        // Assume the second IF does not work
        when(verificationService.verifyUser(requesterID, conferenceID, trackID, UserRole.REVIEWER))
                .thenReturn(false);

        Assert.assertThrows(IllegalCallerException.class, () -> {
            reviewsService.submitReview(reviewDTO, requesterID, paperID);
        });
    }

    @Test
    void submitReview_NotAReviewer() throws NotFoundException {
        // Assume first IF works
        when(verificationService.verifyPaper(paperID)).thenReturn(true);

        // Essentially, fake the track, that the submission belongs to
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);

        // Assume the second IF works
        when(verificationService.verifyUser(requesterID, conferenceID, trackID, UserRole.REVIEWER))
                .thenReturn(true);

        // Assume that the user is not a reviewer - i.e., the third IF does not work
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID)))
                .thenReturn(Optional.empty());

        Assert.assertThrows(IllegalAccessException.class, () -> {
            reviewsService.submitReview(reviewDTO, requesterID, paperID);
        });
    }

    @Test
    void submitReview_Successful() throws Exception {
        // Assume first IF works
        when(verificationService.verifyPaper(paperID)).thenReturn(true);

        // Essentially, fake the track, that the submission belongs to
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);

        // Assume the second IF works
        when(verificationService.verifyUser(requesterID, conferenceID, trackID, UserRole.REVIEWER))
                .thenReturn(true);

        // Assume that the third if works
        when(reviewRepository.findById(new ReviewID(paperID, requesterID)))
                .thenReturn(Optional.of(fakeReview));

        // Assert that a correct review is added to the repository
        Review expected = new Review(reviewDTO, new ReviewID(paperID, requesterID));
        reviewsService.submitReview(reviewDTO, requesterID, paperID);
        verify(reviewRepository).save(expected);
    }

    @Test
    void checkIfReviewExists_NoSuchPaper() {
        // Assume paper does not exist
        when(verificationService.verifyPaper(paperID)).thenReturn(false);
        assertThat(reviewsService.checkIfReviewExists(conferenceID, trackID, reviewerID, paperID))
                .isEqualTo(false);
    }

    @Test
    void checkIfReviewExists_NoSuchUser() {
        // Assume paper exists
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        // Assume reviewer does not exist
        when(verificationService.verifyUser(reviewerID, conferenceID, trackID, UserRole.REVIEWER))
                .thenReturn(false);

        assertThat(reviewsService.checkIfReviewExists(conferenceID, trackID, reviewerID, paperID))
                .isEqualTo(false);
    }

    @Test
    void checkIfReviewExists_NoSuchReview() {
        // Assume paper exists
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        // Assume user exists
        when(verificationService.verifyUser(reviewerID, conferenceID, trackID, UserRole.REVIEWER))
                .thenReturn(true);
        // Assume such review does not exist
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID)))
                .thenReturn(Optional.empty());
        assertThat(reviewsService.checkIfReviewExists(conferenceID, trackID, reviewerID, paperID))
                .isEqualTo(false);
    }

    @Test
    void checkIfReviewExists_Successful() {
        // Assume paper exists
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        // Assume reviewer exists
        when(verificationService.verifyUser(reviewerID, conferenceID, trackID, UserRole.REVIEWER))
                .thenReturn(true);
        // Assume such submission exists
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID)))
                .thenReturn(Optional.of(fakeReview));
        assertThat(reviewsService.checkIfReviewExists(conferenceID, trackID, reviewerID, paperID))
                .isEqualTo(true);
    }

    @Test
    void verifyIfUserCanAccessReview_NotReviewer() throws NotFoundException {
        // Fake the track, that the submission belongs to
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);

        // Assume user is not a reviewer and not a chair
        when(verificationService.verifyUser(requesterID, conferenceID, trackID, UserRole.REVIEWER))
                .thenReturn(false);
        when(verificationService.verifyUser(requesterID, conferenceID, trackID, UserRole.CHAIR))
                .thenReturn(false);

        assertThrows(IllegalCallerException.class, () -> {
            reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        });
    }

    @Test
    void verifyIfUserCanAccessReview_ReviewDoesNotExist() throws NotFoundException {

        // We are going to mock the "checkIfReviewExists" method.
        reviewsService = Mockito.spy(reviewsService);

        // Fake the track, that the submission belongs to
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);

        // Assume user is a reviewer
        when(verificationService.verifyUser(requesterID, conferenceID, trackID, UserRole.REVIEWER))
                .thenReturn(true);
        when(verificationService.verifyUser(requesterID, conferenceID, trackID, UserRole.CHAIR))
                .thenReturn(false);

        // Assume such review does not exist
        doReturn(false).when(reviewsService)
                .checkIfReviewExists(conferenceID, trackID, reviewerID, paperID);

        assertThrows(NotFoundException.class, () -> {
            reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        });
    }

    @Test
    void verifyIfUserCanAccessReview_Successful() throws NotFoundException {

        // We are going to mock the "checkIfReviewExists" method.
        reviewsService = Mockito.spy(reviewsService);

        // Fake the track, that the submission belongs to
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);

        // Assume user is a reviewer
        when(verificationService.verifyUser(requesterID, conferenceID, trackID, UserRole.REVIEWER))
                .thenReturn(true);
        when(verificationService.verifyUser(requesterID, conferenceID, trackID, UserRole.CHAIR))
                .thenReturn(false);

        // Assume such review does exist
        doReturn(true).when(reviewsService)
                .checkIfReviewExists(conferenceID, trackID, reviewerID, paperID);

        assertDoesNotThrow(() -> {
            reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        });
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
    void getReview_UnauthorizedAccess() throws NotFoundException, IllegalAccessException {
        // We are going to mock the "verifyIfUserCanAccessReview" method
        reviewsService = Mockito.spy(reviewsService);

        // Assume the review was not found
        doThrow(new NotFoundException("")).when(reviewsService)
                .verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);

        assertThrows(NotFoundException.class, () -> {
            reviewsService.getReview(requesterID, reviewerID, paperID);
        });
    }
}
