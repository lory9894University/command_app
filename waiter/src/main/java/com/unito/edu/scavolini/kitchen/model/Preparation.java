package com.unito.edu.scavolini.kitchen.model;

import com.unito.edu.scavolini.kitchen.enums.PreparationFor;
import com.unito.edu.scavolini.kitchen.enums.PreparationStatesEnum;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "preparation")
public class Preparation {
    private static final List<PreparationStatesEnum> prepStatesWaiter = List.of(
            PreparationStatesEnum.TO_DELIVER,
            PreparationStatesEnum.DELIVERED);

    private static final List<PreparationStatesEnum> prepStatesKitchen = List.of(
            PreparationStatesEnum.WAITING,
            PreparationStatesEnum.UNDERWAY,
            PreparationStatesEnum.READY);

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "tableNum")
    private String tableNum;

    @Column(name = "state")
    private PreparationStatesEnum state;

    private PreparationFor preparationFor;

    public Preparation(String name, String tableNum, PreparationFor preparationFor) {
        this.name = name;
        this.tableNum = tableNum;
        this.preparationFor = preparationFor;
        if (preparationFor == PreparationFor.KITCHEN) {
            this.state = PreparationStatesEnum.WAITING;
        } else if (preparationFor == PreparationFor.WAITER) {
            this.state = PreparationStatesEnum.TO_DELIVER;
        } else {
            throw new IllegalArgumentException("PreparationFor must be KITCHEN or WAITER");
        }
    }

    public Preparation() {
        this.name = "";
        this.tableNum = "";
        this.state = PreparationStatesEnum.WAITING;
    }

    public String getName() {
        return name;
    }

    public String getTable() {
        return tableNum;
    }

    public PreparationFor getPreparationFor() {
        return preparationFor;
    }

    public void setTable(String tableNum) {
        this.tableNum = tableNum;
    }

    public PreparationStatesEnum getState() {
        return state;
    }

    public void setState(PreparationStatesEnum state) {
        // Check if the state is valid for the preparation
        if (preparationFor == PreparationFor.KITCHEN && prepStatesKitchen.contains(state)) {
            this.state = state;
        } else if (preparationFor == PreparationFor.WAITER && prepStatesWaiter.contains(state)) {
            this.state = state;
        }else {
            throw new IllegalArgumentException("Invalid state for this preparation");
        }
    }

    public int getId() {
        return id;
    }
}
