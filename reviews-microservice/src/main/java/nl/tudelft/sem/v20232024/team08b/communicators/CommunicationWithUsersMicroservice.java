package nl.tudelft.sem.v20232024.team08b.communicators;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.dtos.users.Track;

public interface CommunicationWithUsersMicroservice {
    public Track getTrack(Long conferenceID, Long trackID) throws NotFoundException;

    public RolesOfUser getRolesOfUser(Long userID) throws NotFoundException;
}
