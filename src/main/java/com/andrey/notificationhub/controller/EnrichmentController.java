package com.andrey.notificationhub.controller;

import com.andrey.notificationhub.dto.BookEnrichmentResponseDTO;
import com.andrey.notificationhub.service.BookEnrichmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class EnrichmentController {
    private final BookEnrichmentService bookEnrichmentService;

    @PostMapping("/{id}/enrich")
    public ResponseEntity<BookEnrichmentResponseDTO> enrichBook(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(bookEnrichmentService.enrichBook(id));
    }
}
