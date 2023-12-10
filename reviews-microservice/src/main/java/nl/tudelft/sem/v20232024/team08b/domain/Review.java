package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.v20232024.team08b.dtos.DiscussionComment;

@Entity
@Data
public class Review implements Serializable {

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private Paper paper;
    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;
    private ConfidenceScore confidenceScore;
    private String commentForAuthor;
    private RecommendationScore recommendationScore;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> commentForReviewers;

    /**
     * Constructor for review.
     *
     * @param paper paper to which the review belongs to
     * @param user user that wrote the review
     * @param confidenceScore confidence score of a review
     * @param commentForAuthor comment for the author
     * @param recommendationScore recommendation score of review
     * @param commentForReviewers comment for reviewers
     */
    public Review(Paper paper, User user, ConfidenceScore confidenceScore, String commentForAuthor,
                  RecommendationScore recommendationScore, List<Comment> commentForReviewers) {
        this.paper = paper;
        this.user = user;
        this.confidenceScore = confidenceScore;
        this.commentForAuthor = commentForAuthor;
        this.recommendationScore = recommendationScore;
        this.commentForReviewers = commentForReviewers;
    }

    public Review(){}
}
