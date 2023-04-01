package it.unito.edu.scavolini.reservation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.unito.edu.scavolini.reservation.enums.ReservationStateEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
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
    private List<Reservation> reservationList;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @JsonManagedReference
    private List<Order> orderList;
}
