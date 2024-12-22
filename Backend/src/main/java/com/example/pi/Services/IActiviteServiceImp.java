package com.example.pi.Services;

import com.example.pi.Models.Activite;
import com.example.pi.Models.Reservation;
import com.example.pi.Models.Session;
import com.example.pi.Models.User;
import com.example.pi.Repositories.ActiviteRepository;
import com.example.pi.Repositories.ReservationRepository;
import com.example.pi.Repositories.SessionRepository;
import com.example.pi.Repositories.UserRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class IActiviteServiceImp implements IActiviteService{

    private final ActiviteRepository activiteRepository;
    private final SessionRepository sessionRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    @Override
    public Activite addActivite(Activite activite) {
        try {
            return activiteRepository.save(activite);
        } catch (DataIntegrityViolationException e) {
            // Gérer l'erreur de clé dupliquée ici
            throw new IllegalArgumentException("Erreur lors de l'ajout de l'activité : Cette activité existe déjà.");
        } catch (Exception e) {
            // Gérer les autres exceptions ici
            throw new RuntimeException("Une erreur s'est produite lors du traitement de la demande : " + e.getMessage());
        }
    }

    @Override
    public void deleteActivite(Long id) {
        activiteRepository.deleteById(id);
    }


    @Override
    public Activite updateActivite(Long id, Activite newActivite) {
        try {
            Activite existingActivite = activiteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Activite not found"));

            if (newActivite.getNom() != null) {
                existingActivite.setNom(newActivite.getNom());
            }
            if (newActivite.getDescription() != null) {
                existingActivite.setDescription(newActivite.getDescription());
            }
            if (newActivite.getAnnimateur() != null) {
                existingActivite.setAnnimateur(newActivite.getAnnimateur());
            }
            if (newActivite.getImage() != null) {
                existingActivite.setImage(newActivite.getImage());
            }

            if (newActivite.getNbrParticipants() != -1) {
                existingActivite.setNbrParticipants(newActivite.getNbrParticipants());
            }


            if (!newActivite.getListSessions().isEmpty()) {
                List<Long> sessionIds = newActivite.getListSessions().stream()
                        .map(Session::getId)
                        .collect(Collectors.toList());
                List<Session> sessions = sessionRepository.findAllById(sessionIds);
                existingActivite.getListSessions().addAll(sessions);
            }

            if (!newActivite.getListReservations().isEmpty()) {
                List<Long> reservationIds = newActivite.getListReservations().stream()
                        .map(Reservation::getId)
                        .collect(Collectors.toList());
                List<Reservation> reservations = reservationRepository.findAllById(reservationIds);
                existingActivite.getListReservations().addAll(reservations);
            }



            Activite updatedActivite = activiteRepository.save(existingActivite);

            return updatedActivite;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Activite not found with ID: " + id);
        }
    }

    @Override
    public Activite getActiviteById(Long id) {
        return activiteRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("activite not found"));
    }

    @Override
    public Set<Activite> getAllActivites() {
        List<Activite> activitesList = activiteRepository.findAll();
        Set<Activite> activitesSet = new HashSet<>(activitesList);

        // Chargement des sessions et des réservations pour chaque activité
        activitesSet.forEach(activite -> {
            // Charger les sessions
            activite.getListSessions().size(); // Pour charger les sessions

            // Charger les réservations
            activite.getListReservations().size(); // Pour charger les réservations

            // Ne pas charger d'autres attributs des sessions ou des réservations ici, sauf si nécessaire
        });

        return activitesSet;
    }


    private void decrementNbrParticipants(Activite activite) {
        if (activite.getNbrParticipants() > 0) {
            activite.setNbrParticipants(activite.getNbrParticipants() - 1);
            activiteRepository.save(activite);
        } else {
            throw new RuntimeException("No more available spots for this activity");
        }
    }
    @Override
    @Transactional
    public Activite participateInActivity(Long userId, Long activiteId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Activite activite = activiteRepository.findById(activiteId)
                .orElseThrow(() -> new RuntimeException("Activite not found"));

        // Vérifiez s'il reste des places disponibles
        if (activite.getNbrParticipants() <= 0) {
            throw new RuntimeException("No more available spots for this activity");
        }

        // Ajouter l'activité à la liste de l'utilisateur
        user.getListActivites().add(activite);
        userRepository.save(user);

        // Décrémenter le nombre de participants
        decrementNbrParticipants(activite);

        // Générer le QR code pour l'activité sous forme de byte[]
        byte[] qrCodeBytes = generateQRCode(user.getUsername(), activite.getNom());
        byte[] pdfBytes = generatePDF(user.getUsername(), activite.getNom(), activite.getListSessions());

        // Envoyer le résultat de la participation par e-mail avec le QR code en pièce jointe
        sendParticipationResultByEmail(user.getEmail(), activite, qrCodeBytes, pdfBytes);

        return activite;
    }
    private byte[] generateQRCode(String participantName, String activiteNom) {

        // Générer le contenu du QR code avec les détails de l'activité
        String qrCodeContent = "Accés autorisé";

        //String qrCodeContent = "Confirmation de l'acces du participant: " + participantName + "\n";
        //qrCodeContent += "inscrit dans l'activité: " + activiteNom + "\n ";

        ByteArrayOutputStream outputStream = QRCode.from(qrCodeContent)
                .to(ImageType.PNG)
                .stream();

        return outputStream.toByteArray();
    }


    private void sendParticipationResultByEmail(String userEmail, Activite activite, byte[] qrCodeBytes, byte[] pdfBytes) {
        String subject = "Résultat de votre participation à l'activité";
        String message = buildParticipationResultMessage(activite);

        emailService.sendEmailWithAttachment(userEmail, subject, message, qrCodeBytes,pdfBytes);

        System.out.println("E-mail envoyé à l'utilisateur : " + userEmail);
        System.out.println("Sujet : " + subject);
        System.out.println("Message : " + message);
    }

    private String buildParticipationResultMessage(Activite activite) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bonjour,\n\n");
        sb.append("Vous avez participé à l'activité suivante :\n\n");
        sb.append("Nom de l'activité : ").append(activite.getNom()).append("\n");
        sb.append("Description : ").append(activite.getDescription()).append("\n\n");
        sb.append("Le QR code pour cette activité est en pièce jointe.\n\n");
        sb.append("Merci d'avoir participé !");

        return sb.toString();
    }


    private byte[] generatePDF(String participantName, String activiteNom, Set<Session> sessionNom) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Chemin où enregistrer le fichier PDF
        String outputPath = "images/fichier.pdf";

        // Création du document PDF
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        Document document = new Document(pdfDocument);

        try {
            // Ajouter l'en-tête avec titre et logo
            Paragraph header = new Paragraph()
                    .setTextAlignment(TextAlignment.CENTER)  // Centre le paragraphe
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            // Ajouter le titre en rouge
            Text title = new Text("Participation à l'activité")
                    .setFontSize(18);

            // Ajouter le logo centré et redimensionné
            Image logo = new Image(ImageDataFactory.create("src/main/java/com/example/pi/logo/logo.jpg"))
                    .scaleToFit(100, 100)  // Redimensionne l'image pour s'adapter à une taille de 100x100 pixels
                    .setFixedPosition(30, 760);  // Position fixe à gauche avec une marge de 30 pixels et à 760 pixels de la bas

            header.add(title);
            header.add(logo);
            document.add(header);

            // Ajouter les détails de l'activité et du participant
            Paragraph confirmation = new Paragraph("\n \n \nNous vous confirmons votre participation à cette activité. " +
                    "\nNous avons hâte de vous y retrouver !");
            document.add(confirmation);
            document.add(new Paragraph("Nom participant: " + participantName));
            document.add(new Paragraph("Nom activité: " + activiteNom + "\n"));


            Paragraph directeur = new Paragraph("Le Directeur\n Prof.Tahar BEN LAKHDAR")
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(directeur);


            // Ajouter l'image de la signature alignée à droite
            Image badge = new Image(ImageDataFactory.create("src/main/java/com/example/pi/logo/badge.png"))
                    .scaleToFit(100, 100)
                    .setFixedPosition(450, 450); // Position fixe à droite, ajustez selon vos besoins
            document.add(badge);
            // Fermeture du document
            document.close();

            // Enregistrer le fichier PDF localement
            FileOutputStream fos = new FileOutputStream(outputPath);
            fos.write(outputStream.toByteArray());
            fos.close();

            System.out.println("PDF généré et enregistré avec succès : " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }


    @Override
    public List<Object[]> getActivityParticipationCount() {
        return activiteRepository.getActivityParticipationCount();
    }

    @Override
    public List<Object[]> getMostPopularActivity() {
        Pageable pageable = PageRequest.of(0, 1); // Limiter à un résultat
        return activiteRepository.getMostPopularActivity(pageable);
    }

    @Override
    public List<Object[]> getActivitySessionsCount() {
        return activiteRepository.getActivitySessionsCount();
    }

    @Override
    public long countAllActivites() {
        return activiteRepository.count();
    }

    @Override
    public List<Object[]> getTotalBudgetPerActivity() {
        return activiteRepository.getTotalBudgetPerActivity();
    }

    @Override
    public Set<User> getParticipantsByActivity(long id) {
        Activite activite = activiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activite not found with id: " + id));

        return activite.getUsers();
    }



}
