package nl.tudelft.sem.v20232024.team08b.domain;


import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Data
public class Bid implements Serializable {

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private Paper paper;
    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private User bidder;

    public Bid() {}

}
