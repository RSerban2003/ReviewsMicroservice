package nl.tudelft.sem.v20232024.team08b.application.strategies;

import java.util.List;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;

public interface AutomaticAssignmentStrategy {
  void automaticAssignment(List<Paper> papers, Long requesterID);

}

