package nl.tudelft.sem.v20232024.team08b.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackID implements Serializable {
    private Long conferenceID;
    @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
    private Long trackID;
}
