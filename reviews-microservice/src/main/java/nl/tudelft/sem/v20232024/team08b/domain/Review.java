package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Data
@AllArgsConstructor
public class Review implements Serializable {
    @Id
    private ReviewId reviewId;

    private ConfidenceScore confidenceScore;

    private String commentForAuthor;

    private RecommendationScore recommendationScore;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> commentForReviewers;

    public Review(){

    }
}
