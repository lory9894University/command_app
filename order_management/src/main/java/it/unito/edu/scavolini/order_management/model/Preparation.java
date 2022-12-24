package it.unito.edu.scavolini.order_management.model;

import it.unito.edu.scavolini.order_management.enums.PreparationStatesEnum;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "preparation")
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
    private Order order;

    public String toString() {
        return "Preparation [" +
            "\n\t\tid=" + id +
            "\n\t\tname=" + name +
            "\n\t\ttable=" + tableNum +
            "\n\t\tstate=" + state + "]";
    }
}
