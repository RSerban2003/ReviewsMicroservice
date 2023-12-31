package nl.tudelft.sem.v20232024.team08b.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.utils.HttpRequestSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import java.io.IOException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ExternalRepositoryTests {
    HttpRequestSender httpRequestSender = Mockito.mock(HttpRequestSender.class);

    ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);

    ExternalRepository externalRepository = new ExternalRepository(httpRequestSender, objectMapper);

    @Test
    void getRolesOfUserJsonFail() throws NotFoundException, IOException {
        when(httpRequestSender.sendGetRequest(ArgumentMatchers.any())).thenReturn("json");
        when(
                objectMapper.readValue("json", RolesOfUser.class)
        ).thenThrow(new RuntimeException(""));
        assertThrows(RuntimeException.class, () -> externalRepository.getRolesOfUser(1L));
    }

    @Test
    void getRolesOfUserUserNotFound() throws NotFoundException, IOException {
        when(httpRequestSender.sendGetRequest(ArgumentMatchers.any()))
                .thenThrow(new NotFoundException(""));
        assertThrows(NotFoundException.class, () -> externalRepository.getRolesOfUser(1L));
    }

    @Test
    void getRolesOfUserUserSuccessful() throws NotFoundException, IOException {
        RolesOfUser fakeRoles = new RolesOfUser();
        when(httpRequestSender.sendGetRequest(ArgumentMatchers.any())).thenReturn("json");
        when(
                objectMapper.readValue("json", RolesOfUser.class)
        ).thenReturn(fakeRoles);
        assertThat(externalRepository.getRolesOfUser(1L)).isEqualTo(fakeRoles);
    }

    @Test
    void getSubmissionJsonFail() throws NotFoundException, IOException {
        when(httpRequestSender.sendGetRequest(ArgumentMatchers.any())).thenReturn("json");
        when(
                objectMapper.readValue("json", Submission.class)
        ).thenThrow(new RuntimeException(""));
        assertThrows(RuntimeException.class, () -> externalRepository.getSubmission(1L));
    }

    @Test
    void getSubmissionNotFound() throws NotFoundException {
        when(httpRequestSender.sendGetRequest(ArgumentMatchers.any()))
                .thenThrow(new NotFoundException(""));
        assertThrows(NotFoundException.class, () -> externalRepository.getSubmission(1L));
    }

    @Test
    void getSubmissionSuccessful() throws NotFoundException, IOException {
        Submission fakeSubmission = new Submission();
        when(httpRequestSender.sendGetRequest(ArgumentMatchers.any())).thenReturn("json");
        when(
                objectMapper.readValue("json", Submission.class)
        ).thenReturn(fakeSubmission);
        assertThat(externalRepository.getSubmission(1L)).isEqualTo(fakeSubmission);
    }
}
