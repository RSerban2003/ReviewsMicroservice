package nl.tudelft.sem.v20232024.team08b.dtos.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Counts of accepted, rejected and unknown papers")
public class TrackAnalytics {
    @Schema(description = "Number of accepted papers", example = "0")
    private int accepted;

    @Schema(description = "Number of rejected papers", example = "1")
    private int rejected;

    @Schema(description = "Number of papers that are not yet decided", example = "2")
    private int unknown;
}
