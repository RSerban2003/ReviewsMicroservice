package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.application.phase.PaperPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Paper;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummary;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    @MockBean
    private PaperPhaseCalculator paperPhaseCalculator = Mockito.mock(PaperPhaseCalculator.class);

    private PapersService papersService;

    private final Long reviewerID = 1L;
    private final Long paperID = 2L;
    private Submission fakeSubmission;

    @BeforeEach
    void setUp() {
        papersService = new PapersService(
                paperRepository,
                reviewRepository,
                externalRepository,
                verificationService,
                paperPhaseCalculator);

        fakeSubmission = new Submission();
        fakeSubmission.setEventId(3L);
        fakeSubmission.setTrackId(4L);
    }

    @Test
    void getPaper_NoPaperFound() {
        when(verificationService.verifyPaper(paperID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> papersService.getPaper(reviewerID, paperID));
    }

    @Test
    void getPaper_NoUserFound() throws Exception {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(false);

        assertThrows(IllegalCallerException.class, () -> papersService.getPaper(reviewerID, paperID));
    }

    @Test
    void getPaper_NotAReviewer() throws Exception {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(verificationService.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);

        assertThrows(IllegalAccessException.class, () -> papersService.getPaper(reviewerID, paperID));
    }

    @Test
    void getPaper_NoSubmissionFound() throws NotFoundException {
        when(externalRepository.getSubmission(1L)).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> papersService.getPaper(1L, 2L));
    }

    @Test
    void getPaper_Successful_Reviewer() throws NotFoundException,
                                         IllegalAccessException {

        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(verificationService.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);

        fakeSubmission.setTitle("Title");
        fakeSubmission.setKeywords(List.of("Keywords"));
        fakeSubmission.setAbstract("Abstract");
        fakeSubmission.setPaper("Content".getBytes());

        Paper expectedPaper = new Paper();
        expectedPaper.setTitle("Title");
        expectedPaper.setKeywords(List.of("Keywords"));
        expectedPaper.setAbstractSection("Abstract");
        expectedPaper.setMainText(new String(fakeSubmission.getPaper()));

        Paper result = papersService.getPaper(reviewerID, paperID);
        assertThat(result).isEqualToComparingFieldByField(expectedPaper);
    }

    @Test
    void getPaper_Successful_Chair() throws NotFoundException,
            IllegalAccessException {

        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.CHAIR)).thenReturn(true);

        fakeSubmission.setTitle("Title");
        fakeSubmission.setKeywords(List.of("Keywords"));
        fakeSubmission.setAbstract("Abstract");
        fakeSubmission.setPaper("Content".getBytes());

        Paper expectedPaper = new Paper();
        expectedPaper.setTitle("Title");
        expectedPaper.setKeywords(List.of("Keywords"));
        expectedPaper.setAbstractSection("Abstract");
        expectedPaper.setMainText(new String(fakeSubmission.getPaper()));

        Paper result = papersService.getPaper(reviewerID, paperID);
        assertThat(result).isEqualToComparingFieldByField(expectedPaper);
    }

    @Test
    void getTitleAndAbstract_NoPaperFound() {
        when(verificationService.verifyPaper(paperID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> papersService.getTitleAndAbstract(reviewerID, paperID));
    }

    @Test
    void getTitleAndAbstract_NoUserFound() throws Exception {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(false);

        assertThrows(IllegalCallerException.class, () -> papersService.getTitleAndAbstract(reviewerID, paperID));
    }

    @Test
    void getTitleAndAbstract_NoSubmissionFound() throws NotFoundException {
        when(externalRepository.getSubmission(1L)).thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class, () -> papersService.getTitleAndAbstract(1L, 2L));
    }

    @Test
    void getTitleAndAbstract_Successful() throws NotFoundException,
            IllegalAccessException {

        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(true);

        fakeSubmission.setTitle("Title");
        fakeSubmission.setAbstract("Abstract");

        Paper expectedPaper = new Paper();
        expectedPaper.setTitle("Title");
        expectedPaper.setAbstractSection("Abstract");

        PaperSummary result = papersService.getTitleAndAbstract(reviewerID, paperID);

        assertThat(result).isEqualToComparingFieldByField(expectedPaper);
    }

}