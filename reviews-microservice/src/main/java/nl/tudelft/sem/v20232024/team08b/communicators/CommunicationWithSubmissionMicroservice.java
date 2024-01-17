package nl.tudelft.sem.v20232024.team08b.communicators;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;

import java.util.List;

public interface CommunicationWithSubmissionMicroservice {
    public Submission getSubmission(Long paperID) throws NotFoundException;

    public List<Submission> getSubmissionsInTrack(Long conferenceID, Long trackID) throws NotFoundException;

    public List<Submission> getSubmissionsInTrack(Long conferenceID, Long trackID, Long requesterID)
            throws NotFoundException;
}
