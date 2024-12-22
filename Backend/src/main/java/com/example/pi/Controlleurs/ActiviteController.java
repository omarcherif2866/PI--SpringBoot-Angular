package com.example.pi.Controlleurs;

import com.example.pi.Models.*;
import com.example.pi.Repositories.ReservationRepository;
import com.example.pi.Repositories.SessionRepository;
import com.example.pi.Repositories.UserRepository;
import com.example.pi.Services.EmailService;
import com.example.pi.Services.FileStorageException;
import com.example.pi.Services.FileStorageService;
import com.example.pi.Services.IActiviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/activite")
@RequiredArgsConstructor
public class ActiviteController {

    private final IActiviteService iActiviteService;

    private final EmailService emailService;
    private final FileStorageService fileStorageService;
    private final SessionRepository sessionRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;


  /*  @PostMapping()
    public ResponseEntity<Activite> addActivite(@RequestParam("nom") String nom,
                                                @RequestParam("description") String description,
                                                @RequestParam("annimateur") Annimateur annimateur,
                                                @RequestParam("image") MultipartFile imageFile) {
        try {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String imagePath = "/images/" + fileName;

            Path path = Paths.get("images/");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            try {
                Files.copy(imageFile.getInputStream(), Paths.get("images/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (FileAlreadyExistsException ex) {
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                fileName = timeStamp + "_" + fileName;
                Files.copy(imageFile.getInputStream(), Paths.get("images/" + fileName), StandardCopyOption.REPLACE_EXISTING);
            }

            Activite activite = new Activite();
            activite.setNom(nom);
            activite.setDescription(description);
            activite.setAnnimateur(annimateur);
            activite.setImage(fileName);
            Activite savedActivite = iActiviteService.addActivite(activite);

            return ResponseEntity.ok(savedActivite);
        } catch (IOException e) {
            System.out.println("An error occurred while processing the request: " + e.getMessage());
            e.printStackTrace(System.out);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    } */



    @PostMapping()
    public ResponseEntity<?> addActivite(@RequestParam("nom") String nom,
                                         @RequestParam("description") String description,
                                         @RequestParam("annimateur") Annimateur annimateur,
                                         @RequestParam("image") MultipartFile imageFile,
                                         @RequestParam("nbrParticipants") Integer nbrParticipants,
                                         @RequestParam(value = "sessionIds", required = false) List<Long> sessionIds,
                                         @RequestParam(value = "reservationIds", required = false) List<Long> reservationIds) {
        try {
            if (nom == null || nom.isEmpty() || description == null || description.isEmpty() || annimateur == null || imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Paramètres d'entrée invalides.");
            }

            String fileName = fileStorageService.storeFile(imageFile);

            Activite activite = new Activite();
            activite.setNom(nom);
            activite.setDescription(description);
            activite.setNbrParticipants(nbrParticipants);
            activite.setAnnimateur(annimateur);
            activite.setImage(fileName);

            if (sessionIds != null) {
                List<Session> sessions = sessionRepository.findAllById(sessionIds);
                for (Session session : sessions) {
                    session.setActiviteId(activite);
                }
                activite.getListSessions().addAll(sessions);
            }

            if (reservationIds != null) {
                List<Reservation> reservations = reservationRepository.findAllById(reservationIds);
                activite.getListReservations().addAll(reservations);
            }

            Activite savedActivite = iActiviteService.addActivite(activite);

            // Envoyer un e-mail pour notifier de l'ajout de l'activité
            String emailTo = "comar2866@gmail.com";
            String emailSubject = "Nouvelle activité ajoutée";
            String emailBody = "Une nouvelle activité a été ajoutée : " + activite.getNom() + "\nDescription : " + activite.getDescription()
            + "\nL'annimateur est : " + activite.getAnnimateur()
                    + "\nDans la/les session(s) : " + activite.getListSessions()
                    + "\nReservé à : " + activite.getListReservations();
            emailService.sendEmail(emailTo, emailSubject, emailBody);

            return ResponseEntity.ok(savedActivite);
        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la sauvegarde du fichier : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }







    @GetMapping("{id}")
    public Activite getActiviteById(@PathVariable Long id){
        return iActiviteService.getActiviteById(id);
    }

    @DeleteMapping("{id}")
    public void deleteActivite(@PathVariable Long id) {
        iActiviteService.deleteActivite(id);
    }

    @GetMapping("/allActivites")
    public ResponseEntity<Set<Activite>> getAllActivites() {
        Set<Activite> activites = iActiviteService.getAllActivites();
        return ResponseEntity.ok(activites);
    }

  /*  @PutMapping("/{id}")
    public ResponseEntity<Activite> updateActivite(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("annimateur") Annimateur annimateur,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            Activite existingActivite = iActiviteService.getActiviteById(id);

            Activite newActivite = new Activite();
            existingActivite.setNom(newActivite.getNom());
            existingActivite.setDescription(newActivite.getDescription());
            existingActivite.setAnnimateur(newActivite.getAnnimateur());
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String imagePath = "/images/" + fileName;
                Path path = Paths.get("images/");
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                Files.copy(imageFile.getInputStream(), Paths.get("images/" + fileName), StandardCopyOption.REPLACE_EXISTING);
                existingActivite.setImage(imagePath);
                existingActivite.setNom(nom);
                existingActivite.setDescription(description);
                existingActivite.setAnnimateur(annimateur);
            }

            Activite updatedActivite = iActiviteService.updateActivite(id, existingActivite);

            return ResponseEntity.ok(updatedActivite);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    } */


    @PutMapping("/{id}")
    public ResponseEntity<Activite> updateActivite(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("annimateur") Annimateur annimateur,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "sessionIds", required = false) List<Long> sessionIds,
            @RequestParam(value = "reservationIds", required = false) List<Long> reservationIds,
            @RequestParam(value = "nbrParticipants", required = false) Integer nbrParticipants) {
        try {
            Activite existingActivite = iActiviteService.getActiviteById(id);

            if (nom != null && !nom.isEmpty()) {
                existingActivite.setNom(nom);
            }
            if (description != null && !description.isEmpty()) {
                existingActivite.setDescription(description);
            }
            if (annimateur != null) {
                existingActivite.setAnnimateur(annimateur);
            }
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String imagePath = fileName;
                Path path = Paths.get("images/");
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                Files.copy(imageFile.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                existingActivite.setImage(imagePath);
            }
            if (sessionIds != null) {
                List<Session> sessions = sessionRepository.findAllById(sessionIds);
                existingActivite.getListSessions().clear();
                existingActivite.getListSessions().addAll(sessions);
            }
            if (reservationIds != null) {
                List<Reservation> reservations = reservationRepository.findAllById(reservationIds);
                existingActivite.getListReservations().clear();
                existingActivite.getListReservations().addAll(reservations);
            }
            existingActivite.setNbrParticipants(nbrParticipants); // Mettre à jour le nombre de participants


            Activite updatedActivite = iActiviteService.updateActivite(id, existingActivite);

            return ResponseEntity.ok(updatedActivite);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    @GetMapping("/image/{imageName}")
    public ResponseEntity<?> getImage(@PathVariable String imageName) {
        try {
            // Récupérez le chemin complet de l'image en fonction de son nom
            Path imagePath = fileStorageService.loadFileAsPath(imageName);

            // Lisez les octets de l'image
            byte[] imageBytes = Files.readAllBytes(imagePath);

            // Créez un objet HttpHeaders pour définir le type de contenu de la réponse
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Vous pouvez ajuster le type de contenu selon le format de votre image

            // Renvoyez une réponse avec les octets de l'image et les en-têtes appropriés
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            // Gérez l'exception si le fichier n'est pas trouvé ou s'il y a une erreur lors de la lecture
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @PostMapping("/{userId}/participate/{activiteId}")
    public ResponseEntity<?> participateInActivity(@PathVariable Long userId, @PathVariable Long activiteId) {
        try {
            Activite activite = iActiviteService.participateInActivity(userId, activiteId);
            return ResponseEntity.ok(activite);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    @GetMapping("/participation-count")
    public List<Object[]> getActivityParticipationCount() {
        return iActiviteService.getActivityParticipationCount();
    }

    @GetMapping("/most-popular")
    public List<Object[]> getMostPopularActivity() {
        return iActiviteService.getMostPopularActivity();
    }

    @GetMapping("/activity-sessions-count")
    public List<Object[]> getActivitySessionsCount() {
        return iActiviteService.getActivitySessionsCount();
    }

    @GetMapping("/countActivity")
    public ResponseEntity<Long> countAllActivites() {
        long count = iActiviteService.countAllActivites();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/total-budget-per-activity")
    public List<Object[]> getTotalBudgetPerActivity() {
        return iActiviteService.getTotalBudgetPerActivity();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<Set<User>> getParticipantsByActivity(@PathVariable("id") long id) {
        Set<User> participants = iActiviteService.getParticipantsByActivity(id);
        return ResponseEntity.ok(participants);
    }
}



