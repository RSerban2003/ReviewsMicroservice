package nl.tudelft.sem.v20232024.team08b.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidID implements Serializable {
    private Long paperID;
    private Long bidderID;
}
