package nl.tudelft.sem.v20232024.team08b.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Track implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Basic(optional = false)
    private Date biddingDeadLine;
    @Basic(optional = false)
    private TrackPhase currentPhase;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Paper> papers;

    @ManyToOne(cascade = CascadeType.ALL)
    private Conference conference;

}
