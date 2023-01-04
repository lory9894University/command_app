package it.unito.edu.scavolini.reservation.repository;

import it.unito.edu.scavolini.reservation.enums.ReservationStateEnum;
import it.unito.edu.scavolini.reservation.model.Reservation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    List<Reservation> findAll();

    List<Reservation> findAllByOrderNull();

    List<Reservation> findAllByOrderNotNull();

    Reservation findDistinctFirstById(Long id);

    List<Reservation> findAllByState(ReservationStateEnum waiting);
}
