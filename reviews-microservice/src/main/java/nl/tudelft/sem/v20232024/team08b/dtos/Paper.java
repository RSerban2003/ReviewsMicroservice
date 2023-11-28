package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "The full contents of a submitted paper. Doesn't include the author names.")
public class Paper {
    private PaperSummary paperSummary;
    private List<String> keywords;
    private String pdfLink;
    private String replicationPackageLink;
}
