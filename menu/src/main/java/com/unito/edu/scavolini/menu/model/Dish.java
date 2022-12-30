package com.unito.edu.scavolini.menu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dish")
public class Dish {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "imgUrl")
    private String imgURL;

    @Column(name = "course", nullable = false)
    private String course;

    public Dish() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
