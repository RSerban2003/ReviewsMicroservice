package nl.tudelft.sem.template.example.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Date;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@JsonIdentityInfo(scope = Track.class, generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Track implements Serializable {
  enum Phase{
    Submitting,
    Bidding,
    Assigning,
    Reviewing,
    Final
  }
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Basic(optional = false)
  private Date biddingDeadLine;
  @Basic
  private Phase currentPhase;

  @SuppressWarnings("unused")
  public Track() {

  }

  public Track(Date biddingDeadLine) {
    this.biddingDeadLine = biddingDeadLine;
    currentPhase = Phase.Submitting;
  }

  public void setCurrentPhase(Phase phase) {
    this.currentPhase = phase;
  }


}