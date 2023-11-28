package nl.tudelft.sem.v20232024.team08b.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Review {

    private ConfidenceScore confidenceScore;
    private String commentForAuthor;
    private RecommendationScore recommendationScore;
    private List<ConfidentialComment> commentForReviewers;
}
