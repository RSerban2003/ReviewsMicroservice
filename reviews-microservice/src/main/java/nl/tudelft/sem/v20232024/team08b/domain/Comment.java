package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
@IdClass(CommentId.class)
public class Comment implements Serializable {
    @EmbeddedId
    private CommentId author;

    private String text;

    public Comment(){}

}
