package it.unito.edu.scavolini.order_management.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.unito.edu.scavolini.order_management.enums.OrderStateEnum;
import it.unito.edu.scavolini.order_management.enums.OrderTypeEnum;
import it.unito.edu.scavolini.order_management.enums.PaymentStateEnum;
import it.unito.edu.scavolini.order_management.enums.PaymentTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "table_num")
    private String tableNum;

    @Column(name = "total")
    private double total;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "payment_state")
    @Enumerated(EnumType.STRING)
    private PaymentStateEnum paymentState;

    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentTypeEnum paymentType;

    @Column(name = "order_type")
    @Enumerated(EnumType.STRING)
    private OrderTypeEnum orderType;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private OrderStateEnum orderState;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
    @JsonManagedReference
    private List<Preparation> preparationList;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private String orderUsername;

    // used to transit user between microservices (field user is not serialized in JSON)
    @Transient
    private UserTransient userTransient;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Preparation p : preparationList) {
            sb.append(p.toString() + "\n\t");
        }

        return "Order [" +
            "\n\tid=" + id +
            "\n\tuser=" + (user != null ? user.getUsername() : "null") +
            "\n\ttableNum=" + tableNum +
            "\n\ttotal=" + total +
            "\n\tstate=" + paymentState +
            "\n\tpaymentType=" + paymentType +
            "\n\tpreparationList=\n\t" + sb + "]";
    }
}
