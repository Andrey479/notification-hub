package com.andrey.notificationhub.service;

import com.andrey.notificationhub.client.OpenLibraryClient;
import com.andrey.notificationhub.dto.BookEditionDTO;
import com.andrey.notificationhub.dto.BookEnrichmentResponseDTO;
import com.andrey.notificationhub.model.Book;
import com.andrey.notificationhub.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookEnrichmentService {

    private final OpenLibraryClient client;
    private final BookRepository repository;

    public BookEnrichmentResponseDTO enrichBook(Long id){
        Optional<Book> optionalBook = repository.findById(id);
        if (optionalBook.isEmpty()) {
            throw new RuntimeException("Livro não ecnontrado");
        }

        Book book = optionalBook.get();
        // Construtores começam aqui porquê se o BookEdition e a synopse for empty eles começam com null
        BookEditionDTO bookEditionDTO;

        Optional<BookEditionDTO> optionalBookEditionDTO = client.findBookByIsbn(optionalBook.get().getIsbn());
        if (optionalBookEditionDTO.isPresent()){
            bookEditionDTO = optionalBookEditionDTO.get();
            if (book.getCoverUrl() == null){
                if (bookEditionDTO.getCovers() != null){
                    if (!bookEditionDTO.getCovers().isEmpty()){
                        book.setCoverUrl("https://covers.openlibrary.org/b/id/"+bookEditionDTO.getCovers().getFirst()+"-M.jpg");
                    }
                }
            }

            book.setPageCount(bookEditionDTO.getNumberOfPages());

            Optional<String> optionalSynopses = client.findBookByKey(optionalBookEditionDTO.get().getWorks().getFirst().getKey());

            if (optionalSynopses.isPresent() && book.getSynopsis() == null){
                book.setSynopsis(optionalSynopses.get());
            }
        }

        repository.save(book);

        BookEnrichmentResponseDTO responseDTO = new BookEnrichmentResponseDTO();
        responseDTO.setId(book.getId());
        responseDTO.setCoverUrl(book.getCoverUrl());
        responseDTO.setIsbn(book.getIsbn());
        responseDTO.setPageCount(book.getPageCount());
        responseDTO.setSynopsis(book.getSynopsis());
        return responseDTO;
    }
}
