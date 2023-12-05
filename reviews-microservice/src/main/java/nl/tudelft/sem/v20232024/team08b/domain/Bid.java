package nl.tudelft.sem.v20232024.team08b.domain;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Bid {
    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private Paper paperID;
    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private User userID;
}
