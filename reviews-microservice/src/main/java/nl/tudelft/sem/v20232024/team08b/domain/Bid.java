package nl.tudelft.sem.v20232024.team08b.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity
@Data
@IdClass(BidID.class)
@AllArgsConstructor
@NoArgsConstructor
public class Bid implements Serializable {
    @Id
    private Long paperID;

    @Id
    private Long bidderID;

    @SuppressWarnings("PMD")
    private nl.tudelft.sem.v20232024.team08b.dtos.review.Bid bid;
}
