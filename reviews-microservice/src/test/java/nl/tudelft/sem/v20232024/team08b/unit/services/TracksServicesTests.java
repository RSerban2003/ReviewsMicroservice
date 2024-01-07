package nl.tudelft.sem.v20232024.team08b.unit.services;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.TracksService;
import nl.tudelft.sem.v20232024.team08b.application.VerificationService;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TracksServicesTests {
    @MockBean
    private final PaperRepository paperRepository = Mockito.mock(PaperRepository.class);

    @MockBean
    private final TrackRepository trackRepository = Mockito.mock(TrackRepository.class);

    @MockBean
    private final ExternalRepository externalRepository = Mockito.mock(ExternalRepository.class);

    @MockBean
    private final VerificationService verificationService = Mockito.mock(VerificationService.class);

    @MockBean
    private final TrackPhaseCalculator trackPhaseCalculator = Mockito.mock(TrackPhaseCalculator.class);

    private final TracksService tracksService = Mockito.spy(
            new TracksService(
                    paperRepository,
                    trackRepository,
                    externalRepository,
                    verificationService,
                    trackPhaseCalculator
            )
    );
    @BeforeEach
    void init() {
        // Assume that the user has no role
        when(
                verificationService.verifyRoleFromTrack(0L, 1L, 2L, UserRole.REVIEWER)
        ).thenReturn(false);
        when(
                verificationService.verifyRoleFromTrack(0L, 1L, 2L, UserRole.CHAIR)
        ).thenReturn(false);
    }

    @Test
    void verifyIfUserCanAccessTrack_NoSuchTrack() {
        when(
                verificationService.verifyTrack(1L, 2L)
        ).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            tracksService.verifyIfUserCanAccessTrack(0L, 1L, 2L);
        });
    }

    void applyRole(UserRole role) {
        when(
                verificationService.verifyRoleFromTrack(0L, 1L, 2L, role)
        ).thenReturn(true);
    }

    @Test
    void verifyIfUserCanAccessTrack_Reviewer() {
        when(
                verificationService.verifyTrack(1L, 2L)
        ).thenReturn(true);

        // Assume the user is a reviewer
        applyRole(UserRole.REVIEWER);

        assertDoesNotThrow(() -> {
            tracksService.verifyIfUserCanAccessTrack(0L, 1L, 2L);
        });
    }

    @Test
    void verifyIfUserCanAccessTrack_Chair() {
        when(
                verificationService.verifyTrack(1L, 2L)
        ).thenReturn(true);

        // Assume the user is a chair
        applyRole(UserRole.CHAIR);

        assertDoesNotThrow(() -> {
            tracksService.verifyIfUserCanAccessTrack(0L, 1L, 2L);
        });
    }

    @Test
    void verifyIfUserCanAccessTrack_NoOne() {
        when(
                verificationService.verifyTrack(1L, 2L)
        ).thenReturn(true);

        // Assume the user is neither chair nor reviewer

        assertThrows(IllegalAccessException.class, () -> {
            tracksService.verifyIfUserCanAccessTrack(0L, 1L, 2L);
        });
    }

    @Test
    void getTrackPhase() throws NotFoundException, IllegalAccessException {
        // Assume that the current phase is bidding
        when(trackPhaseCalculator.getTrackPhase(1L, 2L)).thenReturn(TrackPhase.BIDDING);

        // Assume that the provided input to function is valid
        doNothing().when(tracksService).verifyIfUserCanAccessTrack(0L, 1L, 2L);

        // Make sure, that the service returns the same result that the calculator returns
        assertThat(
                tracksService.getTrackPhase(0L, 1L, 2L)
        ).isEqualTo(TrackPhase.BIDDING);
        verify(tracksService).verifyIfUserCanAccessTrack(0L, 1L, 2L);
    }

}
