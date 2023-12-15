package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Data
@IdClass(ReviewId.class)
public class Review implements Serializable {

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private Paper paper;
    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private User reviewer;
    private ConfidenceScore confidenceScore;
    private String commentForAuthor;
    private RecommendationScore recommendationScore;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> commentForReviewers;


    public Review(){}
}
