package com.unito.edu.scavolini.menu.repository;

import org.springframework.data.repository.CrudRepository;
import com.unito.edu.scavolini.menu.model.UserSession;

import java.util.List;

public interface UserSessionRepository extends CrudRepository<UserSession, Long> {
    UserSession findDistinctFirstById(Long id);

    List<UserSession> findAll();
}
