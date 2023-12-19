package nl.tudelft.sem.v20232024.team08b.utils;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class HttpRequestSender {
    HttpClient httpClient;

    /**
     * Default constructor.
     */
    public HttpRequestSender() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Constructor used for testing purposes.
     *
     * @param httpClient httpClient to be injected
     */
    public HttpRequestSender(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Method that performs a GET request to a given endpoint.
     *
     * @param url the URL of the endpoint
     * @return the response in JSON format
     */
    public String sendGetRequest(String url) throws NotFoundException {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            HttpStatus status = HttpStatus.valueOf(response.statusCode());
            System.out.println("------------hera------------");

            switch (status) {
                case OK, CREATED:
                    System.out.println("------------herb------------");

                    return (String) response.body();
                case NOT_FOUND:
                    System.out.println("------------herc------------");

                    throw new NotFoundException("404, not found");
                default:
                    System.out.println("------------her------------");
                    throw new RuntimeException("Failed to parse status.");
            }
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException("GET request failed");
        }
    }
}
