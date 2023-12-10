package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Comment implements Serializable {
    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private User userID;

    private String text;

    public Comment(User userID, String text) {
        this.userID = userID;
        this.text = text;
    }

    public Comment(){}

}
