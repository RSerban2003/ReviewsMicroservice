package nl.tudelft.sem.v20232024.team08b.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BidByReviewer implements Serializable {
    private Long bidderID;
    private Bid bid;
}
