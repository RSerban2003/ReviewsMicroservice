package nl.tudelft.sem.v20232024.team08b.communicators;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.utils.HttpRequestSender;

public class SubmissionsMicroserviceCommunicator implements CommunicationWithSubmissionMicroservice{

    private final Long ourID = -1L;
    private final String submissionsURL = "http://localhost:8081";
    private final ObjectMapper objectMapper;
    private final HttpRequestSender httpRequestSender;

    public SubmissionsMicroserviceCommunicator(ObjectMapper objectMapper, HttpRequestSender httpRequestSender) {
        this.objectMapper = objectMapper;
        this.httpRequestSender = httpRequestSender;
    }

    public SubmissionsMicroserviceCommunicator(HttpRequestSender httpRequestSender) {
        this.objectMapper = new ObjectMapper();
        this.httpRequestSender = httpRequestSender;
    }

    @Override
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

    @Override
    public List<Submission> getSubmissionsInTrack(TrackID trackID) throws NotFoundException {
        return getSubmissionsInTrack(trackID, ourID);
    }

    @Override
    public List<Submission> getSubmissionsInTrack(TrackID trackID, Long requesterID) throws NotFoundException {
        try {
            String url = submissionsURL + "/submission/event/" + trackID.getConferenceID()
                + "/track/" + trackID.getTrackID() + "/" + requesterID;
            String response = httpRequestSender.sendGetRequest(url);
            List<Submission> submissions;
            submissions = objectMapper.readValue(response, List.class);
            return submissions;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse the HTTP response");
        }
    }
}
