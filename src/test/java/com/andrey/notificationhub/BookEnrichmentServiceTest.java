package com.andrey.notificationhub;

import com.andrey.notificationhub.client.OpenLibraryClient;
import com.andrey.notificationhub.dto.BookEditionDTO;
import com.andrey.notificationhub.dto.BookEnrichmentResponseDTO;
import com.andrey.notificationhub.model.Book;
import com.andrey.notificationhub.repository.BookRepository;
import com.andrey.notificationhub.service.BookEnrichmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
        editionDTO.setTitle("Clean Code: A Handbook of Agile Software Craftsmanship");
        editionDTO.setCovers(List.of(1));
        editionDTO.setNumberOfPages(431);
        editionDTO.setWorks(List.of(new BookEditionDTO.WorkKeyDTO("random key")));
        String synopses = "random description";

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(client.findBookByIsbn("0132350882")).thenReturn(Optional.of(editionDTO));
        when(client.findBookByKey("random key")).thenReturn(Optional.of(synopses));
        when(repository.save(any(Book.class))).thenReturn(book);

        //act
        BookEnrichmentResponseDTO response = service.enrichBook(1L);

        //assert
        assertEquals(1L, response.getId());
        assertEquals("0132350882", response.getIsbn());
        assertEquals("random description", response.getSynopsis());
        assertEquals("https://covers.openlibrary.org/b/id/1-M.jpg", response.getCoverUrl());
        assertEquals(431,response.getPageCount());
        verify(repository).save(any(Book.class));
    }
}
