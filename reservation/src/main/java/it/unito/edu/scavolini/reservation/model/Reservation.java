package it.unito.edu.scavolini.reservation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.unito.edu.scavolini.reservation.enums.ReservationStateEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "people_num")
    @NotNull
    private int peopleNum;

    @Column(name = "table_num")
    private String tableNum;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private ReservationStateEnum state;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}
