package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.v20232024.team08b.domain.ConfidenceScore;
import nl.tudelft.sem.v20232024.team08b.domain.ConfidentialComment;
import nl.tudelft.sem.v20232024.team08b.domain.RecommendationScore;

import java.util.*;
@Getter
@Setter
@Schema(description = "The review of a paper")
public class ReviewDTO {
    @Schema(description = "The confidence score of the reviewer", example = "EXPERT")
    private ConfidenceScore confidenceScore;

    @Schema(description = "The comment of the author", example = "Good job!")
    private String commentForAuthor;

    @Schema(description = "Then recommendation score for the paper")
    private RecommendationScore recommendationScore;

    @Schema(description = "A list of confidential comments for the reviewers. " +
            "It will be empty if the requester is not allowed to see them")
    private Optional<List<ConfidentialComment> > commentForReviewers;
}
