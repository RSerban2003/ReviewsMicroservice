package nl.tudelft.sem.v20232024.team08b.application;

import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PapersService {
    private final PaperRepository paperRepository;
    private final ReviewRepository reviewRepository;
    private final ExternalRepository externalRepository;

    /**
     * Default constructor for the service.
     *
     * @param paperRepository repository storing the papers
     * @param reviewRepository repository storing the reviews
     * @param externalRepository repository storing everything outside of
     *                           this microservice
     */
    @Autowired
    public PapersService(PaperRepository paperRepository,
                         ReviewRepository reviewRepository,
                         ExternalRepository externalRepository) {
        this.paperRepository = paperRepository;
        this.reviewRepository = reviewRepository;
        this.externalRepository = externalRepository;
    }
}
