package it.unito.edu.scavolini.reservation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserTransient {

    @Id
    @Column(name = "user_id")
    @Length(min = 1, max = 150)
    private String userId;

    @Column(name = "username")
    private String username;
}
