package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Specifies a preference by the reviewer to review / not review a paper " +
    "(based on their expertise). By default, every reviewer is NEUTRAL towards a paper.")
public enum Bid {
    CANREVIEW,
    NEUTRAL,
    NOTREVIEW
}
