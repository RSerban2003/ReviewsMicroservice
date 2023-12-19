package nl.tudelft.sem.v20232024.team08b.util;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.utils.HttpRequestSender;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRequestSenderTests {
    private HttpClient httpClient = Mockito.mock(HttpClient.class);
    private HttpRequestSender httpRequestSender = new HttpRequestSender(httpClient);

    @Test
    void simpleIOException1() throws IOException, InterruptedException {
        String url = "http://fake.url";
        when(httpClient.send(any(), any())).thenThrow(new IOException());
        assertThrows(RuntimeException.class, () -> httpRequestSender.sendGetRequest(url));
    }

    @Test
    void simpleOIException2() throws IOException, InterruptedException {
        String url = "http://fake.url";
        InterruptedException fakeInterruptedException = mock(InterruptedException.class);
        when(httpClient.send(any(), any())).thenThrow(fakeInterruptedException);
        assertThrows(RuntimeException.class, () -> httpRequestSender.sendGetRequest(url));
    }

    @Test
    void statusOK() throws NotFoundException, IOException, InterruptedException {
        String url = "http://fake.url";
        HttpResponse fakeResponse = Mockito.mock(HttpResponse.class);
        when(fakeResponse.body()).thenReturn("fake body");
        when(fakeResponse.statusCode()).thenReturn(200);
        when(httpClient.send(any(), any())).thenReturn(fakeResponse);
        assertThat(httpRequestSender.sendGetRequest(url)).isEqualTo("fake body");
    }

    @Test
    void statusNotFound() throws IOException, InterruptedException {
        String url = "http://fake.url";
        HttpResponse fakeResponse = Mockito.mock(HttpResponse.class);
        when(fakeResponse.statusCode()).thenReturn(404);
        when(httpClient.send(any(), any())).thenReturn(fakeResponse);
        assertThrows(NotFoundException. class, () ->
                httpRequestSender.sendGetRequest(url)
        );
    }

    @Test
    void statusUnknown() throws IOException, InterruptedException {
        String url = "http://fake.url";
        HttpResponse fakeResponse = Mockito.mock(HttpResponse.class);
        when(fakeResponse.statusCode()).thenReturn(102);
        when(httpClient.send(any(), any())).thenReturn(fakeResponse);
        assertThrows(RuntimeException.class, () ->
                httpRequestSender.sendGetRequest(url)
        );
    }
}
