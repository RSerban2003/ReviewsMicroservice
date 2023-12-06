package nl.tudelft.sem.v20232024.team08b.domain;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
public class Bid {

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private Paper paperID;
    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    private User userID;

    public Bid(Paper paper, User user) {
        paperID = paper;
        userID = user;
    }

    public Bid() {}

    public Paper getPaperID() {
        return paperID;
    }

    public void setPaperID(Paper paperID) {
        this.paperID = paperID;
    }

    public User getUserID() {
        return userID;
    }

    public void setUserID(User userID) {
        this.userID = userID;
    }
}
