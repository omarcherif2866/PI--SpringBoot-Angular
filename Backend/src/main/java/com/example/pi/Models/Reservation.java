package com.example.pi.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @JoinColumn(nullable = true)
    String lieu;
    @JoinColumn(nullable = true)
    LocalDateTime  dateDeb;
    @JoinColumn(nullable = true)
    LocalDateTime  dateFin;
    @JoinColumn(nullable = true)
    float budget;

    // @JsonManagedReference
    // @ManyToOne
    // @JoinColumn(name = "activite_id")
    // Activite activiteId;

    @ManyToOne
    Activite activiteId;

    @ManyToOne
    Club clubId;

    @ManyToOne
    Evenement evenementId;

}