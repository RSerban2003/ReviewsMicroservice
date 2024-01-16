package nl.tudelft.sem.v20232024.team08b.application.strategies;

import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;

public interface AutomaticAssignmentStrategy {
  void automaticAssignment(TrackID trackID, List<Paper> papers) throws NotFoundException;

}

