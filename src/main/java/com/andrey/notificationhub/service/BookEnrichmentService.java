package com.andrey.notificationhub.service;

import com.andrey.notificationhub.client.OpenLibraryClient;
import com.andrey.notificationhub.dto.BookEditionDTO;
import com.andrey.notificationhub.dto.BookEnrichmentResponseDTO;
import com.andrey.notificationhub.exception.ResourceNotFoundException;
import com.andrey.notificationhub.model.Book;
import com.andrey.notificationhub.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookEnrichmentService {

    private final OpenLibraryClient client;
    private final BookRepository repository;

    public BookEnrichmentResponseDTO enrichBook(Long id){
        return generateBookEnrichmentResponseDTO(enrichAndPersist(id));
    }

    private Book enrichAndPersist(Long id){
        Book book = getTheBookIfItExists(id);
        // Construtores começam aqui porquê se o BookEdition não tiver info ele não sobrescreve o book
        BookEditionDTO bookEditionDTO;

        try {
            Optional<BookEditionDTO> optionalBookEditionDTO = client.findBookByIsbn(book.getIsbn());
            if (optionalBookEditionDTO.isPresent()){
                bookEditionDTO = optionalBookEditionDTO.get();
                addCoverUrl(book, bookEditionDTO);
                addPageCount(book, bookEditionDTO);
                addSynopses(book, bookEditionDTO);
            }
        } catch (ResourceAccessException e){
            log.warn("Falha ao consultar Open Library para o livro id={}: {}", id, e.getMessage());
        }


        return repository.save(book);
    }

    private void addCoverUrl(Book book, BookEditionDTO bookEditionDTO){
        if (book.getCoverUrl() == null){
            if (bookEditionDTO.getCovers() != null){
                if (!bookEditionDTO.getCovers().isEmpty()){
                    book.setCoverUrl("https://covers.openlibrary.org/b/id/"+bookEditionDTO.getCovers().getFirst()+"-M.jpg");
                }
            }
        }
    }

    private void addPageCount(Book book, BookEditionDTO bookEditionDTO){
        if (book.getPageCount() == null){
            book.setPageCount(bookEditionDTO.getNumberOfPages());
        }
    }

    private void addSynopses(Book book, BookEditionDTO bookEditionDTO){
        if (bookEditionDTO.getWorks() != null){
            if (!bookEditionDTO.getWorks().isEmpty()){
                Optional<String> optionalSynopses = client.findBookByKey(bookEditionDTO.getWorks().getFirst().getKey());
                if (optionalSynopses.isPresent() && book.getSynopsis() == null){
                    book.setSynopsis(optionalSynopses.get());
                }
            }
        }
    }

    private Book getTheBookIfItExists(Long id){
        Optional<Book> optionalBook = repository.findById(id);
        if (optionalBook.isEmpty()) {
            throw new ResourceNotFoundException("Book not found");
        }
        return optionalBook.get();
    }

    private BookEnrichmentResponseDTO generateBookEnrichmentResponseDTO(Book book){
        BookEnrichmentResponseDTO responseDTO = new BookEnrichmentResponseDTO();
        responseDTO.setId(book.getId());
        responseDTO.setCoverUrl(book.getCoverUrl());
        responseDTO.setIsbn(book.getIsbn());
        responseDTO.setPageCount(book.getPageCount());
        responseDTO.setSynopsis(book.getSynopsis());
        return responseDTO;
    }
}
