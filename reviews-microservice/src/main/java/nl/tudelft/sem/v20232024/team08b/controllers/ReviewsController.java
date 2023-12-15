package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.api.ReviewsAPI;
import nl.tudelft.sem.v20232024.team08b.application.ReviewsService;
import org.springframework.beans.factory.annotation.Autowired;

public class ReviewsController implements ReviewsAPI {
    private final ReviewsService reviewsService;

    /**
     * Default constructor for the controller.
     *
     * @param reviewsService the respective service to inject
     */
    @Autowired
    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

}
