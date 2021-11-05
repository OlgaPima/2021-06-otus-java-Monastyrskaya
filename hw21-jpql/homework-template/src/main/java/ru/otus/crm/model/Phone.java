package ru.otus.crm.model;

import javax.persistence.*;

@Entity
@Table(name = "Phone")
public class Phone /*implements Cloneable*/ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "number")
    private String number;


    public Phone() {}

    public Phone(String number) {
        this.number = number;
    }

    @Override
    public Phone clone() {
        Phone copy = new Phone(number);
        copy.setId(id);
        return copy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}