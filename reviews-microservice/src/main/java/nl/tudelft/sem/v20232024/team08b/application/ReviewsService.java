package nl.tudelft.sem.v20232024.team08b.application;

import nl.tudelft.sem.v20232024.team08b.repos.CommentRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewsService {
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final PaperRepository paperRepository;

    /**
     * Default constructor for the service
     * @param reviewRepository repository storing the reviews
     * @param commentRepository repository storing the comments
     * @param paperRepository repository storing the papers
     */
    @Autowired
    public ReviewsService(ReviewRepository reviewRepository,
                          CommentRepository commentRepository,
                          PaperRepository paperRepository) {
        this.reviewRepository = reviewRepository;
        this.commentRepository = commentRepository;
        this.paperRepository = paperRepository;
    }
}
