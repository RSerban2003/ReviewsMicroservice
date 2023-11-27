package nl.tudelft.sem.template.example.reponses;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.example.domain.Paper;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(scope = Paper.class, generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "ID")
public class PaperAbstract {
    private UUID ID;
    private String title;
    private String theAbstract;
}
