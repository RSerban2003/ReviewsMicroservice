package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Includes the title, abstract and ID of a paper")
public class PaperSummaryWithID {
    @Schema(description = "The ID of the paper", example = "2")
    private Long paperID;

    @Schema(description = "The title of the paper", example = "Tails of frogs")
    private String title;

    @Schema(description = "The abstract of the paper", example = "Some sample abstract")
    private String abstractSection;
}
