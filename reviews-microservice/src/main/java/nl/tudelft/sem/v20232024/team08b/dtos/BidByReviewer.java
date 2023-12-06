package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Schema(description = "The bid that a reviewer has made")
public class BidByReviewer {

    @Schema(description = "The ID of the bidder", example = "1")
    private Long bidderID;

    @Schema(description = "The bid itself")
    private Bid bid;
}
