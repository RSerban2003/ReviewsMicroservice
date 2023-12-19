package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.IdClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@IdClass(CommentID.class)
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {
    @EmbeddedId
    private CommentID author;

    private String text;
}
