package com.example.pi.Services;

import com.example.pi.Models.Club;
import com.example.pi.Repositories.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClubService implements IServiceClub {
    @Autowired
    private ClubRepository clubRepository;


    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public Optional<Club> getClubById(Long id) {
        return clubRepository.findById(id);
    }

    public Club createClub(Club club) {
        return clubRepository.save(club);
    }

    public Club updateClub(Long id, Club clubDetails) {
        Optional<Club> optionalClub = clubRepository.findById(id);
        if (optionalClub.isPresent()) {
            Club club = optionalClub.get();
            club.setNom(clubDetails.getNom());
            club.setDescription(clubDetails.getDescription());
            club.setStatus(clubDetails.getStatus());
            club.setObjectif(clubDetails.getObjectif());
            club.setListReservations(clubDetails.getListReservations());
            club.setUsers(clubDetails.getUsers());
            club.setEvenementId(clubDetails.getEvenementId());
            return clubRepository.save(club);
        } else {
            return null;
        }
    }

    public void deleteClub(Long id) {
        clubRepository.deleteById(id);
    }


}
