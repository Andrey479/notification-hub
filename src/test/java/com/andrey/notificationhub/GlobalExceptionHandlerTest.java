package com.andrey.notificationhub;

import com.andrey.notificationhub.controller.EnrichmentController;
import com.andrey.notificationhub.exception.BusinessException;
import com.andrey.notificationhub.exception.ResourceNotFoundException;
import com.andrey.notificationhub.service.BookEnrichmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnrichmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookEnrichmentService bookEnrichmentService;

    @Test
    void shouldThrowResourceNotFoundException() throws Exception {
        when(bookEnrichmentService.enrichBook(1L)).thenThrow(new ResourceNotFoundException(""));
        mockMvc.perform(post("/api/books/1/enrich")).andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowBusinessException() throws Exception {
        when(bookEnrichmentService.enrichBook(1L)).thenThrow(new BusinessException(""));
        mockMvc.perform(post("/api/books/1/enrich")).andExpect(status().isUnprocessableContent());
    }

    @Test
    void shouldThrowGenericException() throws Exception {
        when(bookEnrichmentService.enrichBook(1L)).thenThrow(new RuntimeException("dados sensíveis do banco: senha123"));
        mockMvc.perform(post("/api/books/1/enrich"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro interno no servidor."));
    }
}
