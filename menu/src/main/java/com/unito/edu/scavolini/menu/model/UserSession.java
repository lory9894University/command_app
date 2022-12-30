package com.unito.edu.scavolini.menu.model;

import jakarta.persistence.*;

@Table(name = "user_session")
@Entity
public class UserSession {
    // todo RIP CARRELLO
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;

    // if userSession is null, the user is not logged in
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    public UserSession() {
    }

    public Long getId() {
        return id;
    }
}