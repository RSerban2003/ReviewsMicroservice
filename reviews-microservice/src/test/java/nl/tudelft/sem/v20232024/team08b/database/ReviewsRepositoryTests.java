package nl.tudelft.sem.v20232024.team08b.database;

import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class ReviewsRepositoryTests {
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void test() {
        Review review = new Review();
        ReviewID reviewId = new ReviewID(10L, 2L);
        review.setReviewID(reviewId);
        reviewRepository.save(review);

        Optional<Review> got = reviewRepository.findById(reviewId);
        assertThat(got).isNotEmpty();
        assertThat(got.get().getReviewID()).isEqualTo(reviewId);
    }
}