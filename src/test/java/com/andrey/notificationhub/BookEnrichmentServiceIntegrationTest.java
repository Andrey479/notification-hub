package com.andrey.notificationhub;

import com.andrey.notificationhub.dto.BookEnrichmentResponseDTO;
import com.andrey.notificationhub.repository.BookRepository;
import com.andrey.notificationhub.service.BookEnrichmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@EnableWireMock
@ActiveProfiles("test")
public class BookEnrichmentServiceIntegrationTest {

    @Autowired
    private BookEnrichmentService service;

    @Autowired
    private BookRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    //jdbcTemplate foi usado porque repository retorna erro se tentar salvar book

    @Test
    void shouldFallbackGracefullyWhenOpenLibraryTimesOut(){
        jdbcTemplate.update("INSERT INTO books (id, isbn) VALUES (?, ?)", 1L, "0132350882");


        stubFor(get(urlEqualTo("/isbn/0132350882.json"))
                .willReturn(aResponse()
                        .withFixedDelay(10000) // maior que o read-timeout configurado
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));

        BookEnrichmentResponseDTO response = service.enrichBook(1L);

        assertEquals(1L, response.getId());
        assertNull(response.getCoverUrl());
    }
}
