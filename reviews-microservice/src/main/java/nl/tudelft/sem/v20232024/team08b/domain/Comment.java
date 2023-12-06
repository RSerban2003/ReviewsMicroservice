package nl.tudelft.sem.v20232024.team08b.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Comment {
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
