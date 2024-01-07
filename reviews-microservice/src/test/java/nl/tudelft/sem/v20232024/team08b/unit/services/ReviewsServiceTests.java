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
    private ReviewsService reviewsService = new ReviewsService(
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
        fakeSubmission.setTrackId(2L);
        fakeSubmission.setEventId(1L);
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
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
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
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);

        // Assume that the user is not a reviewer - i.e., the third IF does not work
        when(verificationService.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);

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
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);

        // Assume that the third if works
        when(verificationService.isReviewerForPaper(requesterID, paperID)).thenReturn(true);

        // Assert that a correct review is added to the repository
        Review expected = new Review(reviewDTO, new ReviewID(paperID, requesterID));
        reviewsService.submitReview(reviewDTO, requesterID, paperID);
        verify(reviewRepository).save(expected);
    }

    @Test
    void verifyIfUserCanAccessReview_NoSuchPaper() {
        // Assume paper does not exist
        when(verificationService.verifyPaper(paperID)).thenReturn(false);

        Assert.assertThrows(NotFoundException.class, () -> {
            reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        });
    }

    @Test
    void verifyIfUserCanAccessReview_NoSuchUser() throws NotFoundException {
        // Assume paper exists
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        // Assume user is not a reviewer
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(false);
        // Assume user is not a chair either
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(false);

        Assert.assertThrows(IllegalCallerException.class, () -> {
            reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        });
    }

    @Test
    void verifyIfUserCanAccessReview_NoSuchReviewer() throws NotFoundException {
        // Assume paper exists
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        // Assume user exists
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(false);
        // Assume the reviewer does not exist
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER))
                .thenReturn(false);
        when(verificationService.isReviewerForPaper(reviewerID, paperID))
                .thenReturn(false);

        Assert.assertThrows(NotFoundException.class, () -> {
            reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        });
    }

    @Test
    void verifyIfUserCanAccessReview_NoSuchReviewConnectedToReviewer() throws NotFoundException {
        // Assume paper exists
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        // Assume is reviewer
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);
        // Assume is not chair
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(false);
        // Assume the reviewer does exist
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER))
                .thenReturn(true);
        // Assume said reviewer is not assigned to the paper
        when(verificationService.isReviewerForPaper(reviewerID, paperID))
                .thenReturn(false);

        Assert.assertThrows(NotFoundException.class, () -> {
            reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
        });
    }

    @Test
    void checkIfReviewExists_SuccessfulChair() throws NotFoundException, IllegalAccessException {
        // Assume paper exists
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        // Assume user is not a reviewer
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(false);
        // Assume user is a chair
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(true);
        // Assume the reviewer does exist
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER))
                .thenReturn(true);
        // Assume the reviewer does exist
        when(verificationService.isReviewerForPaper(reviewerID, paperID))
                .thenReturn(true);

        reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
    }

    @Test
    void checkIfReviewExists_SuccessfulReviewer() throws NotFoundException, IllegalAccessException {
        // Assume paper exists
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        // Assume user is a reviewer
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER))
                .thenReturn(true);
        // Assume user is not a chair
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
                .thenReturn(false);
        // Assume the reviewer does exist
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER))
                .thenReturn(true);
        // Assume the reviewer does exist
        when(verificationService.isReviewerForPaper(reviewerID, paperID))
                .thenReturn(true);

        reviewsService.verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);
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
