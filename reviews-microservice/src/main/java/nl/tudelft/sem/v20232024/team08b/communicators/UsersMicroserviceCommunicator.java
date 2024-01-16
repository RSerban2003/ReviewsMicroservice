package nl.tudelft.sem.v20232024.team08b.communicators;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.dtos.users.Track;
import nl.tudelft.sem.v20232024.team08b.utils.HttpRequestSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsersMicroserviceCommunicator implements CommunicationWithUsersMicroservice {
    private final Long ourID = -1L;
    private final String usersURL = "http://localhost:8082";
    private final ObjectMapper objectMapper;
    private final HttpRequestSender httpRequestSender;

    @Autowired
    public UsersMicroserviceCommunicator(HttpRequestSender httpRequestSender) {
        this.httpRequestSender = httpRequestSender;
        this.objectMapper = new ObjectMapper();
    }

    public UsersMicroserviceCommunicator(ObjectMapper objectMapper, HttpRequestSender httpRequestSender) {
        this.objectMapper = objectMapper;
        this.httpRequestSender = httpRequestSender;
    }

    @Override
    public Track getTrack(Long conferenceID, Long trackID) throws NotFoundException {
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

    @Override
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
}
