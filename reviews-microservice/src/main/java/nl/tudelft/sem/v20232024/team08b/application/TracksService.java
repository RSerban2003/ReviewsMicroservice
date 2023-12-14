package nl.tudelft.sem.v20232024.team08b.application;

import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TracksService {
    private final PaperRepository paperRepository;
    private final TrackRepository trackRepository;
    private final ExternalRepository externalRepository;

    /**
     * Default constructor for the service.
     *
     * @param paperRepository repository storing the papers
     * @param trackRepository repository storing the tracks
     * @param externalRepository repository storing everything outside of
     *                           this microservice
     */
    @Autowired
    public TracksService(PaperRepository paperRepository,
                         TrackRepository trackRepository,
                         ExternalRepository externalRepository) {
        this.paperRepository = paperRepository;
        this.trackRepository = trackRepository;
        this.externalRepository = externalRepository;
    }
}
