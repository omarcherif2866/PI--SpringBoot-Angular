package com.example.pi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pi.Models.Covoiturage;

public interface CovoiturageRepository extends JpaRepository<Covoiturage, Long> {
}
