package com.eCommerce.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.eCommerce.Entity.CartItem;
import com.eCommerce.Entity.Product;
import com.eCommerce.Entity.Users;
import com.eCommerce.Repository.CartRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public Optional<CartItem> addToCart(Users user, Product product, int quantity) {
        // Find the existing cart item or create a new one
        CartItem cartItem = cartRepository.findByUserAndProduct(user, product)
                .orElse(new CartItem(user, product, (long) 0)); // Initialize quantity to 0 if creating a new item

        // Update the quantity
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        
        // Save the cart item
        CartItem savedCartItem = cartRepository.save(cartItem);
        
        // Return the saved cart item wrapped in an Optional
        return Optional.of(savedCartItem);
    }


    // Retrieve user's cart
 // Retrieve user's cart by user ID
    public List<CartItem> getCartItems(Long userId) {
        return cartRepository.findByUserId(userId); // Use the custom method in the repository
    }

    
    // Remove item from cart
    public void removeFromCart(Long cartItemId) {
        cartRepository.deleteById(cartItemId);
    }
}
