package com.andrey.notificationhub;

import com.andrey.notificationhub.client.OpenLibraryClient;
import com.andrey.notificationhub.dto.BookEditionDTO;
import com.andrey.notificationhub.model.Book;
import com.andrey.notificationhub.repository.BookRepository;
import com.andrey.notificationhub.service.BookEnrichmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookEnrichmentServiceTest {

    @Mock
    BookRepository repository;

    @Mock
    OpenLibraryClient client;

    @InjectMocks
    BookEnrichmentService service;

    @Test
    void shouldReturnTheEnrichedBookSuccessfully(){
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("0132350882");

        BookEditionDTO editionDTO = new BookEditionDTO();
        workkey = BookEditionDTO.Wo

        when(client.findBookByIsbn("0132350882")).thenReturn(Optional.of(editionDTO));
        when(client.findBookByKey());
    }
}
