package nl.tudelft.sem.v20232024.team08b;

import nl.tudelft.sem.v20232024.team08b.controllers.AssignmentsController;
import nl.tudelft.sem.v20232024.team08b.controllers.PapersController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class Tests {

    MockMvc mockMvc;
    // Mockito has to be used here. This is only an example

//    @BeforeEach
//    void setup() {
////        this.mockMvc = MockMvcBuilders.standaloneSetup(new AssignmentsController()).build();
//    }
//
//    @Test
//    public void testGetEndpoints() throws Exception {
//        String paperID = "123";
//        mockMvc.perform(MockMvcRequestBuilders.get("/papers/{paperID}/assignees", paperID)
//                .param("requesterID", "456")) // Add requesterID as a query parameter
//            .andExpect(MockMvcResultMatchers.status().isNotImplemented());
//    }
//
//    @Test
//    public void testPostEndpoints() throws Exception {
//        String paperID = "123";
//        String reviewerID = "123";
//        mockMvc.perform(MockMvcRequestBuilders
//                .post("/papers/{paperID}/assignees/{reviewerID}", paperID, reviewerID)
//                .param("requesterID", "456")) // Add requesterID as a query parameter
//            .andExpect(MockMvcResultMatchers.status().isNotImplemented());
//    }
//
//    @Test
//    public void remove() throws Exception {
//        String paperID = "123";
//        String reviewerID = "125";
//
//        mockMvc.perform(MockMvcRequestBuilders
//                .delete("/papers/{paperID}/assignees/{reviewerID}", paperID, reviewerID)
//                .param("requesterID", "456")  // Add requesterID as a query parameter
//                .contentType(MediaType.APPLICATION_JSON))  // Set content type to JSON
//            .andExpect(MockMvcResultMatchers.status().isNotImplemented());
//    }
//
//    @Test
//    public void put() throws Exception {
//        String conferenceID = "125";
//        String trackID = "126";
//        mockMvc.perform(MockMvcRequestBuilders
//                .put("/conferences/{conferenceID}/tracks/{trackID}/automatic", conferenceID, trackID)
//                .param("requesterID", "456"))  // Add requesterID as a query parameter
//            .andExpect(MockMvcResultMatchers.status().isNotImplemented());
//    }
}