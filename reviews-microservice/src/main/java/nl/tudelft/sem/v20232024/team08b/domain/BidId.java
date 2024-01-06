package nl.tudelft.sem.v20232024.team08b.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class BidId implements Serializable {
    private final Long paperID;
    private final Long bidderID;
}
