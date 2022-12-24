package it.unito.edu.scavolini.order_management.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "order")
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

    @Transient
    @OneToMany(mappedBy = "order")
    private List<Preparation> preparationList;

//    @OneToOne(mappedBy = "order")
//    private Reservation reservation;

    public List<Preparation> addPreparation(Preparation preparation) {
        this.preparationList.add(preparation);
        return this.preparationList;
    }

    public List<Preparation> removePreparation(Preparation preparation) {
        this.preparationList.remove(preparation);
        return this.preparationList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Preparation p : preparationList) {
            sb.append(p.toString() + "\n\t");
        }

        return "Order [" +
            "\n\tid=" + id +
            "\n\ttableNum=" + tableNum +
            "\n\ttotal=" + total +
            "\n\tstate=" + paymentState +
            "\n\tpaymentType=" + paymentType +
            "\n\tpreparationList=\n\t" + sb + "]";
    }
}
