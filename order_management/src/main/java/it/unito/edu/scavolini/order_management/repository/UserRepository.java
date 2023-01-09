package it.unito.edu.scavolini.order_management.repository;

import it.unito.edu.scavolini.order_management.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();
}