package it.unito.edu.scavolini.order_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
