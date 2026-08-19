
 // Authority — جدول الصلاحيات (الأدوار)


package com.example.inventory_management.entity;

import jakarta.persistence.*;

@Entity  // كلاس = جدول في قاعدة البيانات
@Table(name = "authorities")  // اسم الجدول "authorities"
public class Authority {

    @Id        //المفتاح الاساسي
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //توليد الارقام تلقائيا 
    private Long id;

    @Column(nullable = false)   //اسم المستخدم — لا يقبل NULL
    private String username;    // يربط الصلاحية بمستخدم معيّن

    @Column(nullable = false)
    private String authority;     // مثل ROLE_ADMIN أو ROLE_STORE_MANAGER

    public Authority() {   // Constructor فاضي — JPA يحتاجه إجبارياً
    }

    // Constructor بمعطيات — لإنشاء صلاحية بسرعة (username + authority)

    public Authority(String username, String authority) {
        this.username = username;
        this.authority = authority;
    }
    // ===== Getters و Setters =====

    public Long getId() {
        return id;          // ملاحظة: id له getter فقط (ما فيه setter) لأنه يتولّد تلقائياً
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}