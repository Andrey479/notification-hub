package com.andrey.notificationhub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookEditionDTO {

    private String title;

    private List<Integer> covers;

    private List<WorkKeyDTO> works;

    @JsonProperty("number_of_pages")
    private Integer numberOfPages;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkKeyDTO {
        private String key;
    }
}