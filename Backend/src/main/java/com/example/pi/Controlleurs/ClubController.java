package com.example.pi.Controlleurs;

import com.example.pi.Models.Club;
import com.example.pi.Services.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {
    @Autowired
    private ClubService clubService;




    @GetMapping("/get")
    public List<Club> getAllClubs() {
        return clubService.getAllClubs();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Club> getClubById(@PathVariable Long id) {
        Optional<Club> club = clubService.getClubById(id);
        return club.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<String> createClubWithImage(@RequestParam("nom") String nom,
                                                      @RequestParam("description") String description,
                                                      @RequestParam("status") Boolean status,
                                                      @RequestParam("objectif") String objectif,
                                                      @RequestParam("image") MultipartFile imageFile) {
        try {



            byte[] imageData = imageFile.getBytes();
            Club club = new Club();
            club.setNom(nom);
            club.setDescription(description); // Utilisez la description filtrée
            club.setStatus(status);
            club.setObjectif(objectif);
            club.setImage(imageData);

            clubService.createClub(club);

            return ResponseEntity.ok("Club created successfully with image");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable Long id, @RequestBody Club clubDetails) {
        Club updatedClub = clubService.updateClub(id, clubDetails);
        return updatedClub != null ? ResponseEntity.ok(updatedClub) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.ok("Club deleted successfully");
    }


}
