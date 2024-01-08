package nl.tudelft.sem.v20232024.team08b.dtos.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;

@Getter
@Setter
@NoArgsConstructor
@Data
@Schema(description = "Includes the title and abstract of a paper for use during the Bidding process")
public class PaperSummary {
    @Schema(description = "The title of the paper", example = "Tails of frogs")
    private String title;

    @Schema(description = "The abstract of the paper", example = "Some sample abstract")
    private String abstractSection;

    /**
     * Constructs a PaperSummary object from a given Submission.
     *
     * @param submission The Submission object containing the paper's title and abstract.
     */
    public PaperSummary(Submission submission) {
        this.title = submission.getTitle();
        this.abstractSection = submission.getAbstract();
    }
}
