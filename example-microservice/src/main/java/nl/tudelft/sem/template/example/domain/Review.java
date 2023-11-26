package nl.tudelft.sem.template.example.domain;

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
