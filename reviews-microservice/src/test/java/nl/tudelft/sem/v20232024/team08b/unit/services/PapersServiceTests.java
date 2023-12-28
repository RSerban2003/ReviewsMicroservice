package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Paper;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PapersServiceTests {
    @MockBean
    private PaperRepository paperRepository = Mockito.mock(PaperRepository.class);
    @MockBean
    private ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    @MockBean
    private ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);
    @MockBean
    private VerificationService verificationService = Mockito.mock(VerificationService.class);

    private PapersService papersService;

    private Long reviewerID = 1L;
    private Long paperID = 2L;
    private Long trackID = 3L;
    private Long conferenceID = 4L;
    private Paper fakePaper;
    private Submission fakeSubmission;
    private Review fakeReview;

    @BeforeEach
    void setUp() {
        papersService = new PapersService(paperRepository, reviewRepository, externalRepository, verificationService);

        fakePaper = new Paper();
        fakeSubmission = new Submission();
        fakeReview = new Review();
        fakeSubmission.setEventId(conferenceID);
        fakeSubmission.setTrackId(trackID);
    }

    @Test
    void getPaper_NoSuchPaper() {
        when(verificationService.verifyPaper(paperID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> papersService.getPaper(reviewerID, paperID));
    }

    @Test
    void getPaper_NoSuchUser() throws Exception {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyUser(reviewerID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(false);

        assertThrows(IllegalCallerException.class, () -> papersService.getPaper(reviewerID, paperID));
    }

    @Test
    void getPaper_NotAReviewer() throws Exception {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyUser(reviewerID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.empty());

        assertThrows(IllegalAccessException.class, () -> papersService.getPaper(reviewerID, paperID));
    }

    @Test
    void getPaper_Successful() throws Exception {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyUser(reviewerID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(reviewRepository.findById(new ReviewID(paperID, reviewerID))).thenReturn(Optional.of(fakeReview));
        when(externalRepository.getFullPaper(paperID)).thenReturn(fakePaper);

        Paper result = papersService.getPaper(reviewerID, paperID);
        assertEquals(fakePaper, result);
    }

    @Test
    void getTitleAndAbstract_Successful() throws Exception {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyUser(reviewerID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(externalRepository.getTitleAndAbstract(paperID)).thenReturn(fakePaper);

        Paper result = papersService.getTitleAndAbstract(reviewerID, paperID);
        assertEquals(fakePaper, result);
    }

    @Test
    void getTitleAndAbstract_NoSuchPaper() {
        when(verificationService.verifyPaper(paperID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> papersService.getTitleAndAbstract(reviewerID, paperID));
    }

    @Test
    void getTitleAndAbstract_NoSuchUser() throws Exception {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyUser(reviewerID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(false);

        assertThrows(IllegalCallerException.class, () -> papersService.getTitleAndAbstract(reviewerID, paperID));
    }
}