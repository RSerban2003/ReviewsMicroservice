package nl.tudelft.sem.template.example.domain;

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
