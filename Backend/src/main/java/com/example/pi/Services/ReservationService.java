package com.example.pi.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pi.Models.Reservation;
import com.example.pi.Repositories.ReservationRepository;


@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(long id) {
        return reservationRepository.findById(id);
    }

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(long id, Reservation reservationDetails) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            Reservation updatedReservation = reservation.get();
            updatedReservation.setLieu(reservationDetails.getLieu());
            updatedReservation.setDateDeb(reservationDetails.getDateDeb());
            updatedReservation.setDateFin(reservationDetails.getDateFin());
            updatedReservation.setBudget(reservationDetails.getBudget());
            updatedReservation.setActiviteId(reservationDetails.getActiviteId());
            updatedReservation.setClubId(reservationDetails.getClubId());
            updatedReservation.setEvenementId(reservationDetails.getEvenementId());
            return reservationRepository.save(updatedReservation);
        } else {
            // handle the case where the reservation is not found
            return null;
        }
    }

    public void deleteReservation(long id) {
        reservationRepository.deleteById(id);
    }
}
