package com.example.inventory_management.repository;

import com.example.inventory_management.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findByUsername(String username);

}