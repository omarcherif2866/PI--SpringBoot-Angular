package com.example.pi.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Covoiturage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String nom;
    LocalDate date;
    String destination;
    String depart;
    String tel;
    @Enumerated(EnumType.STRING)
    Voiture voiture ;

    @ManyToOne
    User userId;


}
