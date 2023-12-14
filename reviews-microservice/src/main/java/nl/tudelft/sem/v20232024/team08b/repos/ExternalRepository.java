package nl.tudelft.sem.v20232024.team08b.repos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Review;
import org.springframework.stereotype.Repository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Repository
public class ExternalRepository {
    private ObjectMapper objectMapper;
    private HttpClient httpClient;

    /**
     * Empty constructor.
     */
    public ExternalRepository() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Method that performs a GET request to a given endpoint.
     *
     * @param url the URL of the endpoint
     * @return the response in JSON format
     */
    private String sendGetRequest(String url) {
        HttpRequest request;
        String response;
        try {
            request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();
            response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    /**
     * Sample method that gets and Event from submissions microservice.
     *
     * @return Event object gotten from other microservice.
     */
    public Review getEvent() {
        String response = sendGetRequest("https://29889bc5-e017-4c7b-8e69-e567dcd4556d.mock.pstmn.io/review/1/1");
        Review review;
        try {
            review = objectMapper.readValue(response, Review.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse the HTTP response");
        }
        return review;
    }
}
