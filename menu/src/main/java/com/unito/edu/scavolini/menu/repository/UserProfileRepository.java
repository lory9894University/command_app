package com.unito.edu.scavolini.menu.repository;

import com.unito.edu.scavolini.menu.model.UserProfile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserProfileRepository extends CrudRepository<UserProfile, String> {
    List<UserProfile> findAll();

    UserProfile findDistinctFirstByEmail(String email);

    boolean existsByEmail(String email);
}
