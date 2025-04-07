package com.eCommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.eCommerce.Entity.CartItem;
import com.eCommerce.Entity.Product;
import com.eCommerce.Entity.Users;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findById(Users user);
    List<CartItem> findByUserId(Long userId); // Custom query to find cart items by user ID
    Optional<CartItem> findByUserAndProduct(Users user, Product product); // Ensure it's returning Optional<CartItem>
}