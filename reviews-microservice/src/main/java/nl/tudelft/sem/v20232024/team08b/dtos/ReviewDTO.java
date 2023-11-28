package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.v20232024.team08b.domain.ConfidenceScore;
import nl.tudelft.sem.v20232024.team08b.domain.RecommendationScore;

@Getter
@Setter
@Schema(description = "The review of a paper. It does not contain the confidential comments")
public class ReviewDTO {
    @Schema(description = "The confidence score of the reviewer", example = "EXPERT")
    private ConfidenceScore confidenceScore;

    @Schema(description = "The comment of the author", example = "Good job!")
    private String commentForAuthor;

    @Schema(description = "Then recommendation score for the paper")
    private RecommendationScore recommendationScore;
}
