package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.application.phase.PaperPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.dtos.review.*;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PapersServiceTests {
    private final ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);
    private final PaperRepository paperRepository = Mockito.mock(PaperRepository.class);
    private final VerificationService verificationService = Mockito.mock(VerificationService.class);
    private final PaperPhaseCalculator paperPhaseCalculator = Mockito.mock(PaperPhaseCalculator.class);

    private PapersService papersService;

    private final Long reviewerID = 1L;
    private final Long paperID = 2L;
    private Submission fakeSubmission;

    @BeforeEach
    void setUp() {
        papersService = Mockito.spy(
                new PapersService(
                        externalRepository,
                        paperRepository,
                        verificationService,
                        paperPhaseCalculator
                )
        );

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
    void getPaper_WrongPhase() throws NotFoundException, IllegalAccessException {
        // Make verifyPermissionToViewUser() passes nicely
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(true);
        when(verificationService.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);

        // Make sure exception is thrown when phase is checked
        doThrow(
                new IllegalAccessException("")
        ).when(verificationService).verifyTrackPhaseThePaperIsIn(
                paperID,
                List.of(TrackPhase.SUBMITTING, TrackPhase.REVIEWING, TrackPhase.FINAL)
        );

        // Assert that the method itself passes the exception upstream
        assertThrows(IllegalAccessException.class, () -> papersService.getPaper(reviewerID, paperID));
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
        assertThat(result).isEqualTo(expectedPaper);
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
        assertThat(result).isEqualTo(expectedPaper);
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
    void getTitleAndAbstract_SuccessfulReviewer() throws NotFoundException,
            IllegalAccessException {

        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(true);

        fakeSubmission.setTitle("Title");
        fakeSubmission.setAbstract("Abstract");

        PaperSummary expectedPaper = new PaperSummary();
        expectedPaper.setTitle("Title");
        expectedPaper.setAbstractSection("Abstract");

        PaperSummary result = papersService.getTitleAndAbstract(reviewerID, paperID);

        assertThat(result).isEqualTo(expectedPaper);
    }

    @Test
    void getTitleAndAbstract_SuccessfulChair() throws NotFoundException,
            IllegalAccessException {

        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(externalRepository.getSubmission(paperID)).thenReturn(fakeSubmission);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.CHAIR)).thenReturn(true);

        fakeSubmission.setTitle("Title");
        fakeSubmission.setAbstract("Abstract");

        PaperSummary expectedPaper = new PaperSummary();
        expectedPaper.setTitle("Title");
        expectedPaper.setAbstractSection("Abstract");

        PaperSummary result = papersService.getTitleAndAbstract(reviewerID, paperID);

        assertThat(result).isEqualTo(expectedPaper);
    }

    @Test
    void getPaperPhase() throws NotFoundException, IllegalAccessException {
        // Assume that the current phase is bidding
        when(paperPhaseCalculator.getPaperPhase(1L)).thenReturn(PaperPhase.REVIEWED);

        // Assume that the provided input to function is valid
        doNothing().when(papersService).verifyPermissionToViewPaper(0L, 1L);

        // Make sure, that the service returns the same result that the calculator returns
        assertThat(
                papersService.getPaperPhase(0L, 1L)
        ).isEqualTo(PaperPhase.REVIEWED);
        verify(papersService).verifyPermissionToViewPaper(0L, 1L);
    }

    @Test
    void verifyPermissionToViewStatus_UserIsReviewer() throws IllegalAccessException {
        when(verificationService.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(true);

        papersService.verifyPermissionToViewStatus(reviewerID, paperID);
    }

    @Test
    void verifyPermissionToViewStatus_UserIsAuthor() throws IllegalAccessException {
        when(verificationService.isAuthorToPaper(reviewerID, paperID)).thenReturn(true);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.AUTHOR)).thenReturn(true);

        papersService.verifyPermissionToViewStatus(reviewerID, paperID);
    }

    @Test
    void verifyPermissionToViewStatus_UserIsChair() throws IllegalAccessException {
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.CHAIR)).thenReturn(true);

        papersService.verifyPermissionToViewStatus(reviewerID, paperID);
    }

    @Test
    void verifyPermissionToViewStatus_UserDoesNotHavePermission() {
        when(verificationService.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(false);
        when(verificationService.isAuthorToPaper(reviewerID, paperID)).thenReturn(false);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.AUTHOR)).thenReturn(false);
        when(verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.CHAIR)).thenReturn(false);

        assertThrows(IllegalAccessException.class, () -> papersService.verifyPermissionToViewStatus(reviewerID, paperID));
    }

    @Test
    void getPaperStatus_Success() throws NotFoundException {
        nl.tudelft.sem.v20232024.team08b.domain.Paper domainPaper = new nl.tudelft.sem.v20232024.team08b.domain.Paper();
        domainPaper.setStatus(PaperStatus.ACCEPTED);
        Optional<nl.tudelft.sem.v20232024.team08b.domain.Paper> optionalPaper = Optional.of(domainPaper);
        when(paperRepository.findById(paperID)).thenReturn(optionalPaper);

        assertThat(papersService.getPaperStatus(paperID)).isEqualTo(PaperStatus.ACCEPTED);
    }

    @Test
    void getPaperStatus_NoPaperFound() {
        when(paperRepository.findById(paperID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> papersService.getPaperStatus(paperID));
    }

    @Test
    void getState_Success() throws NotFoundException, IllegalAccessException {
        doNothing().when(papersService).verifyPermissionToViewStatus(reviewerID, paperID);
        nl.tudelft.sem.v20232024.team08b.domain.Paper domainPaper = new nl.tudelft.sem.v20232024.team08b.domain.Paper();
        domainPaper.setStatus(PaperStatus.ACCEPTED);
        Optional<nl.tudelft.sem.v20232024.team08b.domain.Paper> optionalPaper = Optional.of(domainPaper);
        when(paperRepository.findById(paperID)).thenReturn(optionalPaper);

        assertThat(papersService.getState(reviewerID, paperID)).isEqualTo(PaperStatus.ACCEPTED);
    }

    @Test
    void getState_NoPermission() throws IllegalAccessException {
        doThrow(new IllegalAccessException("")).when(papersService).verifyPermissionToViewStatus(reviewerID, paperID);

        assertThrows(IllegalAccessException.class, () -> papersService.getState(reviewerID, paperID));
    }

    @Test
    void getState_NoPaperFound() throws IllegalAccessException {
        doNothing().when(papersService).verifyPermissionToViewStatus(reviewerID, paperID);
        when(paperRepository.findById(paperID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> papersService.getState(reviewerID, paperID));
    }

}