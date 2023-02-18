package com.unito.edu.scavolini.kitchen.model;

import com.unito.edu.scavolini.kitchen.enums.PreparationStatesEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "preparation")
public class Preparation {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "table_num")
    private String tableNum;

    @Column(name = "state")
    private PreparationStatesEnum state;

    public Preparation(String name, String tableNum) {
        this.name = name;
        this.tableNum = tableNum;
        this.state = PreparationStatesEnum.WAITING;
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

    public void setTableNum(String tableNum) {
        this.tableNum = tableNum;
    }

    public PreparationStatesEnum getState() {
        return state;
    }

    public void setState(PreparationStatesEnum state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }
}
