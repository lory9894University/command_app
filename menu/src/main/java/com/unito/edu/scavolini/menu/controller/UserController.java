package com.unito.edu.scavolini.menu.controller;

import com.unito.edu.scavolini.menu.model.UserProfile;
import com.unito.edu.scavolini.menu.model.UserSession;
import com.unito.edu.scavolini.menu.repository.UserProfileRepository;
import com.unito.edu.scavolini.menu.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    //user session

    @PostMapping(value = "/addUserSession")
    public UserSession postUserSession(@RequestBody UserSession userSession) {
        System.out.println(userSession.getId());
        return this.userSessionRepository.save(userSession);
    }

    @GetMapping(value = "/getUserSessionById")
    public UserSession getUserSession(@RequestBody UserSessionId userSessionId) {
        return this.userSessionRepository.findDistinctFirstById(userSessionId.id);
    }

    @GetMapping(value = "/getUserSessions")
    public List<UserSession> getUserSession() {
        return userSessionRepository.findAll();
    }

    @PostMapping(value = "/deleteUserSession")
    public void deleteUserSession(@RequestBody UserSessionId userSessionId) {
        userSessionRepository.deleteById(userSessionId.id);
    }

    // user profile

    // add user profile
    @PostMapping(value = "/addUserProfile")
    public UserProfile postUserProfile(@RequestBody UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    // get user profile by email
    @GetMapping(value = "/getUserProfile")
    public UserProfile getUserProfile(@RequestBody UserProfileEmail userProfileEmail) {
        return userProfileRepository.findDistinctFirstByEmail(userProfileEmail.email);
    }

    // get all user profiles
    @GetMapping(value = "/getUserProfiles")
    public List<UserProfile> getLoggedUsers() {
        return userProfileRepository.findAll();
    }

    // delete user profile by email
    @PostMapping(value = "/deleteUserProfile")
    public void deleteUserProfile(@RequestBody UserProfileEmail userProfileEmail) {
        boolean exists = userProfileRepository.existsByEmail(userProfileEmail.email);
        if (exists){
            userProfileRepository.deleteById(userProfileEmail.email);
        }else {
            System.out.printf("User with email %s not found", userProfileEmail.email);
        }
    }

    private record UserSessionId(Long id) {}
    private record UserProfileEmail(String email) {}
}
