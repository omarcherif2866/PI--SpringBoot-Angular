package com.example.pi.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String nom;
    String description;
    @Enumerated(EnumType.STRING)
    Annimateur annimateur ;
    String image;
    Integer  nbrParticipants;
    //@JsonManagedReference
    //@JsonIgnoreProperties("activiteId")
    @OneToMany(mappedBy = "activiteId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //@JsonIgnore
    private Set<Session> listSessions = new HashSet<>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reservation> listReservations = new HashSet<>();


    @JsonIgnoreProperties("ListActivites")
   @ManyToMany(mappedBy = "ListActivites")
    Set<User> users;



}
