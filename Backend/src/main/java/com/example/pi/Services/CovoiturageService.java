package com.example.pi.Services;

import com.example.pi.Models.Covoiturage;
import com.example.pi.Repositories.CovoiturageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CovoiturageService {

    @Autowired
    private CovoiturageRepository covoiturageRepository;

    public List<Covoiturage> findAll() {
        return covoiturageRepository.findAll();
    }

    public Optional<Covoiturage> findById(Long id) {
        return covoiturageRepository.findById(id);
    }

    public Covoiturage save(Covoiturage covoiturage) {
        return covoiturageRepository.save(covoiturage);
    }
}
