package nl.tudelft.sem.v20232024.team08b.unit.verification;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.User;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUserTracksInner;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class UsersVerificationTests {

    private ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);
    private ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    UsersVerification usersVerification = Mockito.spy(new UsersVerification(
            externalRepository,
            reviewRepository
    ));

    private Submission fakeSubmission;
    private RolesOfUser fakeRolesOfUser;

    @BeforeEach
    void prepare() {
        fakeSubmission = new Submission();
        fakeSubmission.setTrackId(3L);

        RolesOfUserTracksInner innerReviewer = new RolesOfUserTracksInner();
        innerReviewer.setRoleName("PC Member");
        innerReviewer.setTrackId(2);
        innerReviewer.setEventId(4);

        List<RolesOfUserTracksInner> listOfTracks = new ArrayList<>();
        listOfTracks.add(innerReviewer);

        fakeRolesOfUser = new RolesOfUser();
        fakeRolesOfUser.setTracks(listOfTracks);
    }

    @Test
    void verifyRoleFromTrack_verifyUserExists() throws NotFoundException {
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(usersVerification.verifyRoleFromTrack(1L, 4L, 2L, UserRole.REVIEWER)).isEqualTo(true);
    }


    @Test
    void verifyRoleFromTrack_verifyUserExistsButInDifferentConference() throws NotFoundException {
        // This user IS a reviewer in a track with the same ID, but in a different conference
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(usersVerification.verifyRoleFromTrack(1L, 3L, 2L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyRoleFromTrack_verifyUserExistsButInDifferentEvent() throws NotFoundException {
        // This user IS a reviewer in a track with the same ID, but in a different track
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(usersVerification.verifyRoleFromTrack(1L, 4L, 1L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyRoleFromTrack_verifyUserExistsButBadRole() throws NotFoundException {
        // This user is in the same conference and track, but is not a reviewer

        // Construct a user in the same track, but he is a chair
        RolesOfUserTracksInner innerChair = new RolesOfUserTracksInner();
        innerChair.setRoleName("PC Chair");
        innerChair.setTrackId(2);
        innerChair.setEventId(4);

        // Add the user to the DTO
        List<RolesOfUserTracksInner> listOfTracks = new ArrayList<>();
        listOfTracks.add(innerChair);
        fakeRolesOfUser = new RolesOfUser();
        fakeRolesOfUser.setTracks(listOfTracks);

        // Mock the return
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(usersVerification.verifyRoleFromTrack(1L, 3L, 2L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyRoleFromTrack_verifyUserDoesNotExist() throws NotFoundException {
        when(externalRepository.getRolesOfUser(1L)).thenThrow(new NotFoundException(""));
        assertThat(usersVerification.verifyRoleFromTrack(1L, 4L, 2L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyRoleFromTrack_verifyUser2UsersOneExists() throws NotFoundException {
        // Construct a user in the same track, but he is a chair
        RolesOfUserTracksInner innerChair = new RolesOfUserTracksInner();
        innerChair.setRoleName("PC Chair");
        innerChair.setTrackId(2);
        innerChair.setEventId(4);

        // Add the user to the DTO
        fakeRolesOfUser.getTracks().add(innerChair);
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(usersVerification.verifyRoleFromTrack(1L, 4L, 2L, UserRole.REVIEWER))
                .isEqualTo(true);
    }

    @Test
    void verifyRoleFromTrack_verifyUser2UsersNoneExist() throws NotFoundException {
        // Construct a user in the same track, but he is a chair
        RolesOfUserTracksInner innerChair = new RolesOfUserTracksInner();
        innerChair.setRoleName("PC Chair");
        innerChair.setTrackId(2);
        innerChair.setEventId(4);

        // Add the user to the DTO
        fakeRolesOfUser.getTracks().clear();
        fakeRolesOfUser.getTracks().add(innerChair);
        fakeRolesOfUser.getTracks().add(innerChair);

        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(usersVerification.verifyRoleFromTrack(1L, 4L, 2L, UserRole.REVIEWER))
                .isEqualTo(false);
    }


    @Test
    void verifyRoleFromPaper_Yes() throws NotFoundException {
        Submission fakeSubmission = new Submission();
        fakeSubmission.setEventId(1L);
        fakeSubmission.setTrackId(2L);

        when(externalRepository.getSubmission(3L)).thenReturn(fakeSubmission);
        doReturn(true).when(usersVerification)
                .verifyRoleFromTrack(0L, 1L, 2L, UserRole.CHAIR);
        boolean result = usersVerification.verifyRoleFromPaper(0L, 3L, UserRole.CHAIR);
        assertThat(result).isTrue();
    }

    @Test
    void verifyRoleFromPaper_No() throws NotFoundException {
        Submission fakeSubmission = new Submission();
        fakeSubmission.setEventId(1L);
        fakeSubmission.setTrackId(2L);

        when(externalRepository.getSubmission(3L)).thenReturn(fakeSubmission);
        doReturn(false).when(usersVerification)
                .verifyRoleFromTrack(0L, 1L, 2L, UserRole.CHAIR);
        boolean result = usersVerification.verifyRoleFromPaper(0L, 3L, UserRole.CHAIR);
        assertThat(result).isFalse();
    }

    @Test
    void verifyRoleFromPaper_NoSuchPaper() throws NotFoundException {
        when(externalRepository.getSubmission(3L)).thenThrow(
                new NotFoundException("")
        );
        boolean result = usersVerification.verifyRoleFromPaper(0L, 3L, UserRole.CHAIR);
        assertThat(result).isFalse();
    }

    @Test
    void userIsReviewerForPaper() {
        Long reviewerID = 1L;
        Long paperID = 1L;

        when(reviewRepository.isReviewerForPaper(reviewerID, paperID)).thenReturn(true);
        assertThat(usersVerification.isReviewerForPaper(reviewerID, paperID)).isEqualTo(true);
    }

    @Test
    void userIsNotReviewerForPaper() {
        Long reviewerID = 1L;
        Long paperID = 1L;

        when(reviewRepository.isReviewerForPaper(reviewerID, paperID)).thenReturn(false);
        assertThat(usersVerification.isReviewerForPaper(reviewerID, paperID)).isEqualTo(false);
    }

    @Test
    void verifyIsAuthorToPaper_NoSuchPaper() throws NotFoundException {
        when(externalRepository.getSubmission(3L)).thenThrow(
                new NotFoundException("")
        );
        boolean result = usersVerification.isAuthorToPaper(0L, 3L);
        assertThat(result).isFalse();
    }

    @Test
    void verifyIsAuthorToPaper_NotAuthor() throws NotFoundException {
        User user1 = new User();
        User user2 = new User();
        user1.setUserId(1L);
        user2.setUserId(2L);
        fakeSubmission.setAuthors(List.of(user1, user2));
        when(externalRepository.getSubmission(3L)).thenReturn(fakeSubmission);
        boolean result = usersVerification.isAuthorToPaper(0L, 3L);
        assertThat(result).isFalse();
    }

    @Test
    void verifyIsAuthorToPaper() throws NotFoundException {
        User user1 = new User();
        User user2 = new User();
        user1.setUserId(1L);
        user2.setUserId(0L);
        fakeSubmission.setAuthors(List.of(user1, user2));
        when(externalRepository.getSubmission(3L)).thenReturn(fakeSubmission);
        boolean result = usersVerification.isAuthorToPaper(0L, 3L);
        assertThat(result).isTrue();
    }
}
