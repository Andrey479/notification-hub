package com.andrey.notificationhub.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate expectedReturnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;
}
