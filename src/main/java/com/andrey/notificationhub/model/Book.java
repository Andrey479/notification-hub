package com.andrey.notificationhub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    private Long id;

//    A api apenas lê o isbn sem atualizar ele
    @Column(insertable = false, updatable = false)
    private String isbn;

    private String coverUrl;
    private String synopsis;
    private Integer pageCount;
}
