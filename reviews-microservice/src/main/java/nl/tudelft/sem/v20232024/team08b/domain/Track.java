package nl.tudelft.sem.v20232024.team08b.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private TrackID trackID;

    @Basic(optional = false)
    private Date biddingDeadline;

    @Basic(optional = false)
    private Boolean reviewersHaveBeenFinalized;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Paper> papers;
}
