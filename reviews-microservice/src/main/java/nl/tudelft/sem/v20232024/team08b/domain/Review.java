package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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

    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> confidentialComments;

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
