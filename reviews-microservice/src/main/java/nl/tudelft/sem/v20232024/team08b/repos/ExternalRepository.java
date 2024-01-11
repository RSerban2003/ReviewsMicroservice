package nl.tudelft.sem.v20232024.team08b.repos;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.dtos.users.Track;
import nl.tudelft.sem.v20232024.team08b.utils.HttpRequestSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExternalRepository {
    private final int ourID = -1;
    private final String submissionsURL = "https://localhost:8081";
    private final String usersURL = "https://localhost:8080";
    private final ObjectMapper objectMapper;
    private final HttpRequestSender httpRequestSender;

    /**
     * Default constructor.
     *
     * @param httpRequestSender class used for sending HTTP requests
     */
    @Autowired
    public ExternalRepository(HttpRequestSender httpRequestSender) {
        this.httpRequestSender = httpRequestSender;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructor used for testing purposes.
     *
     * @param httpRequestSender class used for sending HTTP requests
     * @param objectMapper class used to map objects to json
     */
    public ExternalRepository(HttpRequestSender httpRequestSender, ObjectMapper objectMapper) {
        this.httpRequestSender = httpRequestSender;
        this.objectMapper = objectMapper;
    }

    /**
     * Gets from the Users microservice all the roles of a user.
     *
     * @param userID the ID of the user
     * @return a list of roles of that user
     */
    public RolesOfUser getRolesOfUser(Long userID) throws NotFoundException {
        try {
            String url = usersURL + "/user/" + userID + "/tracks/role";
            String response = httpRequestSender.sendGetRequest(url);
            RolesOfUser rolesOfUser;
            rolesOfUser = objectMapper.readValue(response, RolesOfUser.class);
            return rolesOfUser;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse the HTTP response");
        }
    }

    /**
     * Gets a paper (called submission) from the Submissions microservice.
     *
     * @param paperID the ID of the paper to get
     * @return the gotten Submission object
     */
    public Submission getSubmission(Long paperID) throws NotFoundException {
        try {
            String url = submissionsURL + "/submission/" + paperID + "/" + ourID;
            String response = httpRequestSender.sendGetRequest(url);
            Submission submission;
            submission = objectMapper.readValue(response, Submission.class);
            return submission;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse the HTTP response");
        }
    }

    /**
     * Gets a track from the Users microservice.
     *
     * @param conferenceID the ID of the conference the track is in
     * @param trackID the ID of the conference the track is in
     * @return the track object, from the Users microservice
     */
    public Track getTrack(Long conferenceID,
                          Long trackID) throws NotFoundException {
        try {
            String url = usersURL + "/track/" + conferenceID + "/" + trackID;
            String response = httpRequestSender.sendGetRequest(url);
            Track track;
            track = objectMapper.readValue(response, Track.class);
            return track;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse the HTTP response");
        }
    }
}
