package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Includes the title and abstract of a paper for use during the Bidding process")
public class PaperSummary {
    @Schema(description = "The title of the paper", example = "Tails of frogs")
    private String title;

    @Schema(description = "The abstract of the paper", example = "Some sample abstract")
    private String abstractSection;
}
