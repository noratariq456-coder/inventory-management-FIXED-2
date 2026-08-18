package com.example.inventory_management.config;

import com.example.inventory_management.entity.Authority;
import com.example.inventory_management.entity.Category;
import com.example.inventory_management.entity.Supplier;
import com.example.inventory_management.entity.User;
import com.example.inventory_management.repository.AuthorityRepository;
import com.example.inventory_management.repository.CategoryRepository;
import com.example.inventory_management.repository.SupplierRepository;
import com.example.inventory_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AuthorityRepository authorityRepository,
                           CategoryRepository categoryRepository,
                           SupplierRepository supplierRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.passwordEncoder = passwordEncoder;
    }
    

    @Override
    public void run(String... args) {

        createUserIfNotExists("admin", "admin123", "ADMIN");
        createUserIfNotExists("manager", "manager123", "STORE_MANAGER");
        createUserIfNotExists("employee", "employee123", "EMPLOYEE");

        if (categoryRepository.count() == 0) {

            categoryRepository.save(new Category(
                    null,
                    "Electronics",
                    "Electronic products"
            ));

            categoryRepository.save(new Category(
                    null,
                    "Furniture",
                    "Furniture products"
            ));

            categoryRepository.save(new Category(
                    null,
                    "Stationery",
                    "Office supplies"
            ));
        }

        if (supplierRepository.count() == 0) {

            supplierRepository.save(new Supplier(
                    null,
                    "China Supplier",
                    "china@supplier.com",
                    "0500000000"
            ));

            supplierRepository.save(new Supplier(
                    null,
                    "Turkey Supplier",
                    "turkey@supplier.com",
                    "0500000001"
            ));

            supplierRepository.save(new Supplier(
                    null,
                    "Local Supplier",
                    "local@supplier.com",
                    "0500000002"
            ));
        }
    }

    private void createUserIfNotExists(String username,
                                       String password,
                                       String role) {

        if (!userRepository.existsById(username)) {

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);

            userRepository.save(user);

            Authority authority = new Authority();
            authority.setUsername(username);
            authority.setAuthority(role);

            authorityRepository.save(authority);

            System.out.println("Created user: " + username + " (" + role + ")");
        }
    }
}