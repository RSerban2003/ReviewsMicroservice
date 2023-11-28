package nl.tudelft.sem.v20232024.team08b.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIdentityInfo(scope = Track.class, generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
@EqualsAndHashCode
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class Track implements Serializable {


  enum Phase {
    SUBMITTING,
    BIDDING,
    REVIEWING,
    FINAL;
  }
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID trackId;
  @Basic(optional = false)
  private Date biddingDeadLine;

  @Basic(optional = false)
  private Phase currentPhase;
}
