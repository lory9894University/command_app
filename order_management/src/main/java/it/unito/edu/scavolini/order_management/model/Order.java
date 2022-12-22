package it.unito.edu.scavolini.order_management.model;

import it.unito.edu.scavolini.order_management.enums.OrderStateEnum;
import it.unito.edu.scavolini.order_management.enums.PaymentTypeEnum;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int order_id;

    @Column(name = "table_num")
    private String tableNum;

    @Column(name = "total")
    private double total;

    @Column(name = "state")
    private OrderStateEnum state;

    @Column(name = "payment_type")
    private PaymentTypeEnum paymentType;

//    @OneToMany(mappedBy = "order")
    @Transient
    private List<Preparation> preparationList;

    public Order(String tableNum, double total, OrderStateEnum state, PaymentTypeEnum paymentType) {
        this.tableNum = tableNum;
        this.total = total;
        this.state = state;
        this.paymentType = paymentType;
    }

    public Order() {
        this.tableNum = "null";
        this.total = -1;
        this.state = OrderStateEnum.UNPAID;
        this.paymentType = PaymentTypeEnum.ONLINE;
    }

    public int getId() {
        return order_id;
    }

    public String getTableNum() {
        return tableNum;
    }

    public void setTableNum(String tableNum) {
        this.tableNum = tableNum;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public OrderStateEnum getState() {
        return state;
    }

    public void setState(OrderStateEnum state) {
        this.state = state;
    }

    public PaymentTypeEnum getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
    }

    public List<Preparation> getPreparationList() {
        return preparationList;
    }

    public List<Preparation> addPreparation(Preparation preparation) {
        this.preparationList.add(preparation);
        return this.preparationList;
    }

    public List<Preparation> removePreparation(Preparation preparation) {
        this.preparationList.remove(preparation);
        return this.preparationList;
    }

    public void setPreparationList(List<Preparation> preparationList) {
        this.preparationList = preparationList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Preparation p : preparationList) {
            sb.append(p.toString() + "\n\t");
        }

        return "Order [" +
            "\n\tid=" + order_id +
            "\n\ttableNum=" + tableNum +
            "\n\ttotal=" + total +
            "\n\tstate=" + state +
            "\n\tpaymentType=" + paymentType +
            "\n\tpreparationList=\n\t" + sb + "]";
    }
}
