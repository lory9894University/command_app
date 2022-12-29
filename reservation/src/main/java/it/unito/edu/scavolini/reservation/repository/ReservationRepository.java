package it.unito.edu.scavolini.reservation.repository;

import it.unito.edu.scavolini.reservation.model.Reservation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    List<Reservation> findAll();

    Reservation findDistinctFirstById(Long id);
}
