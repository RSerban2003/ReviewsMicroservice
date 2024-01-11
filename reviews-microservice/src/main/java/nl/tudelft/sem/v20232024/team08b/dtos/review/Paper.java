package nl.tudelft.sem.v20232024.team08b.dtos.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Data
@Schema(description = "The full contents of a submitted paper. Doesn't include the author names.")
public class Paper {
    @Schema(description = "The title of the paper", example = "Tails of frogs")
    private String title;

    @Schema(description = "The abstract of the paper", example = "Some sample abstract")
    private String abstractSection;

    @Schema(description = "Main keywords of the paper")
    private List<String> keywords;

    @Schema(description = "The paper itself", example = "Full text of the paper")
    private String mainText;

    @Schema(description = "The link to replication package", example = "https://localhost/paper")
    private String replicationPackageLink;

    /**
     * Constructs a Paper object from a given Submission.
     *
     * @param submission The Submission object containing the paper's details.
     */
    public Paper(Submission submission) {
        this.title = submission.getTitle();
        this.keywords = submission.getKeywords();
        this.abstractSection = submission.getAbstract();
        this.mainText = new String(submission.getPaper());
    }
}
