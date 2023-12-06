package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The review status of the paper.")
public enum PaperStatus {
    ACCEPTED,
    REJECTED,
    NOT_DECIDED
}
