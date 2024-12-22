package com.example.pi.Repositories;

import com.example.pi.Models.Activite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ActiviteRepository extends JpaRepository<Activite, Long> {

    @Query("SELECT a.nom, COUNT(u) FROM Activite a JOIN a.users u GROUP BY a.nom")
    List<Object[]> getActivityParticipationCount();

    @Query("SELECT a.nom, COUNT(u) FROM Activite a JOIN a.users u GROUP BY a.nom ORDER BY COUNT(u) DESC")
    List<Object[]> getMostPopularActivity(Pageable pageable); // Limiter les résultats à 1


    @Query("SELECT a.nom, COUNT(s) FROM Activite a JOIN a.listSessions s GROUP BY a.nom")
    List<Object[]> getActivitySessionsCount();

    @Query("SELECT a.nom, SUM(r.budget) FROM Activite a JOIN a.listReservations r GROUP BY a.nom")
    List<Object[]> getTotalBudgetPerActivity();
}
