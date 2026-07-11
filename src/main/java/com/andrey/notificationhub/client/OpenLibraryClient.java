package com.andrey.notificationhub.client;

import com.andrey.notificationhub.dto.BookEditionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class OpenLibraryClient {

    private final RestClient restClient;

    public OpenLibraryClient(RestClient.Builder builder, @Value("${openlibrary.api.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public Optional<BookEditionDTO> findBookByIsbn(String isbn) {
        try {
            BookEditionDTO book = restClient
                    .get()
                    .uri("/isbn/{isbn}.json", isbn)
                    .retrieve()
                    .body(BookEditionDTO.class);
            return Optional.of(book);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }

//        throw new UnsupportedOperationException("not implemented yet");
    }
}