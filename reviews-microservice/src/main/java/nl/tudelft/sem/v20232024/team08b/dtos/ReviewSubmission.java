package nl.tudelft.sem.v20232024.team08b.dtos;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReviewSubmission {


    enum Score {
        EXPERT,
        KNOWLEDGEABLE,
        BASIC;
    }
    private Score confidenceScore;
    private String commentForAuthor;
    private int recommendationScore;
}
