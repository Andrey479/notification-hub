package com.andrey.notificationhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookEnrichmentResponseDTO {
    private Long id;
    private String isbn;
    private String coverUrl;
    private String synopsis;
    private Integer pageCount;
}
