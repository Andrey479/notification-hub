package com.andrey.notificationhub;

import com.andrey.notificationhub.client.OpenLibraryClient;
import com.andrey.notificationhub.dto.BookEditionDTO;
import com.andrey.notificationhub.dto.BookEnrichmentResponseDTO;
import com.andrey.notificationhub.exception.BusinessException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void shouldReturnBookGracefullyWhenApiReturnsNoEnrichmentData(){
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("0132350882");

        BookEditionDTO editionDTO = new BookEditionDTO();

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(client.findBookByIsbn("0132350882")).thenReturn(Optional.of(editionDTO));
        when(repository.save(any(Book.class))).thenReturn(book);

        BookEnrichmentResponseDTO response = service.enrichBook(1L);

        assertEquals(1L, response.getId());
        assertEquals("0132350882", response.getIsbn());
        assertNull(response.getSynopsis());
        assertNull(response.getCoverUrl());
        assertNull(response.getPageCount());
        verify(repository).save(any(Book.class));
    }

    @Test
    void shouldNotOverwriteTheBookDataWhenTheApiReturnsNoData(){
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("0132350882");
        book.setCoverUrl("https://covers.openlibrary.org/b/id/1-M.jpg");
        book.setSynopsis("Random Synopses");
        book.setPageCount(431);

        BookEditionDTO editionDTO = new BookEditionDTO();

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(client.findBookByIsbn("0132350882")).thenReturn(Optional.of(editionDTO));
        when(repository.save(any(Book.class))).thenReturn(book);

        //act
        BookEnrichmentResponseDTO response = service.enrichBook(1L);

        //assert
        assertEquals(1L, response.getId());
        assertEquals("0132350882", response.getIsbn());
        assertEquals("Random Synopses", response.getSynopsis());
        assertEquals("https://covers.openlibrary.org/b/id/1-M.jpg", response.getCoverUrl());
        assertEquals(431,response.getPageCount());
        verify(repository).save(any(Book.class));
    }

    @Test
    void shouldNotOverwriteCoversWhenApiReturnsNullCover(){
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("0132350882");
        book.setCoverUrl("https://covers.openlibrary.org/b/id/1-M.jpg");
        book.setSynopsis("Random Synopses");
        book.setPageCount(431);

        BookEditionDTO editionDTO = new BookEditionDTO();
        editionDTO.setCovers(List.of());

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(client.findBookByIsbn("0132350882")).thenReturn(Optional.of(editionDTO));
        when(repository.save(any(Book.class))).thenReturn(book);

        //act
        BookEnrichmentResponseDTO response = service.enrichBook(1L);

        //assert
        assertEquals(1L, response.getId());
        assertEquals("0132350882", response.getIsbn());
        assertEquals("Random Synopses", response.getSynopsis());
        assertEquals("https://covers.openlibrary.org/b/id/1-M.jpg", response.getCoverUrl());
        assertEquals(431,response.getPageCount());
        verify(repository).save(any(Book.class));
    }

    @Test
    void shouldNotEnrichSynopsisWhenWorksListIsEmpty(){
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("0132350882");
        book.setCoverUrl("https://covers.openlibrary.org/b/id/1-M.jpg");
        book.setSynopsis("Random Synopses");
        book.setPageCount(431);

        BookEditionDTO editionDTO = new BookEditionDTO();
        editionDTO.setWorks(List.of());

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(client.findBookByIsbn("0132350882")).thenReturn(Optional.of(editionDTO));
        when(repository.save(any(Book.class))).thenReturn(book);

        //act
        BookEnrichmentResponseDTO response = service.enrichBook(1L);

        //assert
        assertEquals(1L, response.getId());
        assertEquals("0132350882", response.getIsbn());
        assertEquals("Random Synopses", response.getSynopsis());
        assertEquals("https://covers.openlibrary.org/b/id/1-M.jpg", response.getCoverUrl());
        assertEquals(431,response.getPageCount());
        verify(repository).save(any(Book.class));
    }

    @Test
    void shouldReturnBussinessExceptionWhenBookHaveNoIsbn(){
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("");

        when(repository.findById(1L)).thenReturn(Optional.of(book));

//        act + assert
        assertThrows(BusinessException.class, () -> {
            service.enrichBook(1L);
        });

        verify(client, never()).findBookByIsbn(any());
        verify(repository, never()).save(any());
    }
}
