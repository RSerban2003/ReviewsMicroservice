package nl.tudelft.sem.v20232024.team08b.repos;

import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface ReviewRepository extends JpaRepository<Review, ReviewID> {

    /**
     * Checks whether a user is assigned as a reviewer to a specific paper.
     *
     * @param reviewerID the ID of the user
     * @param paperID the ID of the paper
     * @return whether the user is assigned to the paper or not
     */
    default boolean isReviewerForPaper(Long reviewerID, Long paperID) {
        return findById(new ReviewID(paperID, reviewerID)).isPresent();
    }

    /**
     * Finds a list of reviews that are assigned to given paper.
     *
     * @param paperID the ID of the paper
     * @return a list of reviews by that reviewer
     */
    List<Review> findByReviewIDPaperID(Long paperID);

    /**
     * Finds a list of reviewIDs that are assigned to the reviewer.
     *
     * @param reviewerID the ID of the reviewer
     * @return a list of reviewIDs assigned to the reviewer
     */
    List<Review> findByReviewIDReviewerID(Long reviewerID);

    default List<Long> findPapersByReviewer(Long reviewerID) {
        List<Review> reviews = findByReviewIDReviewerID(reviewerID);
        return reviews.stream().map(x -> x.getReviewID().getPaperID()).collect(Collectors.toList());
    }
}
