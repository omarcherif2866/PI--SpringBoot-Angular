package com.example.pi.Controlleurs;

import com.example.pi.Models.Session;
import com.example.pi.Services.ISessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    private final ISessionService  iSessionService ;


    @PostMapping()
    public Session addSession(@RequestBody Session session){
        return iSessionService.addSession(session);
    }


    @GetMapping("{id}")
    public Session getSessionById(@PathVariable Long id){
        return iSessionService.getSessionById(id);
    }

    @DeleteMapping("{id}")
    public void deleteSession(@PathVariable Long id) { iSessionService.deleteSession(id);}

    @GetMapping("/allSessions")
    public ResponseEntity<Set<Session>> getAllSessions() {
        Set<Session> sessions = iSessionService.getAllSession();
        return ResponseEntity.ok(sessions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        String newNom = updates.get("nom");
        if (newNom == null || newNom.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Session updatedSession = iSessionService.updateSession(id, newNom);
        return ResponseEntity.ok(updatedSession);
    }

    @PostMapping(value = "/addSessionAndAssignToActivite/{activiteId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Session> addSessionAndAssignToActivite(@RequestBody Session session, @PathVariable Long activiteId) {
        Session createdSession = iSessionService.addSessionAndAssignToActivite(session, activiteId);
        return ResponseEntity.ok(createdSession);
    }

    @GetMapping("/countSessions")
    public ResponseEntity<Long> countAllSessions() {
        long count = iSessionService.countAllSessions();
        return ResponseEntity.ok(count);
    }


}
