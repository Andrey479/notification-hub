package com.andrey.notificationhub;

import com.andrey.notificationhub.client.OpenLibraryClient;
import com.andrey.notificationhub.dto.BookEditionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.EnableWireMock;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@RestClientTest(OpenLibraryClient.class)
@AutoConfigureMockRestServiceServer(enabled = false)
@EnableWireMock
@ActiveProfiles("test")
class OpenLibraryClientTest {

    @Autowired
    private OpenLibraryClient openLibraryClient;

    @Test
    void shouldFindBookByIsbnSuccessfully() {
        // given
        String isbn = "9780132350884";
        String jsonResponse = """
            {
              "title": "Clean Code: A Handbook of Agile Software Craftsmanship",
              "covers": [6790157],
              "works": [{ "key": "/works/OL18109322W" }],
              "number_of_pages": 464
            }
            """;

        stubFor(get(urlEqualTo("/isbn/" + isbn + ".json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)));

        // when
        Optional<BookEditionDTO> result = openLibraryClient.findBookByIsbn(isbn);

        // then
        assertTrue(result.isPresent());
        assertEquals("Clean Code: A Handbook of Agile Software Craftsmanship", result.get().getTitle());
        assertEquals(464, result.get().getNumberOfPages());
        assertEquals(1, result.get().getCovers().size());
        assertEquals(6790157, result.get().getCovers().getFirst());
        assertEquals("/works/OL18109322W", result.get().getWorks().getFirst().getKey());
    }

    @Test
    void shouldFindBookByKeySuccessfully(){
        // given
        String key = "/works/OL18109322W";
        String jsonResponse = """
            {
              "description": "algum texto"
            }
            """;

        stubFor(get(urlEqualTo(key + ".json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)));

        // when
        Optional<String> result = openLibraryClient.findBookByKey(key);

        //then
        assertTrue(result.isPresent());
        assertEquals("algum texto", result.get());
    }
}