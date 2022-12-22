package it.unito.edu.scavolini.order_management.model;

import it.unito.edu.scavolini.order_management.enums.PreparationStatesEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "preparation")
public class Preparation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int preparation_id;

    @Column(name = "name")
    private String name;

    @Column(name = "table_num")
    private String tableNum;

    @Column(name = "state")
    private PreparationStatesEnum state;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public Preparation(String name, String tableNum, Order order) {
        this.name = name;
        this.tableNum = tableNum;
        this.state = PreparationStatesEnum.WAITING;
        this.order = order;
    }

    public Preparation() {
        this.name = "";
        this.tableNum = "";
        this.state = PreparationStatesEnum.WAITING;
    }

    public String getName() {
        return name;
    }

    public String getTableNum() {
        return tableNum;
    }

    public void setTable(String tableNum) {
        this.tableNum = tableNum;
    }

    public PreparationStatesEnum getState() {
        return state;
    }

    public void setState(PreparationStatesEnum state) {
        this.state = state;
    }

    public int getId() {
        return preparation_id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String toString() {
        return "Preparation [" +
            "\n\t\tid=" + preparation_id +
            "\n\t\tname=" + name +
            "\n\t\ttable=" + tableNum +
            "\n\t\tstate=" + state + "]";
    }
}
