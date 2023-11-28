package nl.tudelft.sem.template.example.reponses;

import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.example.domain.ConfidentialComment;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ReviewSubmission {

    enum Score {
        EXPERT,
        KNOWLEDGEABLE,
        BASIC
    }
    private Score confidenceScore;
    private String commentForAuthor;
    private int recommendationScore;
    private List<ConfidentialComment> commentForReviewers;
    private UUID submitterID;
}
