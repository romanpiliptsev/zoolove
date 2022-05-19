package com.mirea.petshop.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "types_id")
    private int typeId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "price")
    private int price;
    @Column(name = "description")
    private String description;
}
