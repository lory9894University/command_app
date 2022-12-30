package com.unito.edu.scavolini.menu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_session_id", nullable = false)
    private UserSession userSession;

    public Cart() {
    }

    public Cart(UserSession userSession) {
        this.userSession = userSession;
    }

    public Long getId() {
        return id;
    }

    public UserSession getUserSession() {
        return userSession;
    }
}
