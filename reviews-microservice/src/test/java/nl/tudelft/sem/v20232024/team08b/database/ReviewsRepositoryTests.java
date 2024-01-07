package nl.tudelft.sem.v20232024.team08b.database;

import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
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

    @Test
    public void testCustomGetterByPartialID() {
        // Create fake review 1
        Review review1 = new Review();
        ReviewID reviewId1 = new ReviewID(10L, 2L);
        review1.setReviewID(reviewId1);

        // Create fake review 2
        Review review2 = new Review();
        ReviewID reviewId2 = new ReviewID(11L, 2L);
        review2.setReviewID(reviewId2);

        // Add them to repo
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        // Get them back using the custom method and assert behaviour
        List<Review> got = reviewRepository.findByReviewIDPaperID(10L);
        assertThat(got).isEqualTo(List.of(review1));
    }
}