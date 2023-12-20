package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUserTracksInner;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

public class VerificationServiceTests {
    @MockBean
    ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);
    private VerificationService verificationService = new VerificationService(externalRepository);

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
    void verifyPaperExists() throws NotFoundException {
        when(externalRepository.getSubmission(1L)).thenReturn(fakeSubmission);
        assertThat(verificationService.verifyPaper(1L)).isEqualTo(true);
    }

    @Test
    void verifyPaperDoesNotExist() throws NotFoundException {
        when(externalRepository.getSubmission(1L)).thenThrow(new NotFoundException(""));
        assertThat(verificationService.verifyPaper(1L)).isEqualTo(false);
    }

    @Test
    void verifyUserExists() throws NotFoundException {
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(verificationService.verifyUser(1L, 4L, 2L, UserRole.REVIEWER)).isEqualTo(true);
    }

    @Test
    void verifyUserExistsButInDifferentConference() throws NotFoundException {
        // This user IS a reviewer in a track with the same ID, but in a different conference
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(verificationService.verifyUser(1L, 3L, 2L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyUserExistsButInDifferentEvent() throws NotFoundException {
        // This user IS a reviewer in a track with the same ID, but in a different track
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(verificationService.verifyUser(1L, 4L, 1L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyUserExistsButBadRole() throws NotFoundException {
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
        assertThat(verificationService.verifyUser(1L, 3L, 2L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyUserDoesNotExist() throws NotFoundException {
        when(externalRepository.getRolesOfUser(1L)).thenThrow(new NotFoundException(""));
        assertThat(verificationService.verifyUser(1L, 4L, 2L, UserRole.REVIEWER)).isEqualTo(false);
    }

    @Test
    void verifyUser2UsersOneExists() throws NotFoundException {
        // Construct a user in the same track, but he is a chair
        RolesOfUserTracksInner innerChair = new RolesOfUserTracksInner();
        innerChair.setRoleName("PC Chair");
        innerChair.setTrackId(2);
        innerChair.setEventId(4);

        // Add the user to the DTO
        fakeRolesOfUser.getTracks().add(innerChair);
        when(externalRepository.getRolesOfUser(1L)).thenReturn(fakeRolesOfUser);
        assertThat(verificationService.verifyUser(1L, 4L, 2L, UserRole.REVIEWER))
                .isEqualTo(true);
    }

    @Test
    void verifyUser2UsersNoneExist() throws NotFoundException {
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
        assertThat(verificationService.verifyUser(1L, 4L, 2L, UserRole.REVIEWER))
                .isEqualTo(false);
    }
}
