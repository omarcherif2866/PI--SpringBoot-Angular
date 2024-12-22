package com.example.pi.Controlleurs;

import com.example.pi.Models.Covoiturage;
import com.example.pi.Services.CovoiturageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/covoiturages")
@CrossOrigin(origins = "http://localhost:4200")
public class CovoiturageController {

    @Autowired
    private CovoiturageService covoiturageService;

    // Ajouter Covoiturage
    @PostMapping
    public Covoiturage createCovoiturage(@RequestBody Covoiturage covoiturage) {
        return covoiturageService.save(covoiturage);
    }

    // Consulter Covoiturage
    @GetMapping
    public List<Covoiturage> getAllCovoiturages() {
        return covoiturageService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Covoiturage> getCovoiturageById(@PathVariable Long id) {
        return covoiturageService.findById(id)
                .map(covoiturage -> ResponseEntity.ok().body(covoiturage))
                .orElse(ResponseEntity.notFound().build());
    }
}
