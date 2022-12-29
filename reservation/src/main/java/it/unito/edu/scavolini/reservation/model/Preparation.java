package it.unito.edu.scavolini.reservation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import it.unito.edu.scavolini.reservation.enums.PreparationStatesEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "preparations")
public class Preparation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "table_num")
    private String tableNum;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private PreparationStatesEnum state;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    public String toString() {
        return "Preparation [" +
            "\n\t\tid=" + id +
            "\n\t\tname=" + name +
            "\n\t\ttableNum=" + tableNum +
            "\n\t\tstate=" + state + "]";
    }
}
