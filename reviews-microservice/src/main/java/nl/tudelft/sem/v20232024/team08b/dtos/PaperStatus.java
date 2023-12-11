package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The review status of the paper.\n\n" +
        "ACCEPTED: The paper has been accepted.\n\n" +
        "REJECTED: The paper has been rejected.\n\n" +
        "NOT_DECIDED: The review process has hot finished yet.")
public enum PaperStatus {
    ACCEPTED,
    REJECTED,
    NOT_DECIDED
}
