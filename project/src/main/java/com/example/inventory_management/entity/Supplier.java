package com.example.inventory_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    @NotBlank(message = "Supplier name is required")
    @Column(nullable = false)
    private String name;

    @Email(message = "Contact email must be a valid email address")
    @Column(unique = true)
    private String contactEmail;

    private String phone;

    public Supplier() {
    }

    public Supplier(Long supplierId, String name, String contactEmail, String phone) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactEmail = contactEmail;
        this.phone = phone;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
