package com.example.pi.Services;

import com.example.pi.Models.Activite;
import com.example.pi.Models.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IActiviteService {

    public Activite addActivite(Activite activite);

    public void deleteActivite(Long id);

    Activite updateActivite(Long id, Activite activite);

    public Activite getActiviteById (Long id);

    public Set<Activite> getAllActivites ();

    Activite participateInActivity(Long userId, Long activiteId);

    List<Object[]> getActivityParticipationCount();

    List<Object[]> getMostPopularActivity();

    List<Object[]> getActivitySessionsCount();

    long countAllActivites();

    List<Object[]> getTotalBudgetPerActivity();

    Set<User> getParticipantsByActivity(long id);

}
