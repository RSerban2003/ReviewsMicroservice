package nl.tudelft.sem.v20232024.team08b.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BidsControllerTests {
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // Use mockito to mock the parameters when creating the controller
        //this.mockMvc = MockMvcBuilders.standaloneSetup(new BidsController()).build();
    }
}
