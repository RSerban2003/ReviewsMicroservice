package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.AssignmentsService;
import nl.tudelft.sem.v20232024.team08b.application.verification.PapersVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.AssignmentsService;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AssignmentsServiceTests {
    private final ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    private final PapersVerification papersVerification = Mockito.mock(PapersVerification.class);
    private final TracksVerification tracksVerification = Mockito.mock(TracksVerification.class);
    private final UsersVerification usersVerification = Mockito.mock(UsersVerification.class);

    private AssignmentsService assignmentsService;

    private final Long reviewerID = 1L;
    private final Long paperID = 2L;
    private final Long requesterID = 3L;
    private Submission fakeSubmission;

    @BeforeEach
    void setUp() {
        assignmentsService = Mockito.spy(
            new AssignmentsService(
                reviewRepository,
                papersVerification,
                tracksVerification,
                usersVerification
            )
        );

        fakeSubmission = new Submission();
        fakeSubmission.setEventId(4L);
        fakeSubmission.setTrackId(5L);
    }

    @Test
    void verifyIfRequesterCanAssignRequesterIsChair()
        throws IllegalAccessException, NotFoundException, ConflictOfInterestException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
            .thenReturn(true);

        assertTrue(assignmentsService.verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR));

        verify(usersVerification).verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
    }

    @Test
    void verifyIfRequesterCanAssignRequesterIsNotChair() {

        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
            .thenReturn(false);
        assertThrows(IllegalAccessException.class, () ->
            assignmentsService.verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR)
        );

        verify(usersVerification).verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
    }

    @Test
    void verifyIfReviewerCanBeAssignedUserNotInTrack() throws NotFoundException, ConflictOfInterestException {

        when(usersVerification.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER))
            .thenReturn(false);
        assertThrows(NotFoundException.class, () ->
            assignmentsService.verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER)
        );

        verify(papersVerification, never()).verifyCOI(anyLong(), anyLong());
    }

    @Test
    void verifyIfReviewerCanBeAssignedUserInTrackNoConflictOfInterest()
        throws NotFoundException, ConflictOfInterestException, IllegalAccessException {

        when(usersVerification.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER))
            .thenReturn(true);
        doNothing().when(papersVerification).verifyCOI(anyLong(), anyLong());
        assertTrue(assignmentsService.verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER));

        verify(papersVerification).verifyCOI(paperID, reviewerID);
    }

    @Test
    void verifyIfReviewerCanBeAssignedUserInTrackConflictOfInterest()
        throws NotFoundException, ConflictOfInterestException {

        when(usersVerification.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER))
            .thenReturn(true);
        doThrow(new ConflictOfInterestException("there is coi")).when(papersVerification).verifyCOI(anyLong(), anyLong());
        assertThrows(ConflictOfInterestException.class, () -> {
            assignmentsService.verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER);
        });
    }

    @Test
    void verifyIfUserCanAssignUndefined() {
        assertThrows(IllegalAccessException.class, () -> {
            assignmentsService.verifyIfUserCanAssign(requesterID, paperID, UserRole.AUTHOR);
        });
    }


    @Test
    void assignManuallySuccessfullyAssigned() throws IllegalAccessException, NotFoundException, ConflictOfInterestException {

        List<TrackPhase> phases = new ArrayList<>();
        TrackPhase phase = TrackPhase.ASSIGNING;
        phases.add(phase);
        doNothing().when(tracksVerification).verifyTrackPhaseThePaperIsIn(paperID, phases);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(usersVerification.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER)).thenReturn(true);
        doNothing().when(tracksVerification).verifyIfTrackExists(paperID);

        // Execute the method
        assertDoesNotThrow(() -> assignmentsService.assignManually(requesterID, reviewerID, paperID));

        // Verify statements
        verify(tracksVerification).verifyTrackPhaseThePaperIsIn(paperID, phases);
        verify(assignmentsService).verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR);
        verify(assignmentsService).verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER);
        verify(tracksVerification).verifyIfTrackExists(paperID);

        // Verify that reviewRepository.save is called with the correct argument
        verify(reviewRepository).save(argThat(review -> review.getReviewID().getPaperID().equals(paperID)
            && review.getReviewID().getReviewerID().equals(reviewerID)));
    }

    @Test
    void assignmentsWrongPhase() throws NotFoundException, IllegalAccessException {
        List phases = List.of(TrackPhase.ASSIGNING, TrackPhase.FINAL, TrackPhase.REVIEWING);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        doThrow(
                new IllegalAccessException("Wrong phase")
        ).when(tracksVerification).verifyTrackPhaseThePaperIsIn(paperID, phases);

        assertThrows(IllegalAccessException.class, () -> {
            assignmentsService.assignments(requesterID, paperID);
        });
    }

    @Test
    void assignmentsWrongRole() {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        assertThrows(IllegalAccessException.class, () -> {
            assignmentsService.assignments(requesterID, paperID);
        });
    }

    @Test
    void assignmentsPaperNotFound() {
        when(papersVerification.verifyPaper(paperID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            assignmentsService.assignments(requesterID, paperID);
        });
    }

    @Test
    void assignmentsSuccessful() throws IllegalAccessException, NotFoundException {
        when(usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        when(papersVerification.verifyPaper(paperID)).thenReturn(true);
        List<Review> reviews = new ArrayList<>();
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(reviews);
        assertThat(assignmentsService.assignments(requesterID, paperID).size()).isEqualTo(0);
        Review r1 = new Review();
        Review r2 = new Review();
        Review r3 = new Review();
        ReviewID riD1 = new ReviewID(paperID, 8L);
        ReviewID riD2 = new ReviewID(paperID, reviewerID);
        ReviewID riD3 = new ReviewID(paperID, reviewerID);
        r1.setReviewID(riD1);
        r2.setReviewID(riD2);
        r3.setReviewID(riD3);
        reviews.add(r1);
        assertThat(assignmentsService.assignments(requesterID, paperID).size()).isEqualTo(1);
        reviews.add(r2);
        assertThat(assignmentsService.assignments(requesterID, paperID).size()).isEqualTo(2);
        reviews.add(r3);
        assertThat(assignmentsService.assignments(requesterID, paperID).size()).isEqualTo(3);
    }

    @Test
    void testRemovePaperDoesNotExist() {
        when(verificationService.verifyPaper(paperID)).thenReturn(false);

        // Assert that NotFoundException is thrown with the expected message
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> assignmentsService.remove(requesterID, paperID, reviewerID));
        assertEquals("this paper does not exist", exception.getMessage());

        // Ensure no interactions with other methods
        verify(verificationService, times(1)).verifyPaper(paperID);
        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void testRemoveNotPCChair() {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);

        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(false);

        IllegalAccessException exception = assertThrows(IllegalAccessException.class,
            () -> assignmentsService.remove(requesterID, paperID, reviewerID));
        assertEquals("Only pc chairs are allowed to do that", exception.getMessage());

        verify(verificationService, times(1)).verifyPaper(paperID);
        verify(verificationService, times(1))
            .verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void testRemoveNoReviewersAssigned() throws NotFoundException, IllegalAccessException {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);

        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);

        doNothing().when(verificationService).verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING));

        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(new ArrayList<>());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> assignmentsService.remove(requesterID, paperID, reviewerID));
        assertEquals("there are no reviewers assigned to this paper", exception.getMessage());

        verify(verificationService, times(1)).verifyPaper(paperID);
        verify(verificationService, times(1))
            .verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
        verify(verificationService, times(1))
            .verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING));
        verify(reviewRepository, times(1)).findByReviewIDPaperID(paperID);
        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void testRemoveReviewerNotFound() throws NotFoundException, IllegalAccessException {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);

        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);

        doNothing().when(verificationService).verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING));

        List<Review> reviews = new ArrayList<>();
        Review review = new Review();
        review.setReviewID(new ReviewID(paperID, 10L));
        reviews.add(review);
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(reviews);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> assignmentsService.remove(requesterID, paperID, reviewerID));
        assertEquals("There is no such a assignment", exception.getMessage());

        verify(verificationService, times(1)).verifyPaper(paperID);
        verify(verificationService, times(1))
            .verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
        verify(verificationService, times(1))
            .verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING));
        verify(reviewRepository, times(1)).findByReviewIDPaperID(paperID);
        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void testRemove() throws NotFoundException, IllegalAccessException {
        when(verificationService.verifyPaper(paperID)).thenReturn(true);
        when(verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)).thenReturn(true);
        doNothing().when(verificationService).verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING));

        List<Review> reviews = new ArrayList<>();
        Review review = new Review();
        review.setReviewID(new ReviewID(paperID, reviewerID));
        reviews.add(review);
        when(reviewRepository.findByReviewIDPaperID(paperID)).thenReturn(reviews);

        assignmentsService.remove(requesterID, paperID, reviewerID);

        verify(reviewRepository, times(1)).delete(review);
    }




}
