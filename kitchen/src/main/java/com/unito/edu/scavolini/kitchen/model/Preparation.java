package com.unito.edu.scavolini.kitchen.model;

import com.unito.edu.scavolini.kitchen.enums.PreparationStatesEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "preparation")
public class Preparation {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "table")
    private String table;

    @Column(name = "state")
    private PreparationStatesEnum state;

    public Preparation(String name, String table) {
        this.name = name;
        this.table = table;
        this.state = PreparationStatesEnum.WAITING;
    }

    public Preparation() {
        this.name = "";
        this.table = "";
        this.state = PreparationStatesEnum.WAITING;
    }

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public PreparationStatesEnum getState() {
        return state;
    }

    public void setState(PreparationStatesEnum state) {
        this.state = state;
    }
}
