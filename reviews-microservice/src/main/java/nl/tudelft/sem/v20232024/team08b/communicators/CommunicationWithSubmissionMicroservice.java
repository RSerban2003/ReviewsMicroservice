package nl.tudelft.sem.v20232024.team08b.communicators;

import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;

public interface CommunicationWithSubmissionMicroservice {
    public Submission getSubmission(Long paperID) throws NotFoundException;
    public List<Submission> getSubmissionsInTrack(TrackID trackID) throws NotFoundException;
    public List<Submission> getSubmissionsInTrack(TrackID trackID, Long requesterID) throws NotFoundException;
}
