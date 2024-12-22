package com.example.pi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pi.Models.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {


}
