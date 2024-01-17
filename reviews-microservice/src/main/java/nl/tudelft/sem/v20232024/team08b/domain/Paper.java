package nl.tudelft.sem.v20232024.team08b.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperStatus;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paper implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Track track;

    private PaperStatus status = PaperStatus.NOT_DECIDED;

    private Boolean reviewsHaveBeenFinalized = false;
}
