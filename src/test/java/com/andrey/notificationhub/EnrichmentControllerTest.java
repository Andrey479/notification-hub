package com.andrey.notificationhub;

import com.andrey.notificationhub.controller.EnrichmentController;
import com.andrey.notificationhub.dto.BookEnrichmentResponseDTO;
import com.andrey.notificationhub.exception.ResourceNotFoundException;
import com.andrey.notificationhub.service.BookEnrichmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnrichmentController.class)
public class EnrichmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookEnrichmentService bookEnrichmentService;

    @Test
    void shouldProperlyEnrichTheBook() throws Exception {
        BookEnrichmentResponseDTO bookEnrichmentResponseDTO = new BookEnrichmentResponseDTO(
                1L,
                "1234567890",
                "coverUrl",
                "RandomSynopses",
                500
        );

        when(bookEnrichmentService.enrichBook(1L)).thenReturn(bookEnrichmentResponseDTO);

        mockMvc.perform(post("/api/books/1/enrich"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.isbn").value("1234567890"));
    }

    @Test
    void shouldReturnResourceNotFoundError() throws Exception {
        when(bookEnrichmentService.enrichBook(1L)).thenThrow(new ResourceNotFoundException("Book not found"));

        mockMvc.perform(post("/api/books/1/enrich")).andExpect(status().isNotFound());
    }
}
