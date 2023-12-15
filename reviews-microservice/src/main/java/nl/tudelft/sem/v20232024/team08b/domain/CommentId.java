package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import lombok.Data;

@Data
@Embeddable
public class CommentId implements Serializable {
    @ManyToOne(cascade = CascadeType.ALL)
    private User author;
}
