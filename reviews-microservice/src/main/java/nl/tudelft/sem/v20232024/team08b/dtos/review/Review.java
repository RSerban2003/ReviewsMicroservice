package nl.tudelft.sem.v20232024.team08b.dtos.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import nl.tudelft.sem.v20232024.team08b.domain.ConfidenceScore;
import nl.tudelft.sem.v20232024.team08b.domain.RecommendationScore;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "The review of a paper. It does not contain the confidential comments")
public class Review {
    @Schema(description = "The confidence score of the reviewer", example = "EXPERT")
    private ConfidenceScore confidenceScore;

    @Schema(description = "The comment of the author", example = "Good job!")
    private String commentForAuthor;

    @Schema(description = "Confidential comments for others involved in the review process. " +
        "Will be null if sent to an author.")
    private String confidentialComment;

    @Schema(description = "Then recommendation score for the paper")
    private RecommendationScore recommendationScore;
}
