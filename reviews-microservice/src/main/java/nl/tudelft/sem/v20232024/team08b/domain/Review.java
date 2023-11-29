package nl.tudelft.sem.v20232024.team08b.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Review {

    enum Score {
        EXPERT,
        KNOWLEDGEABLE,
        BASIC
    }
    private Score confidenceScore;
    private String commentForAuthor;
    private int recommendationScore;
    private List<ConfidentialComment> commentForReviewers;
}
