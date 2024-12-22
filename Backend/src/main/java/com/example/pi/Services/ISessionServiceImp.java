package com.example.pi.Services;

import com.example.pi.Models.Activite;
import com.example.pi.Models.Session;
import com.example.pi.Repositories.ActiviteRepository;
import com.example.pi.Repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;
@RequiredArgsConstructor
@Service
public class ISessionServiceImp implements ISessionService{

    private final SessionRepository sessionRepository;
    private final IActiviteService activiteService ;
    private final ActiviteRepository activiteRepository;


    @Override
    public Session addSession(Session session) {
        return sessionRepository.save(session);
    }

    @Override
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    @Override
    public Session updateSession(Long id, String newNom) {
        Session existingSession = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        existingSession.setNom(newNom);

        return sessionRepository.save(existingSession);
    }




    @Override
    public Session getSessionById(Long id) {
        return sessionRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("session not found"));
    }

    @Override
    public Set<Session> getAllSession() {
        Set<Session> sessions = new HashSet<>(sessionRepository.findAll());
        for (Session session : sessions) {

            if (session.getActiviteId() != null) {
                Activite activite = session.getActiviteId();
                Activite activiteInfo = new Activite();
                activiteInfo.setId(activite.getId());
                activiteInfo.setNom(activite.getNom());
                session.setActiviteId(activiteInfo);
            }
        }
        return sessions;
    }

    @Override
    public Session addSessionAndAssignToActivite(Session session, Long activiteId) {
        Activite activite = activiteService.getActiviteById(activiteId);
        session.setActiviteId(activite);
        sessionRepository.save(session);

        activite.getListSessions().add(session);
        activiteRepository.save(activite);

        return session;
    }

    @Override
    public long countAllSessions() {
        return sessionRepository.count();
    }
}
