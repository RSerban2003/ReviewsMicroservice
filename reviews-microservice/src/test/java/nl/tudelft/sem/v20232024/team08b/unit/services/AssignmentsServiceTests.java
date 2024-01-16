package nl.tudelft.sem.v20232024.team08b.unit.services;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.AssignmentsService;
import nl.tudelft.sem.v20232024.team08b.application.verification.PapersVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.domain.Bid;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperStatus;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class AssignmentsServiceTests {
    private final ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    private final BidRepository bidRepository = Mockito.mock(BidRepository.class);
    private final TrackRepository trackRepository = Mockito.mock(TrackRepository.class);
    private final PapersVerification papersVerification = Mockito.mock(PapersVerification.class);
    private final TracksVerification tracksVerification = Mockito.mock(TracksVerification.class);
    private final UsersVerification usersVerification = Mockito.mock(UsersVerification.class);

    private AssignmentsService assignmentsService;

    private final Long reviewerID = 1L;
    private final Long paperID = 2L;
    private final Long requesterID = 3L;
    private final Long trackID = 4L;
    private final Long conferenceID = 5L;
    private Submission fakeSubmission;

    @BeforeEach
    void setUp() {
        assignmentsService = Mockito.spy(
            new AssignmentsService(
                bidRepository,
                reviewRepository,
                trackRepository,
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
    void trackNotExist() throws NotFoundException, IllegalAccessException {
        when(tracksVerification.verifyTrack(123L,123L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            assignmentsService.assignAuto(123L, 123L, 123L);
        });
    }

    @Test
    void userNotExist() throws NotFoundException, IllegalAccessException {
        when(tracksVerification.verifyTrack(123L,123L)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack
            (123L, 123L, 123L, UserRole.REVIEWER)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            assignmentsService.assignAuto(123L, 123L, 123L);
        });
    }

    @Test
    void notPcChair() throws NotFoundException, IllegalAccessException {
        when(tracksVerification.verifyTrack(123L,123L)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack
            (123L, 123L, 123L, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack
            (123L, 123L, 123L, UserRole.CHAIR)).thenReturn(false);
        assertThrows(IllegalAccessException.class, () -> {
            assignmentsService.assignAuto(123L, 123L, 123L);
        });
    }

    @Test
    void zeroUser() throws NotFoundException, IllegalAccessException {
        List<Paper> papers = new ArrayList<>();
        List<Bid> bids = new ArrayList<>();
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Date date = new Date();
        Optional<Track> trackOptional = Optional.of(new Track(trackID1, date, false, papers));
        Track track = trackOptional.get();
        Paper paper = new Paper(123L, track, PaperStatus.ACCEPTED, false);
        papers.add(paper);
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack
            (requesterID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack
            (requesterID, conferenceID, trackID, UserRole.CHAIR)).thenReturn(true);
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
            trackOptional
        );

        when(bidRepository.getBidsOfPapers(requesterID, paper.getId())).thenReturn(bids);
        assertThrows(IllegalArgumentException.class, () -> {
            assignmentsService.assignAuto(requesterID, conferenceID, trackID);
        });



    }

    @Test
    void oneUser() throws NotFoundException, IllegalAccessException {
        List<Paper> papers = new ArrayList<>();
        List<Bid> bids = new ArrayList<>();
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Date date = new Date();
        Optional<Track> trackOptional = Optional.of(new Track(trackID1, date, false, papers));
        Track track = trackOptional.get();
        Paper paper = new Paper(123L, track, PaperStatus.ACCEPTED, false);
        Bid bid = new Bid(paper.getId(), 123L,
            nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        papers.add(paper);
        bids.add(bid);
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack
            (requesterID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack
            (requesterID, conferenceID, trackID, UserRole.CHAIR)).thenReturn(true);
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
            trackOptional
        );

        when(bidRepository.getBidsOfPapers(requesterID, paper.getId())).thenReturn(bids);
        assignmentsService.assignAuto(requesterID, conferenceID, trackID);
        verify(reviewRepository).save(argThat(review -> review.getReviewID().getPaperID().equals(123L)
            && review.getReviewID().getReviewerID().equals(123L)));


    }

    @Test
    void threeUser() throws NotFoundException, IllegalAccessException {
        List<Paper> papers = new ArrayList<>();
        List<Bid> bids = new ArrayList<>();
        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Date date = new Date();
        Optional<Track> trackOptional = Optional.of(new Track(trackID1, date, false, papers));
        Track track = trackOptional.get();
        Paper paper = new Paper(123L, track, PaperStatus.ACCEPTED, false);
        Bid bid = new Bid(paper.getId(), 1L,
            nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        Bid bid2 = new Bid(paper.getId(), 2L,
            nl.tudelft.sem.v20232024.team08b.dtos.review.Bid.CAN_REVIEW);
        List<Paper> user1Papers = new ArrayList<>();
        List<Paper> user2Papers = new ArrayList<>();
        user1Papers.add(paper);
        user1Papers.add(paper);
        user2Papers.add(paper);
        papers.add(paper);
        bids.add(bid);
        bids.add(bid2);
        when(tracksVerification.verifyTrack(conferenceID, trackID)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack
            (requesterID, conferenceID, trackID, UserRole.REVIEWER)).thenReturn(true);
        when(usersVerification.verifyRoleFromTrack
            (requesterID, conferenceID, trackID, UserRole.CHAIR)).thenReturn(true);
        when(trackRepository.findById(new TrackID(conferenceID, trackID))).thenReturn(
            trackOptional
        );

        when(bidRepository.getBidsOfPapers(requesterID, paper.getId())).thenReturn(bids);
        assignmentsService.assignAuto(requesterID, conferenceID, trackID);
        when(assignmentsService.byReviewer(1L)).thenReturn(user1Papers);
        when(assignmentsService.byReviewer(2L)).thenReturn(user2Papers);
        verify(reviewRepository).save(argThat(review -> review.getReviewID().getPaperID().equals(123L)
            && review.getReviewID().getReviewerID().equals(1L)));
        verify(reviewRepository).save(argThat(review -> review.getReviewID().getPaperID().equals(123L)
            && review.getReviewID().getReviewerID().equals(2L)));



    }



}
