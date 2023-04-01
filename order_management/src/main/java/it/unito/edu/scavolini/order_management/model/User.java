package it.unito.edu.scavolini.order_management.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "username")
    @Length(max = 150)
    private String username; // contains user email from firebase

    @Column(name = "user_id", length = 2000)
    @Lob
    private String userId; // contains idtoken from firebase

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @JsonManagedReference
    private List<Order> orderList;
}