package com.example.pi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.example.pi.Models.Reservation;


@Repository

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
