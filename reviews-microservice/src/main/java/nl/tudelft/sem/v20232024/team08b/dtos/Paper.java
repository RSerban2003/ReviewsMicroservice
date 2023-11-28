package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "The full contents of a submitted paper. Doesn't include the author names.")
public class Paper {
    @Schema(description = "The title of the paper", example = "Tails of frogs")
    private String title;

    @Schema(description = "The abstract of the paper", example = "Some sample abstract")
    private String abstractSection;

    @Schema(description = "Main keywords of the paper", example = "[Animals]")
    private List<String> keywords;

    @Schema(description = "The paper itself", example = "Full text of the paper")
    private String mainText;

    @Schema(description = "The link to replication package", example = "https://localhost/paper")
    private String replicationPackageLink;
}
