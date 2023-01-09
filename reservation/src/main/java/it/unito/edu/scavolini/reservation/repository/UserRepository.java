package it.unito.edu.scavolini.reservation.repository;

import it.unito.edu.scavolini.reservation.model.Reservation;
import it.unito.edu.scavolini.reservation.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();
}
