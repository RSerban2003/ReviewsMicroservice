package nl.tudelft.sem.v20232024.team08b.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.v20232024.team08b.dtos.DiscussionComment;

@Getter
@Setter
public class Review {

    private ConfidenceScore confidenceScore;
    private String commentForAuthor;
    private RecommendationScore recommendationScore;
    private List<DiscussionComment> commentForReviewers;
}
