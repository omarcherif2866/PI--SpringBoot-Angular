package com.example.pi.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String nom;
    String description;
    Boolean status;
    String objectif;
    LocalDate dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDate.now();
    }

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    @OneToMany(mappedBy = "clubId")
    @JsonIgnore
    Set<Reservation> ListReservations;

    @ManyToMany(mappedBy = "listClubs")
    @JsonIgnore
    Set<User> users;

    @ManyToOne
    Evenement evenementId;
}
