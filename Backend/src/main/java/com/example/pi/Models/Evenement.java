package com.example.pi.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String nom;
    String description;
    int capacite;

    @OneToMany(mappedBy = "evenementId")
    @JsonIgnore
    Set<Reservation> ListReservations;

    @ManyToMany(mappedBy = "ListEvenements")
    @JsonIgnore
    Set<User> users;

    @OneToMany(mappedBy = "evenementId")
    @JsonIgnore
    Set<Club> ListClub;
}
