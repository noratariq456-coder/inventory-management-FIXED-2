package com.example.inventory_management.dto;

import java.util.List;

public class AuthResponse {

    private String username;
    private List<String> roles;

    public AuthResponse() {
    }

    public AuthResponse(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
