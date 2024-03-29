package nl.tudelft.sem.v20232024.team08b.dtos.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Schema(description = "The bid that a reviewer has made")
public class BidByReviewer {

    @Schema(description = "The ID of the bidder", example = "1")
    private Long bidderID;

    @Schema(description = "The bid itself")
    private Bid bid;
}
