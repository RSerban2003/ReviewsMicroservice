package nl.tudelft.sem.v20232024.team08b.dtos;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(scope = Paper.class, generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "ID")
public class WholePaper {
    private UUID ID;
    private String title;

    @Schema(description = "abstract", example = "blah\n{\n,\n}")
    private String theAbstract;


    private List<String> keywords;
    private String file;
    private String link;
}
