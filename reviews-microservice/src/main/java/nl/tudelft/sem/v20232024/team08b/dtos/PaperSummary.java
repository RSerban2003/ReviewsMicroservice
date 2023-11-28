package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Includes the title and abstract of a paper for use during the Bidding process")
public class PaperSummary {
    private String title;
    private String abstractSection;
}
