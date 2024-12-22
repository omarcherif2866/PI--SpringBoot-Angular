package com.example.pi.Services;

import com.example.pi.Models.Session;

import java.util.Set;

public interface ISessionService {

    public Session addSession(Session session);

    public void deleteSession(Long id);

    Session updateSession(Long id, String newNom);

    public Session getSessionById (Long id);

    public Set<Session> getAllSession ();

    public Session addSessionAndAssignToActivite (Session session, Long activiteId);

    long countAllSessions();

}
