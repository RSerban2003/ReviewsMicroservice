package nl.tudelft.sem.v20232024.team08b.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import nl.tudelft.sem.v20232024.team08b.controllers.PapersController;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Paper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PapersControllerTests {
    MockMvc mockMvc;

    private PapersService paperService = Mockito.mock(PapersService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Paper fakePaper;
    private Paper fakeTitleAndAbstract;
    private Long requesterID;
    private Long paperID;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                new PapersController(paperService)
        ).build();
        fakePaper = new Paper();
        fakePaper.setTitle("title");
        fakePaper.setAbstractSection("abstract");
        fakePaper.setKeywords(Arrays.asList("keyword1","keyword2"));
        fakePaper.setMainText("text");

        fakeTitleAndAbstract = new Paper();
        fakeTitleAndAbstract.setTitle("title");
        fakeTitleAndAbstract.setAbstractSection("abstract");

        requesterID = 1L;
        paperID = 2L;
    }

    @Test
    public void getPaperSuccessfully() throws Exception {

        when(paperService.getPaper(requesterID, paperID)).thenReturn(fakePaper);

        String expectedJSON = objectMapper.writeValueAsString(fakePaper);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/papers/{paperID}", paperID.toString())
                                .param("requesterID", requesterID.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().json(expectedJSON));

        verify(paperService).getPaper(requesterID, paperID);
    }

    @Test
    public void getTitleAndAbstractSuccessfully() throws Exception {

        when(paperService.getPaper(requesterID, paperID)).thenReturn(fakeTitleAndAbstract);

        String expectedJSON = objectMapper.writeValueAsString(fakeTitleAndAbstract);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/papers/{paperID}/title-and-abstract", paperID.toString())
                                .param("requesterID", requesterID.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().json(expectedJSON));

        verify(paperService).getTitleAndAbstract(requesterID, paperID);
    }
}
