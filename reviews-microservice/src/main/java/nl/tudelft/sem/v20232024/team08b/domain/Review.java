package nl.tudelft.sem.v20232024.team08b.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review implements Serializable {
    @Id
    private ReviewID reviewID;

    private ConfidenceScore confidenceScore;

    private String commentForAuthor;

    private RecommendationScore recommendationScore;

    private String commentForReviewers;

    @ElementCollection
    private List<Comment> confidentialComments;

    /**
     * Constructs an empty review given paperID and reviewID of
     * that review.
     *
     * @param paperID the paper that is reviewed
     * @param reviewerID the ID of the reviewer
     */
    public Review(Long paperID, Long reviewerID) {
        this.reviewID = new ReviewID(paperID, reviewerID);
        this.confidentialComments = new ArrayList<>();
    }

    /**
     * A constructor that parses the DTO into the domain object.
     *
     * @param reviewDTO the review data transfer object
     * @param reviewID the ID of this review
     */
    public Review(nl.tudelft.sem.v20232024.team08b.dtos.review.Review reviewDTO,
                  ReviewID reviewID) {
        this.reviewID = reviewID;
        this.confidenceScore = reviewDTO.getConfidenceScore();
        this.commentForAuthor = reviewDTO.getCommentForAuthor();
        this.recommendationScore = reviewDTO.getRecommendationScore();
        this.commentForReviewers = reviewDTO.getConfidentialComment();
        this.confidentialComments = new ArrayList<>();
    }
}
